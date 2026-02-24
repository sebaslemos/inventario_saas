package br.com.sbsistemas.inventario.shared.util;

import br.com.sbsistemas.inventario.domain.bem.Bem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Utilitário de cálculos de depreciação linear (método da linha reta),
 * replicando a lógica da planilha original.
 */
public final class DepreciacaoUtil {

    private DepreciacaoUtil() {
    }

    /**
     * Calcula a idade do bem em anos decimais a partir da data de compra até hoje.
     */
    public static double idadeEmAnos(LocalDate dataCompra) {
        long dias = ChronoUnit.DAYS.between(dataCompra, LocalDate.now());
        return dias / 365.0;
    }

    /**
     * Calcula o valor atual pelo método linear: valorAtual = valorAquisicao * (1 -
     * taxaAnual * idadeEmAnos) Nunca retorna valor negativo (bem totalmente
     * depreciado vale 0).
     */
    public static BigDecimal valorAtual(BigDecimal valorAquisicao, BigDecimal taxaAnual, double idadeEmAnos) {
        BigDecimal depreciacao = taxaAnual.multiply(BigDecimal.valueOf(idadeEmAnos));
        BigDecimal fator = BigDecimal.ONE.subtract(depreciacao);
        if (fator.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return valorAquisicao.multiply(fator).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula a próxima data de revisão. Se nunca foi revisado, usa dataCompra +
     * revisarEmAnos. Se já foi revisado, usa ultimaRevisao + revisarEmAnos.
     */
    public static LocalDate proximaRevisao(LocalDate dataCompra, LocalDate ultimaRevisao, int revisarEmAnos) {
        LocalDate base = (ultimaRevisao != null) ? ultimaRevisao : dataCompra;
        return base.plusYears(revisarEmAnos);
    }

    /**
     * Data estimada para troca/reforma: dataCompra + vidaUtilAnos. Anos restantes é
     * negativo se já passou da vida útil.
     */
    public static LocalDate dataTroca(LocalDate dataCompra, int vidaUtilAnos) {
        return dataCompra.plusYears(vidaUtilAnos);
    }

    /**
     * Anos restantes até a troca (negativo = já deveria ter sido trocado).
     */
    public static double anosRestantesParaTroca(LocalDate dataCompra, int vidaUtilAnos) {
        LocalDate dataTroca = dataTroca(dataCompra, vidaUtilAnos);
        long dias = ChronoUnit.DAYS.between(LocalDate.now(), dataTroca);
        return dias / 365.0;
    }

    /** Agrega todos os cálculos para um bem. */
    public static Calculado calcular(Bem bem) {
        double idade = idadeEmAnos(bem.getDataCompra());
        BigDecimal taxaAnual = bem.getCategoria().getTaxaAnual();
        BigDecimal valAtual = valorAtual(bem.getValorAquisicao(), taxaAnual, idade);
        LocalDate proxRevisao = proximaRevisao(bem.getDataCompra(),
                bem.getUltimaRevisao(),
                bem.getCategoria().getRevisarEmAnos());
        LocalDate dataTrocaCalc = dataTroca(bem.getDataCompra(), bem.getCategoria().getVidaUtilAnos());
        double anosRestantes = anosRestantesParaTroca(bem.getDataCompra(), bem.getCategoria().getVidaUtilAnos());

        return new Calculado(idade, valAtual, proxRevisao, dataTrocaCalc, anosRestantes);
    }

    public record Calculado(
            double idadeEmAnos,
            BigDecimal valorAtual,
            LocalDate proximaRevisao,
            LocalDate dataTroca,
            double anosRestantesParaTroca) {
    }
}
