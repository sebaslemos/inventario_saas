package br.com.sbsistemas.inventario.domain.categoria;

import br.com.sbsistemas.inventario.domain.tenant.Tenant;
import br.com.sbsistemas.inventario.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "categoria", uniqueConstraints = @UniqueConstraint(name = "uq_categoria_nome_tenant", columnNames = {
        "nome", "tenant_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(name = "taxa_anual", nullable = false, precision = 5, scale = 4)
    private BigDecimal taxaAnual;

    @Column(name = "vida_util_anos", nullable = false)
    private int vidaUtilAnos;

    @Column(name = "revisar_em_anos", nullable = false)
    private int revisarEmAnos;

    @Column(nullable = false)
    private boolean ativo;
}
