package br.com.sbsistemas.inventario.domain.bem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BemHistoricoRepository extends JpaRepository<BemHistorico, Long> {

    List<BemHistorico> findAllByBemIdOrderByDataEventoDesc(Long bemId);
}
