package br.com.sbsistemas.inventario.infra.excel;

import br.com.sbsistemas.inventario.domain.bem.*;
import br.com.sbsistemas.inventario.domain.categoria.Categoria;
import br.com.sbsistemas.inventario.domain.categoria.CategoriaRepository;
import br.com.sbsistemas.inventario.domain.departamento.Departamento;
import br.com.sbsistemas.inventario.domain.departamento.DepartamentoRepository;
import br.com.sbsistemas.inventario.domain.tenant.TenantRepository;
import br.com.sbsistemas.inventario.shared.TenantContext;
import br.com.sbsistemas.inventario.shared.util.DepreciacaoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelService {

    private final BemRepository bemRepository;
    private final CategoriaRepository categoriaRepository;
    private final DepartamentoRepository departamentoRepository;
    private final TenantRepository tenantRepository;
    private final BemHistoricoRepository historicoRepository;

    // ─── EXPORT ─────────────────────────────────────────────────────────────────

    public byte[] exportarBens() throws IOException {
        Long tenantId = TenantContext.get();
        var bens = bemRepository.filtrar(tenantId, null, null, null, null, Pageable.unpaged()).getContent();

        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Imobilizado");
            CellStyle headerStyle = criarEstiloCabecalho(wb);

            // Cabeçalho
            String[] headers = { "Placa", "Categoria", "Descrição do bem", "Valor de Aquisição", "Fornecedor", "Série",
                    "NF", "Data da compra", "Departamento", "Descrição do local", "Responsável", "Estado do bem",
                    "Última revisão", "Próxima revisão", "Idade (anos)", "Vida útil (anos)", "Trocar em (anos)",
                    "Valor atual (R$)" };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 5000);
            }

            // Dados
            int rowNum = 1;
            for (BemResponse bem : bens.stream().map(BemResponse::from).toList()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(bem.placa());
                row.createCell(1).setCellValue(bem.categoriaNome());
                row.createCell(2).setCellValue(bem.descricao());
                row.createCell(3).setCellValue(bem.valorAquisicao().doubleValue());
                row.createCell(4).setCellValue(nvl(bem.fornecedor()));
                row.createCell(5).setCellValue(nvl(bem.numeroSerie()));
                row.createCell(6).setCellValue(nvl(bem.numeroNf()));
                row.createCell(7).setCellValue(bem.dataCompra().toString());
                row.createCell(8).setCellValue(bem.departamentoNome());
                row.createCell(9).setCellValue(nvl(bem.descricaoLocal()));
                row.createCell(10).setCellValue(nvl(bem.responsavel()));
                row.createCell(11).setCellValue(bem.estado());
                row.createCell(12).setCellValue(bem.ultimaRevisao() != null ? bem.ultimaRevisao().toString() : "");
                row.createCell(13).setCellValue(bem.proximaRevisao() != null ? bem.proximaRevisao().toString() : "");
                row.createCell(14).setCellValue(bem.idadeEmAnos());
                row.createCell(15).setCellValue(bem.vidaUtilAnos());
                row.createCell(16).setCellValue(bem.anosRestantesParaTroca());
                row.createCell(17).setCellValue(bem.valorAtual() != null ? bem.valorAtual().doubleValue() : 0);
            }

            wb.write(bos);
            return bos.toByteArray();
        }
    }

    // ─── IMPORT ─────────────────────────────────────────────────────────────────

    @Transactional
    public ImportResult importarBens(MultipartFile arquivo) throws IOException {
        Long tenantId = TenantContext.get();
        var tenant = tenantRepository.getReferenceById(tenantId);
        List<String> erros = new ArrayList<>();
        int importados = 0;
        int ignorados = 0;

        try (Workbook wb = WorkbookFactory.create(arquivo.getInputStream())) {
            Sheet sheet = encontrarAbaImobilizado(wb);
            if (sheet == null) {
                return new ImportResult(0, 0, List.of("Aba 'Imobilizado' não encontrada no arquivo."));
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isLinhaVazia(row))
                    continue;

                try {
                    String placa = getString(row, 0);
                    if (placa == null || placa.isBlank()) {
                        ignorados++;
                        continue;
                    }

                    // Ignora duplicatas silenciosamente
                    if (bemRepository.existsByPlacaAndTenantId(placa.toUpperCase(), tenantId)) {
                        ignorados++;
                        log.debug("Placa {} já existe, linha {} ignorada", placa, i + 1);
                        continue;
                    }

                    String nomeCategoria = getString(row, 1);
                    String descricao = getString(row, 2);
                    BigDecimal valor = getBigDecimal(row, 3);
                    String fornecedor = getString(row, 4);
                    String serie = getString(row, 5);
                    String nf = getString(row, 6);
                    LocalDate dataCompra = getDate(row, 7);
                    String nomeDepartamento = getString(row, 8);
                    String descLocal = getString(row, 9);
                    String responsavel = getString(row, 10);
                    String estadoStr = getString(row, 11);
                    LocalDate ultimaRevisao = getDate(row, 12);

                    if (descricao == null || valor == null || dataCompra == null) {
                        erros.add("Linha %d: campos obrigatórios ausentes (descrição, valor ou data)".formatted(i + 1));
                        continue;
                    }

                    Categoria categoria = resolverCategoria(nomeCategoria, tenantId);
                    if (categoria == null) {
                        erros.add("Linha %d: categoria '%s' não encontrada".formatted(i + 1, nomeCategoria));
                        continue;
                    }

                    Departamento departamento = resolverDepartamento(nomeDepartamento, tenantId);
                    if (departamento == null) {
                        erros.add("Linha %d: departamento '%s' não encontrado".formatted(i + 1, nomeDepartamento));
                        continue;
                    }

                    EstadoBem estado = parseEstado(estadoStr);

                    Bem bem = Bem.builder()
                            .tenant(tenant)
                            .placa(placa.toUpperCase().trim())
                            .categoria(categoria)
                            .descricao(descricao.trim())
                            .valorAquisicao(valor)
                            .fornecedor(fornecedor)
                            .numeroSerie(serie)
                            .numeroNf(nf)
                            .dataCompra(dataCompra)
                            .departamento(departamento)
                            .descricaoLocal(descLocal)
                            .responsavel(responsavel)
                            .estado(estado)
                            .ultimaRevisao(ultimaRevisao)
                            .ativo(true)
                            .build();

                    bem = bemRepository.save(bem);
                    historicoRepository.save(BemHistorico.builder()
                            .tenant(tenant)
                            .bem(bem)
                            .tipo(BemHistorico.TipoEvento.CRIACAO)
                            .descricao("Importado da planilha Excel")
                            .dataEvento(LocalDate.now())
                            .build());
                    importados++;

                } catch (Exception e) {
                    erros.add("Linha %d: %s".formatted(i + 1, e.getMessage()));
                }
            }
        }

        return new ImportResult(importados, ignorados, erros);
    }

    // ─── helpers ────────────────────────────────────────────────────────────────

    private Sheet encontrarAbaImobilizado(Workbook wb) {
        Sheet s = wb.getSheet("Imobilizado");
        if (s == null)
            s = wb.getSheetAt(0);
        return s;
    }

    private boolean isLinhaVazia(Row row) {
        for (int i = 0; i < 4; i++) {
            Cell c = row.getCell(i);
            if (c != null && c.getCellType() != CellType.BLANK)
                return false;
        }
        return true;
    }

    private String getString(Row row, int col) {
        Cell c = row.getCell(col);
        if (c == null)
            return null;
        return switch (c.getCellType()) {
        case STRING -> c.getStringCellValue().trim();
        case NUMERIC -> String.valueOf((long) c.getNumericCellValue());
        case FORMULA -> c.getCachedFormulaResultType() == CellType.STRING ? c.getStringCellValue()
                : String.valueOf(c.getNumericCellValue());
        default -> null;
        };
    }

    private BigDecimal getBigDecimal(Row row, int col) {
        Cell c = row.getCell(col);
        if (c == null)
            return null;
        if (c.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(c.getNumericCellValue());
        }
        try {
            String s = getString(row, col);
            return s != null ? new BigDecimal(s.replace(",", ".")) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDate getDate(Row row, int col) {
        Cell c = row.getCell(col);
        if (c == null)
            return null;
        if (c.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(c)) {
            Date d = c.getDateCellValue();
            return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        if (c.getCellType() == CellType.STRING) {
            try {
                return LocalDate.parse(c.getStringCellValue().trim());
            } catch (DateTimeParseException e) {
                return null;
            }
        }
        return null;
    }

    private Categoria resolverCategoria(String nome, Long tenantId) {
        if (nome == null || nome.isBlank())
            return null;
        return categoriaRepository.findAllByTenantIdAndAtivoTrue(tenantId)
                .stream()
                .filter(c -> c.getNome().equalsIgnoreCase(nome.trim()))
                .findFirst()
                .orElse(null);
    }

    private Departamento resolverDepartamento(String nome, Long tenantId) {
        if (nome == null || nome.isBlank())
            return null;
        return departamentoRepository.findAllByTenantIdAndAtivoTrue(tenantId)
                .stream()
                .filter(d -> d.getNome().equalsIgnoreCase(nome.trim()))
                .findFirst()
                .orElse(null);
    }

    private EstadoBem parseEstado(String s) {
        if (s == null)
            return EstadoBem.BOM;
        return switch (s.trim().toUpperCase()) {
        case "MÉDIO", "MEDIO" -> EstadoBem.MEDIO;
        case "RUIM" -> EstadoBem.RUIM;
        case "TROCAR" -> EstadoBem.TROCAR;
        default -> EstadoBem.BOM;
        };
    }

    private CellStyle criarEstiloCabecalho(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private String nvl(String s) {
        return s != null ? s : "";
    }

    public record ImportResult(int importados, int ignorados, List<String> erros) {
    }
}
