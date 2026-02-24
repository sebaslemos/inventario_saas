package br.com.sbsistemas.inventario.domain.bem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BemRepository extends JpaRepository<Bem, Long>, JpaSpecificationExecutor<Bem> {

    Optional<Bem> findByIdAndTenantId(Long id, Long tenantId);

    boolean existsByPlacaAndTenantId(String placa, Long tenantId);

    /** Listagem paginada com filtros opcionais. */
    @Query("""
            SELECT b FROM Bem b
            WHERE b.tenant.id = :tenantId
              AND b.ativo = true
              AND (:categoriaId IS NULL OR b.categoria.id = :categoriaId)
              AND (:departamentoId IS NULL OR b.departamento.id = :departamentoId)
              AND (:estado IS NULL OR b.estado = :estado)
              AND (:busca IS NULL OR LOWER(b.descricao) LIKE LOWER(CONCAT('%',:busca,'%'))
                                  OR LOWER(b.placa) LIKE LOWER(CONCAT('%',:busca,'%'))
                                  OR LOWER(b.responsavel) LIKE LOWER(CONCAT('%',:busca,'%')))
            """)
    Page<Bem> filtrar(@Param("tenantId") Long tenantId, @Param("categoriaId") Long categoriaId,
            @Param("departamentoId") Long departamentoId, @Param("estado") EstadoBem estado,
            @Param("busca") String busca, Pageable pageable);

    /** Conta bens por estado para o dashboard. */
    @Query("""
            SELECT b.estado, COUNT(b)
            FROM Bem b
            WHERE b.tenant.id = :tenantId AND b.ativo = true
            GROUP BY b.estado
            """)
    java.util.List<Object[]> contarPorEstado(@Param("tenantId") Long tenantId);

    @Query("""
            SELECT COALESCE(SUM(b.valorAquisicao), 0)
            FROM Bem b
            WHERE b.tenant.id = :tenantId AND b.ativo = true
            """)
    java.math.BigDecimal somarValorAquisicao(@Param("tenantId") Long tenantId);
}
