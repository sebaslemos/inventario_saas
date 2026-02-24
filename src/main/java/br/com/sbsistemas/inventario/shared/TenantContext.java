package br.com.sbsistemas.inventario.shared;

/**
 * Armazena o tenant_id do usuário autenticado no contexto da thread corrente.
 * Populado pelo filtro de segurança após a validação do JWT.
 */
public final class TenantContext {

    private static final ThreadLocal<Long> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void set(Long tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static Long get() {
        return CURRENT_TENANT.get();
    }

    /**
     * Deve ser chamado no finally do filtro para evitar vazamento entre requests.
     */
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
