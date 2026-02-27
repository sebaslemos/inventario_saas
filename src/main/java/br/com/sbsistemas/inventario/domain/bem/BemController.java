package br.com.sbsistemas.inventario.domain.bem;

import br.com.sbsistemas.inventario.shared.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bens")
@RequiredArgsConstructor
@Tag(name = "Bens (Imobilizado)")
@SecurityRequirement(name = "bearerAuth")
public class BemController {

    private final BemService bemService;

    @GetMapping
    @Operation(summary = "Lista bens com filtros e paginação")
    public PageResponse<BemResponse> listar(
            @Parameter(description = "ID da categoria") @RequestParam(required = false) Long categoriaId,
            @Parameter(description = "ID do departamento") @RequestParam(required = false) Long departamentoId,
            @Parameter(description = "Estado do bem") @RequestParam(required = false) EstadoBem estado,
            @Parameter(description = "Busca por placa, descrição ou responsável") @RequestParam(required = false) String busca,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "placa") String sort) {

        return bemService.listar(categoriaId, departamentoId, estado, busca, PageRequest.of(page, size, Sort.by(sort)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca bem por ID")
    public BemResponse buscar(@PathVariable Long id) {
        return bemService.buscarPorId(id);
    }

    @GetMapping("/{id}/historico")
    @Operation(summary = "Histórico de movimentações do bem")
    public List<BemHistoricoResponse> historico(@PathVariable Long id) {
        return bemService.historico(id).stream().map(BemHistoricoResponse::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastra novo bem")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public BemResponse criar(@Valid @RequestBody BemRequest request) {
        return bemService.criar(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza bem")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public BemResponse atualizar(@PathVariable Long id, @Valid @RequestBody BemRequest request) {
        return bemService.atualizar(id, request);
    }

    @PostMapping("/{id}/revisao")
    @Operation(summary = "Registra revisão do bem")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public BemResponse registrarRevisao(@PathVariable Long id, @Valid @RequestBody RevisaoRequest request) {
        return bemService.registrarRevisao(id, request.data(), request.observacao());
    }

    @PostMapping("/{id}/baixa")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Baixa (descarta/vende) o bem")
    @PreAuthorize("hasRole('ADMIN')")
    public void baixar(@PathVariable Long id, @Valid @RequestBody BaixaRequest request) {
        bemService.baixar(id, request.data(), request.motivo());
    }

    // ─── DTOs locais ───────────────────────────────────────────────────────────

    public record RevisaoRequest(
            @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            String observacao) {
    }

    public record BaixaRequest(
            @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @NotBlank String motivo) {
    }
}
