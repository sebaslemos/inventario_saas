package br.com.sbsistemas.inventario.domain.bem;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de criação/atualização de bem.
 *
 * Campos obrigatórios: placa, categoriaId, descricao, valorAquisicao,
 * dataCompra, departamentoId, responsavel, estado.
 *
 * Campos opcionais: fornecedor, numeroSerie, numeroNf, descricaoLocal,
 * ultimaRevisao, observacoes.
 */
@Schema(description = "Dados do bem (ativo imobilizado)")
public record BemRequest(

                // ── Obrigatórios ─────────────────────────────────────────────────────

                @Schema(description = "Tag patrimonial (ex: X001)", example = "X001") @NotBlank @Size(max = 30) String placa,

                @Schema(description = "ID da categoria de depreciação") @NotNull Long categoriaId,

                @Schema(description = "Descrição do bem", example = "Notebook Dell Inspiron") @NotBlank @Size(max = 255) String descricao,

                @Schema(description = "Valor pago na aquisição", example = "2500.00") @NotNull @DecimalMin("0.01") BigDecimal valorAquisicao,

                @Schema(description = "Data da compra / nota fiscal") @NotNull LocalDate dataCompra,

                @Schema(description = "ID do departamento onde o bem está alocado") @NotNull Long departamentoId,

                @Schema(description = "Pessoa responsável pela guarda do bem", example = "Carlos Silva") @NotBlank @Size(max = 150) String responsavel,

                @Schema(description = "Estado de conservação do bem") @NotNull EstadoBem estado,

                // ── Opcionais ─────────────────────────────────────────────────────────

                @Schema(description = "Nome do fornecedor (opcional)", example = "DELL Brasil") @Size(max = 150) String fornecedor,

                @Schema(description = "Número de série do fabricante (opcional)") @Size(max = 100) String numeroSerie,

                @Schema(description = "Número da nota fiscal de compra (opcional)") @Size(max = 50) String numeroNf,

                @Schema(description = "Localização física detalhada (opcional)", example = "Sala 02 — Mesa 4") @Size(max = 255) String descricaoLocal,

                @Schema(description = "Data da última revisão/manutenção realizada (opcional)") LocalDate ultimaRevisao,

                @Schema(description = "Observações livres sobre o bem (opcional)") String observacoes) {
}
