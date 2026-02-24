package br.com.sbsistemas.inventario.domain.categoria;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorias")
@SecurityRequirement(name = "bearerAuth")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    @Operation(summary = "Lista categorias ativas")
    public List<CategoriaResponse> listar() {
        return categoriaService.listarAtivas().stream().map(CategoriaResponse::from).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca categoria por ID")
    public CategoriaResponse buscar(@PathVariable Long id) {
        return CategoriaResponse.from(categoriaService.buscarPorId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria categoria")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public CategoriaResponse criar(@Valid @RequestBody CategoriaRequest request) {
        return CategoriaResponse.from(categoriaService.criar(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza categoria")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public CategoriaResponse atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        return CategoriaResponse.from(categoriaService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desativa categoria")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public void desativar(@PathVariable Long id) {
        categoriaService.desativar(id);
    }
}
