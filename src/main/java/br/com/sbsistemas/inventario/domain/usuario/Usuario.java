package br.com.sbsistemas.inventario.domain.usuario;

import br.com.sbsistemas.inventario.domain.tenant.Tenant;
import br.com.sbsistemas.inventario.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario", uniqueConstraints = @UniqueConstraint(name = "uq_usuario_email_tenant", columnNames = {
        "email", "tenant_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 200)
    private String email;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Perfil perfil;

    @Column(nullable = false)
    private boolean ativo;

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;

    public enum Perfil {
        ADMIN, GESTOR, USUARIO
    }
}
