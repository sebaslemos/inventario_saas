/* ── Types matching the backend API contracts ── */

// ── Auth ──
export interface LoginRequest {
    email: string;
    senha: string;
}

export interface LoginResponse {
    token: string;
    nome: string;
    email: string;
    perfil: "ADMIN" | "GESTOR" | "USUARIO";
    tenantId: number;
    tenantNome: string;
}

export interface User {
    nome: string;
    email: string;
    perfil: "ADMIN" | "GESTOR" | "USUARIO";
    tenantId: number;
    tenantNome: string;
}

// ── Paginação ──
export interface PageResponse<T> {
    content: T[];
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
    last: boolean;
}

// ── Bem (Ativo Imobilizado) ──
export type EstadoBem = "BOM" | "MEDIO" | "RUIM" | "TROCAR";

export interface BemResponse {
    id: number;
    placa: string;
    categoriaId: number;
    categoriaNome: string;
    descricao: string;
    valorAquisicao: number;
    fornecedor: string | null;
    numeroSerie: string | null;
    numeroNf: string | null;
    dataCompra: string;
    departamentoId: number;
    departamentoNome: string;
    descricaoLocal: string | null;
    responsavel: string;
    estado: EstadoBem;
    ultimaRevisao: string | null;
    observacoes: string | null;
    ativo: boolean;
    // Campos calculados
    idadeEmAnos: number;
    valorAtual: number;
    proximaRevisao: string | null;
    dataTroca: string | null;
    anosRestantesParaTroca: number;
    vidaUtilAnos: number;
}

export interface BemRequest {
    placa: string;
    categoriaId: number;
    descricao: string;
    valorAquisicao: number;
    dataCompra: string;
    departamentoId: number;
    responsavel: string;
    estado: EstadoBem;
    fornecedor?: string;
    numeroSerie?: string;
    numeroNf?: string;
    descricaoLocal?: string;
    ultimaRevisao?: string;
    observacoes?: string;
}

export interface BemHistoricoResponse {
    id: number;
    tipo: string;
    descricao: string;
    dataEvento: string;
    usuarioNome: string | null;
    registradoEm: string;
}

// ── Categoria ──
export interface CategoriaResponse {
    id: number;
    nome: string;
    taxaAnual: number;
    vidaUtilAnos: number;
    revisarEmAnos: number;
    ativo: boolean;
}

export interface CategoriaRequest {
    nome: string;
    taxaAnual: number;
    vidaUtilAnos: number;
    revisarEmAnos: number;
}

// ── Departamento ──
export interface DepartamentoResponse {
    id: number;
    nome: string;
    ativo: boolean;
}

// ── Usuário ──
export interface UsuarioResponse {
    id: number;
    nome: string;
    email: string;
    perfil: string;
    ativo: boolean;
    ultimoLogin: string | null;
}

export interface UsuarioRequest {
    nome: string;
    email: string;
    senha: string;
    perfil: "ADMIN" | "GESTOR" | "USUARIO";
}

// ── Dashboard ──
export interface DashboardResponse {
    totalBens: number;
    valorTotalAquisicao: number;
    bensPorEstado: Record<string, number>;
}

// ── Error ──
export interface ErrorResponse {
    status: number;
    error: string;
    message: string;
    timestamp: string;
    fields?: { field: string; message: string }[];
}
