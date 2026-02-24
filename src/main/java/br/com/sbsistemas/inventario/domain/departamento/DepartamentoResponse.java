package br.com.sbsistemas.inventario.domain.departamento;

public record DepartamentoResponse(Long id, String nome, boolean ativo) {
    public static DepartamentoResponse from(Departamento d) {
        return new DepartamentoResponse(d.getId(), d.getNome(), d.isAtivo());
    }
}
