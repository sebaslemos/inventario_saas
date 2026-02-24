package br.com.sbsistemas.inventario.domain.departamento;

import br.com.sbsistemas.inventario.domain.tenant.Tenant;
import br.com.sbsistemas.inventario.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "departamento", uniqueConstraints = @UniqueConstraint(name = "uq_departamento_nome_tenant", columnNames = {
        "nome", "tenant_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Departamento extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private boolean ativo;
}
