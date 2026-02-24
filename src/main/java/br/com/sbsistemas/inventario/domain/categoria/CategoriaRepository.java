package br.com.sbsistemas.inventario.domain.categoria;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findAllByTenantIdAndAtivoTrue(Long tenantId);

    Optional<Categoria> findByIdAndTenantId(Long id, Long tenantId);

    boolean existsByNomeAndTenantId(String nome, Long tenantId);
}
