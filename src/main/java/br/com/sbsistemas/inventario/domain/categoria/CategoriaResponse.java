package br.com.sbsistemas.inventario.domain.categoria;

import java.math.BigDecimal;

public record CategoriaResponse(
        Long id,
        String nome,
        BigDecimal taxaAnual,
        int vidaUtilAnos,
        int revisarEmAnos,
        boolean ativo) {
    public static CategoriaResponse from(Categoria c) {
        return new CategoriaResponse(c.getId(), c.getNome(), c.getTaxaAnual(), c.getVidaUtilAnos(),
                c.getRevisarEmAnos(), c.isAtivo());
    }
}
