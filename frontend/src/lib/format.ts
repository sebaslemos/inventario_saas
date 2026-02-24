/** Utilitários de formatação para o frontend */

export function formatCurrency(value: number | null | undefined): string {
    if (value == null) return "—";
    return value.toLocaleString("pt-BR", {
        style: "currency",
        currency: "BRL",
    });
}

export function formatDate(iso: string | null | undefined): string {
    if (!iso) return "—";
    return new Date(iso + "T00:00:00").toLocaleDateString("pt-BR");
}

export function formatDateTime(iso: string | null | undefined): string {
    if (!iso) return "—";
    return new Date(iso).toLocaleString("pt-BR");
}

export function estadoLabel(estado: string): string {
    const map: Record<string, string> = {
        BOM: "Bom",
        MEDIO: "Médio",
        RUIM: "Ruim",
        TROCAR: "Trocar",
    };
    return map[estado] ?? estado;
}

export function estadoBadgeClass(estado: string): string {
    const map: Record<string, string> = {
        BOM: "badge-bom",
        MEDIO: "badge-medio",
        RUIM: "badge-ruim",
        TROCAR: "badge-trocar",
    };
    return `badge ${map[estado] ?? ""}`;
}

export function tipoEventoLabel(tipo: string): string {
    const map: Record<string, string> = {
        CRIACAO: "Criação",
        ALTERACAO: "Alteração",
        TRANSFERENCIA: "Transferência",
        REVISAO: "Revisão",
        BAIXA: "Baixa",
    };
    return map[tipo] ?? tipo;
}

export function perfilLabel(p: string): string {
    return (
        { ADMIN: "Administrador", GESTOR: "Gestor", USUARIO: "Usuário" }[p] ?? p
    );
}

/** Recebe valor decimal (ex: 0.01) e exibe como percentagem (ex: "1,00%") */
export function formatPercent(value: number | null | undefined): string {
    if (value == null) return "—";
    return (
        (value * 100).toLocaleString("pt-BR", {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2,
        }) + "%"
    );
}
