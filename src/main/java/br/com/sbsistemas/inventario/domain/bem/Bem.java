package br.com.sbsistemas.inventario.domain.bem;

import br.com.sbsistemas.inventario.domain.categoria.Categoria;
import br.com.sbsistemas.inventario.domain.departamento.Departamento;
import br.com.sbsistemas.inventario.domain.tenant.Tenant;
import br.com.sbsistemas.inventario.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bem", uniqueConstraints = @UniqueConstraint(name = "uq_bem_placa_tenant", columnNames = { "placa",
        "tenant_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false, length = 30)
    private String placa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(nullable = false, length = 255)
    private String descricao;

    @Column(name = "valor_aquisicao", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorAquisicao;

    @Column(length = 150)
    private String fornecedor;

    @Column(name = "numero_serie", length = 100)
    private String numeroSerie;

    @Column(name = "numero_nf", length = 50)
    private String numeroNf;

    @Column(name = "data_compra", nullable = false)
    private LocalDate dataCompra;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "departamento_id", nullable = false)
    private Departamento departamento;

    @Column(name = "descricao_local", length = 255)
    private String descricaoLocal;

    @Column(length = 150)
    private String responsavel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoBem estado;

    @Column(name = "ultima_revisao")
    private LocalDate ultimaRevisao;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(nullable = false)
    private boolean ativo;

    @Column(name = "data_baixa")
    private LocalDate dataBaixa;

    @Column(name = "motivo_baixa", length = 255)
    private String motivoBaixa;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;
}
