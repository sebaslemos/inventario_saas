package br.com.sbsistemas.inventario.infra.excel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
@Tag(name = "Excel (Import/Export)")
@SecurityRequirement(name = "bearerAuth")
public class ExcelController {

    private final ExcelService excelService;

    @GetMapping("/exportar")
    @Operation(summary = "Exporta todos os bens para Excel (.xlsx)")
    public ResponseEntity<byte[]> exportar() throws IOException {
        byte[] data = excelService.exportarBens();
        String filename = "inventario-" + LocalDate.now() + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    @PostMapping(value = "/importar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Importa bens da planilha Excel (.xlsx ou .xlsm)")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ExcelService.ImportResult importar(@RequestParam("arquivo") MultipartFile arquivo) throws IOException {
        return excelService.importarBens(arquivo);
    }
}
