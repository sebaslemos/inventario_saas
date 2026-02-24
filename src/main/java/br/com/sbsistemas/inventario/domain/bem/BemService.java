package br.com.sbsistemas.inventario.domain.bem;

import br.com.sbsistemas.inventario.domain.categoria.Categoria;
import br.com.sbsistemas.inventario.domain.categoria.CategoriaRepository;
import br.com.sbsistemas.inventario.domain.departamento.DepartamentoRepository;
import br.com.sbsistemas.inventario.domain.tenant.TenantRepository;
import br.com.sbsistemas.inventario.shared.TenantContext;
import br.com.sbsistemas.inventario.shared.UserContext;
import br.com.sbsistemas.inventario.shared.dto.PageResponse;
import br.com.sbsistemas.inventario.shared.exception.BusinessException;
import br.com.sbsistemas.inventario.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BemService {

    private final BemRepository bemRepository;
    private final BemHistoricoRepository historicoRepository;
    private final CategoriaRepository categoriaRepository;
    private final DepartamentoRepository departamentoRepository;
    private final TenantRepository tenantRepository;

    public PageResponse<BemResponse> listar(Long categoriaId, Long departamentoId, EstadoBem estado, String busca,
            Pageable pageable) {
        return PageResponse
                .from(bemRepository.filtrar(TenantContext.get(), categoriaId, departamentoId, estado, busca, pageable)
                        .map(BemResponse::from));
    }

    public BemResponse buscarPorId(Long id) {
        return BemResponse.from(buscarEntidade(id));
    }

    public List<BemHistorico> historico(Long bemId) {
        // Valida que o bem pertence ao tenant
        buscarEntidade(bemId);
        return historicoRepository.findAllByBemIdOrderByDataEventoDesc(bemId);
    }

    @Transactional
    public BemResponse criar(BemRequest request) {
        Long tenantId = TenantContext.get();

        if (bemRepository.existsByPlacaAndTenantId(request.placa(), tenantId)) {
            throw new BusinessException("Já existe um bem com esta placa: " + request.placa());
        }

        var categoria = categoriaRepository.findByIdAndTenantId(request.categoriaId(), tenantId)
                .orElseThrow(() -> new NotFoundException("Categoria", request.categoriaId()));

        var departamento = departamentoRepository.findByIdAndTenantId(request.departamentoId(), tenantId)
                .orElseThrow(() -> new NotFoundException("Departamento", request.departamentoId()));

        var tenant = tenantRepository.getReferenceById(tenantId);

        Bem bem = Bem.builder()
                .tenant(tenant)
                .placa(request.placa().toUpperCase().trim())
                .categoria(categoria)
                .descricao(request.descricao().trim())
                .valorAquisicao(request.valorAquisicao())
                .fornecedor(request.fornecedor())
                .numeroSerie(request.numeroSerie())
                .numeroNf(request.numeroNf())
                .dataCompra(request.dataCompra())
                .departamento(departamento)
                .descricaoLocal(request.descricaoLocal())
                .responsavel(request.responsavel())
                .estado(request.estado())
                .ultimaRevisao(request.ultimaRevisao())
                .observacoes(request.observacoes())
                .ativo(true)
                .createdBy(currentUserId())
                .updatedBy(currentUserId())
                .build();

        bem = bemRepository.save(bem);
        registrarHistorico(bem,
                BemHistorico.TipoEvento.CRIACAO,
                "Bem cadastrado no sistema. Valor de aquisição: R$ " + bem.getValorAquisicao());

        return BemResponse.from(bem);
    }

    @Transactional
    public BemResponse atualizar(Long id, BemRequest request) {
        Long tenantId = TenantContext.get();
        Bem bem = buscarEntidade(id);

        // Verifica placa duplicada somente se mudou
        if (!bem.getPlaca().equalsIgnoreCase(request.placa())
                && bemRepository.existsByPlacaAndTenantId(request.placa(), tenantId)) {
            throw new BusinessException("Já existe um bem com esta placa: " + request.placa());
        }

        var categoria = categoriaRepository.findByIdAndTenantId(request.categoriaId(), tenantId)
                .orElseThrow(() -> new NotFoundException("Categoria", request.categoriaId()));
        var departamento = departamentoRepository.findByIdAndTenantId(request.departamentoId(), tenantId)
                .orElseThrow(() -> new NotFoundException("Departamento", request.departamentoId()));

        // ── Detecta o que mudou ANTES de aplicar as alterações ──────────────
        boolean houveTranferencia = !bem.getDepartamento().getId().equals(departamento.getId());
        String departamentoAnterior = bem.getDepartamento().getNome();

        List<String> camposAlterados = detectarCamposAlterados(bem, request, categoria);
        boolean houveAlteracao = !camposAlterados.isEmpty();

        // ── Aplica as mutações ───────────────────────────────────────────────
        bem.setPlaca(request.placa().toUpperCase().trim());
        bem.setCategoria(categoria);
        bem.setDescricao(request.descricao().trim());
        bem.setValorAquisicao(request.valorAquisicao());
        bem.setFornecedor(request.fornecedor());
        bem.setNumeroSerie(request.numeroSerie());
        bem.setNumeroNf(request.numeroNf());
        bem.setDataCompra(request.dataCompra());
        bem.setDepartamento(departamento);
        bem.setDescricaoLocal(request.descricaoLocal());
        bem.setResponsavel(request.responsavel());
        bem.setEstado(request.estado());
        bem.setUltimaRevisao(request.ultimaRevisao());
        bem.setObservacoes(request.observacoes());
        bem.setUpdatedBy(currentUserId());

        bem = bemRepository.save(bem);

        // ── Registra eventos de forma independente ───────────────────────────
        // Ambos podem ocorrer na mesma operação — são eventos distintos no histórico
        if (houveTranferencia) {
            registrarHistorico(bem,
                    BemHistorico.TipoEvento.TRANSFERENCIA,
                    "Bem transferido de '%s' para '%s'.".formatted(departamentoAnterior, departamento.getNome()));
        }
        if (houveAlteracao) {
            registrarHistorico(bem,
                    BemHistorico.TipoEvento.ALTERACAO,
                    "Campos alterados: " + String.join(", ", camposAlterados) + ".");
        }

        return BemResponse.from(bem);
    }

    /**
     * Compara os campos relevantes do bem com os valores do request e retorna os
     * nomes dos campos que foram modificados.
     */
    private List<String> detectarCamposAlterados(Bem bem, BemRequest request, Categoria novaCategoria) {
        List<String> alterados = new java.util.ArrayList<>();

        if (!bem.getPlaca().equalsIgnoreCase(request.placa().trim()))
            alterados.add("placa");
        if (!bem.getCategoria().getId().equals(novaCategoria.getId()))
            alterados.add("categoria");
        if (!bem.getDescricao().equalsIgnoreCase(request.descricao().trim()))
            alterados.add("descrição");
        if (bem.getValorAquisicao().compareTo(request.valorAquisicao()) != 0)
            alterados.add("valor de aquisição");
        if (!java.util.Objects.equals(bem.getFornecedor(), request.fornecedor()))
            alterados.add("fornecedor");
        if (!java.util.Objects.equals(bem.getNumeroSerie(), request.numeroSerie()))
            alterados.add("número de série");
        if (!java.util.Objects.equals(bem.getNumeroNf(), request.numeroNf()))
            alterados.add("nota fiscal");
        if (!bem.getDataCompra().equals(request.dataCompra()))
            alterados.add("data de compra");
        if (!java.util.Objects.equals(bem.getDescricaoLocal(), request.descricaoLocal()))
            alterados.add("descrição do local");
        if (!bem.getResponsavel().equals(request.responsavel()))
            alterados.add("responsável");
        if (bem.getEstado() != request.estado())
            alterados.add("estado");
        if (!java.util.Objects.equals(bem.getUltimaRevisao(), request.ultimaRevisao()))
            alterados.add("última revisão");
        if (!java.util.Objects.equals(bem.getObservacoes(), request.observacoes()))
            alterados.add("observações");

        return alterados;
    }

    @Transactional
    public BemResponse registrarRevisao(Long id, LocalDate dataRevisao, String observacao) {
        Bem bem = buscarEntidade(id);
        bem.setUltimaRevisao(dataRevisao);
        bem.setUpdatedBy(currentUserId());
        bemRepository.save(bem);
        registrarHistorico(bem,
                BemHistorico.TipoEvento.REVISAO,
                "Revisão registrada em " + dataRevisao + (observacao != null ? ". " + observacao : ""));
        return BemResponse.from(bem);
    }

    @Transactional
    public void baixar(Long id, LocalDate dataBaixa, String motivo) {
        Bem bem = buscarEntidade(id);
        if (!bem.isAtivo()) {
            throw new BusinessException("Este bem já foi baixado");
        }
        bem.setAtivo(false);
        bem.setDataBaixa(dataBaixa);
        bem.setMotivoBaixa(motivo);
        bem.setUpdatedBy(currentUserId());
        bemRepository.save(bem);
        registrarHistorico(bem, BemHistorico.TipoEvento.BAIXA, "Bem baixado em " + dataBaixa + ". Motivo: " + motivo);
    }

    // ─── helpers ────────────────────────────────────────────────────────────────

    private Bem buscarEntidade(Long id) {
        return bemRepository.findByIdAndTenantId(id, TenantContext.get())
                .orElseThrow(() -> new NotFoundException("Bem", id));
    }

    private void registrarHistorico(Bem bem, BemHistorico.TipoEvento tipo, String descricao) {
        historicoRepository.save(BemHistorico.builder()
                .tenant(bem.getTenant())
                .bem(bem)
                .tipo(tipo)
                .descricao(descricao)
                .dataEvento(LocalDate.now())
                .usuarioId(UserContext.getId())
                .usuarioNome(UserContext.getNome())
                .build());
    }

    private Long currentUserId() {
        return UserContext.getId();
    }
}
