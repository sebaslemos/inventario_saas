package br.com.sbsistemas.inventario.domain.usuario;

import br.com.sbsistemas.inventario.infra.security.JwtProvider;
import br.com.sbsistemas.inventario.shared.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    @Transactional
    @Operation(summary = "Realiza login e retorna token JWT")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("E-mail ou senha inválidos"));

        if (!usuario.isAtivo()) {
            throw new BusinessException("Usuário inativo. Contate o administrador.");
        }

        if (!passwordEncoder.matches(request.senha(), usuario.getSenhaHash())) {
            throw new BusinessException("E-mail ou senha inválidos");
        }

        // Atualiza último login (sem lançar exceção se falhar)
        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(usuario);

        String token = jwtProvider.generate(usuario.getEmail(),
                usuario.getTenant().getId(),
                usuario.getPerfil().name(),
                usuario.getId(),
                usuario.getNome());

        return ResponseEntity.ok(new LoginResponse(token, usuario.getNome(), usuario.getEmail(),
                usuario.getPerfil().name(), usuario.getTenant().getId(), usuario.getTenant().getNome()));
    }
}
