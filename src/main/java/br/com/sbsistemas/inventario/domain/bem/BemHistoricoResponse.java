package br.com.sbsistemas.inventario.domain.bem;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BemHistoricoResponse(
        Long id,
        String tipo,
        String descricao,
        LocalDate dataEvento,
        String usuarioNome,
        LocalDateTime registradoEm) {
    public static BemHistoricoResponse from(BemHistorico h) {
        return new BemHistoricoResponse(h.getId(), h.getTipo().name(), h.getDescricao(), h.getDataEvento(),
                h.getUsuarioNome(), h.getCreatedAt());
    }
}
