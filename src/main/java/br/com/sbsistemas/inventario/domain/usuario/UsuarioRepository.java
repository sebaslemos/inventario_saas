package br.com.sbsistemas.inventario.domain.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailAndTenantId(String email, Long tenantId);

    /**
     * Busca por email em qualquer tenant (usado no login antes de sabermos o
     * tenant)
     */
    Optional<Usuario> findByEmail(String email);

    List<Usuario> findAllByTenantIdAndAtivoTrue(Long tenantId);

    boolean existsByEmailAndTenantId(String email, Long tenantId);
}
