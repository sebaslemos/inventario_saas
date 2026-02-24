package br.com.sbsistemas.inventario.domain.bem;

import br.com.sbsistemas.inventario.shared.util.DepreciacaoUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public record BemResponse(
        Long id,
        String placa,
        Long categoriaId,
        String categoriaNome,
        String descricao,
        BigDecimal valorAquisicao,
        String fornecedor,
        String numeroSerie,
        String numeroNf,
        LocalDate dataCompra,
        Long departamentoId,
        String departamentoNome,
        String descricaoLocal,
        String responsavel,
        String estado,
        LocalDate ultimaRevisao,
        String observacoes,
        boolean ativo,

        // Campos calculados
        double idadeEmAnos,
        BigDecimal valorAtual,
        LocalDate proximaRevisao,
        LocalDate dataTroca,
        double anosRestantesParaTroca,
        int vidaUtilAnos) {
    public static BemResponse from(Bem bem) {
        DepreciacaoUtil.Calculado calc = DepreciacaoUtil.calcular(bem);
        return new BemResponse(bem.getId(), bem.getPlaca(), bem.getCategoria().getId(), bem.getCategoria().getNome(),
                bem.getDescricao(), bem.getValorAquisicao(), bem.getFornecedor(), bem.getNumeroSerie(),
                bem.getNumeroNf(), bem.getDataCompra(), bem.getDepartamento().getId(), bem.getDepartamento().getNome(),
                bem.getDescricaoLocal(), bem.getResponsavel(), bem.getEstado().name(), bem.getUltimaRevisao(),
                bem.getObservacoes(), bem.isAtivo(),

                BigDecimal.valueOf(calc.idadeEmAnos()).setScale(2, RoundingMode.HALF_UP).doubleValue(),
                calc.valorAtual(), calc.proximaRevisao(), calc.dataTroca(),
                BigDecimal.valueOf(calc.anosRestantesParaTroca()).setScale(2, RoundingMode.HALF_UP).doubleValue(),
                bem.getCategoria().getVidaUtilAnos());
    }
}
