package br.com.sbsistemas.inventario.shared;

/**
 * Armazena o id e o nome do usuário autenticado no contexto da thread corrente.
 * Populado pelo filtro de segurança após a validação do JWT.
 */
public final class UserContext {

    private static final ThreadLocal<Long> CURRENT_USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_USER_NAME = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(Long userId, String userName) {
        CURRENT_USER_ID.set(userId);
        CURRENT_USER_NAME.set(userName);
    }

    public static Long getId() {
        return CURRENT_USER_ID.get();
    }

    public static String getNome() {
        return CURRENT_USER_NAME.get();
    }

    /**
     * Deve ser chamado no finally do filtro para evitar vazamento entre requests.
     */
    public static void clear() {
        CURRENT_USER_ID.remove();
        CURRENT_USER_NAME.remove();
    }
}
