package br.com.sbsistemas.inventario.domain.usuario;

import br.com.sbsistemas.inventario.domain.tenant.Tenant;
import br.com.sbsistemas.inventario.domain.tenant.TenantRepository;
import br.com.sbsistemas.inventario.shared.TenantContext;
import br.com.sbsistemas.inventario.shared.exception.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final TenantRepository tenantRepository;

    @GetMapping
    @Operation(summary = "Lista usuários da organização")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UsuarioResponse> listar() {
        return usuarioService.listarTodos().stream().map(UsuarioResponse::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria novo usuário")
    @PreAuthorize("hasRole('ADMIN')")
    public UsuarioResponse criar(@Valid @RequestBody UsuarioRequest request) {
        Tenant tenant = tenantRepository.findById(TenantContext.get())
                .orElseThrow(() -> new NotFoundException("Tenant", TenantContext.get()));

        Usuario usuario = Usuario.builder()
                .tenant(tenant)
                .nome(request.nome())
                .email(request.email())
                .senhaHash(request.senha()) // UsuarioService vai encodar
                .perfil(request.perfil())
                .build();

        return UsuarioResponse.from(usuarioService.criar(usuario));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desativa usuário")
    @PreAuthorize("hasRole('ADMIN')")
    public void desativar(@PathVariable Long id) {
        usuarioService.desativar(id);
    }

    @PatchMapping("/{id}/senha")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Altera a própria senha")
    public ResponseEntity<Void> alterarSenha(@PathVariable Long id, @Valid @RequestBody AlterarSenhaRequest request) {
        usuarioService.alterarSenha(id, request.senhaAtual(), request.novaSenha());
        return ResponseEntity.noContent().build();
    }

    // ─── DTOs locais ───────────────────────────────────────────────────────────

    public record UsuarioRequest(
            @NotBlank @Size(max = 150) String nome,
            @NotBlank @Email @Size(max = 200) String email,
            @NotBlank @Size(min = 8) String senha,
            @NotNull Usuario.Perfil perfil) {
    }

    public record AlterarSenhaRequest(@NotBlank String senhaAtual, @NotBlank @Size(min = 8) String novaSenha) {
    }
}
