package br.com.sbsistemas.inventario.domain.tenant;

import br.com.sbsistemas.inventario.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tenant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant extends BaseEntity {

    @Column(nullable = false, unique = true, length = 60)
    private String slug;

    @Column(nullable = false, length = 150)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Plano plano;

    @Column(nullable = false)
    private boolean ativo;

    public enum Plano {
        FREE, PRO
    }
}
