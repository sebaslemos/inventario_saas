package br.com.sbsistemas.inventario.domain.categoria;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CategoriaRequest(
        @NotBlank @Size(max = 100) String nome,

        @NotNull @DecimalMin("0.0001") @DecimalMax("1.0000") BigDecimal taxaAnual,

        @NotNull @Min(1) @Max(100) Integer vidaUtilAnos,

        @NotNull @Min(1) @Max(100) Integer revisarEmAnos) {
}
