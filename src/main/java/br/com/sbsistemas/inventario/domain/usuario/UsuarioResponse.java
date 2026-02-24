package br.com.sbsistemas.inventario.domain.usuario;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String perfil,
        boolean ativo,
        LocalDateTime ultimoLogin) {
    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getPerfil().name(), u.isAtivo(),
                u.getUltimoLogin());
    }
}
