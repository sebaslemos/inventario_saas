package br.com.sbsistemas.inventario.domain.usuario;

public record LoginResponse(String token, String nome, String email, String perfil, Long tenantId, String tenantNome) {
}
