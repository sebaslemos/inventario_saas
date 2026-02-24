package br.com.sbsistemas.inventario.domain.usuario;

import br.com.sbsistemas.inventario.shared.TenantContext;
import br.com.sbsistemas.inventario.shared.exception.BusinessException;
import br.com.sbsistemas.inventario.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAllByTenantIdAndAtivoTrue(TenantContext.get());
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .filter(u -> u.getTenant().getId().equals(TenantContext.get()))
                .orElseThrow(() -> new NotFoundException("Usuário", id));
    }

    @Transactional
    public void alterarSenha(Long id, String senhaAtual, String novaSenha) {
        Usuario usuario = buscarPorId(id);
        if (!passwordEncoder.matches(senhaAtual, usuario.getSenhaHash())) {
            throw new BusinessException("Senha atual incorreta");
        }
        usuario.setSenhaHash(passwordEncoder.encode(novaSenha));
    }

    @Transactional
    public void desativar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(false);
    }

    /**
     * Cria um usuário novo no tenant atual (apenas ADMIN pode usar este método via
     * controller).
     */
    @Transactional
    public Usuario criar(Usuario usuario) {
        if (usuarioRepository.existsByEmailAndTenantId(usuario.getEmail(), usuario.getTenant().getId())) {
            throw new BusinessException("E-mail já cadastrado nesta organização");
        }
        usuario.setSenhaHash(passwordEncoder.encode(usuario.getSenhaHash()));
        usuario.setAtivo(true);
        return usuarioRepository.save(usuario);
    }
}
