package br.com.sbsistemas.inventario.domain.departamento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {

    List<Departamento> findAllByTenantIdAndAtivoTrue(Long tenantId);

    Optional<Departamento> findByIdAndTenantId(Long id, Long tenantId);

    boolean existsByNomeAndTenantId(String nome, Long tenantId);
}
