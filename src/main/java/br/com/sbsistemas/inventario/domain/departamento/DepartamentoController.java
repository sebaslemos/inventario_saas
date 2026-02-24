package br.com.sbsistemas.inventario.domain.departamento;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departamentos")
@RequiredArgsConstructor
@Tag(name = "Departamentos")
@SecurityRequirement(name = "bearerAuth")
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    @GetMapping
    @Operation(summary = "Lista departamentos ativos")
    public List<DepartamentoResponse> listar() {
        return departamentoService.listarAtivos().stream().map(DepartamentoResponse::from).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca departamento por ID")
    public DepartamentoResponse buscar(@PathVariable Long id) {
        return DepartamentoResponse.from(departamentoService.buscarPorId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria departamento")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public DepartamentoResponse criar(@Valid @RequestBody DepartamentoRequest request) {
        return DepartamentoResponse.from(departamentoService.criar(request.nome()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza departamento")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public DepartamentoResponse atualizar(@PathVariable Long id, @Valid @RequestBody DepartamentoRequest request) {
        return DepartamentoResponse.from(departamentoService.atualizar(id, request.nome()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desativa departamento")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public void desativar(@PathVariable Long id) {
        departamentoService.desativar(id);
    }

    public record DepartamentoRequest(@NotBlank @Size(max = 100) String nome) {
    }
}
