package br.com.sbsistemas.inventario.domain.departamento;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/departamentos")
@RequiredArgsConstructor
@Tag(name = "Departamentos")
@SecurityRequirement(name = "bearerAuth")
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    @GetMapping
    @Operation(summary = "Lista departamentos ativos")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public List<DepartamentoResponse> listar() {
        return departamentoService.listarAtivos().stream().map(DepartamentoResponse::from).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public void desativar(@PathVariable Long id) {
        departamentoService.desativar(id);
    }

    public record DepartamentoRequest(@NotBlank @Size(max = 100) String nome) {
    }
}
