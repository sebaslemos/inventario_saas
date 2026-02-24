import { useState } from "react";
import { Link } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import api from "../lib/api";
import type {
    BemResponse,
    PageResponse,
    CategoriaResponse,
    DepartamentoResponse,
    EstadoBem,
} from "../lib/types";
import { formatCurrency, estadoLabel, estadoBadgeClass } from "../lib/format";
import { useAuth } from "../contexts/AuthContext";
import { BemDetailDrawer } from "../components/BemDetailDrawer";
import {
    Plus,
    Search,
    ChevronLeft,
    ChevronRight,
    FileDown,
} from "lucide-react";

const ESTADOS: EstadoBem[] = ["BOM", "MEDIO", "RUIM", "TROCAR"];

export function BensPage() {
    const { canEdit } = useAuth();

    // — Filtros —
    const [categoriaId, setCategoriaId] = useState("");
    const [departamentoId, setDepartamentoId] = useState("");
    const [estado, setEstado] = useState("");
    const [busca, setBusca] = useState("");
    const [page, setPage] = useState(0);
    const size = 20;

    // — Drawer —
    const [selectedBemId, setSelectedBemId] = useState<number | null>(null);

    // — Dados auxiliares —
    const { data: categorias } = useQuery({
        queryKey: ["categorias-all"],
        queryFn: () =>
            api.get<CategoriaResponse[]>("/categorias").then((r) => r.data),
    });

    const { data: departamentos } = useQuery({
        queryKey: ["departamentos-all"],
        queryFn: () =>
            api
                .get<DepartamentoResponse[]>("/departamentos")
                .then((r) => r.data),
    });

    // — Bens —
    const { data, isLoading } = useQuery({
        queryKey: ["bens", categoriaId, departamentoId, estado, busca, page],
        queryFn: () =>
            api
                .get<PageResponse<BemResponse>>("/bens", {
                    params: {
                        ...(categoriaId && { categoriaId }),
                        ...(departamentoId && { departamentoId }),
                        ...(estado && { estado }),
                        ...(busca && { busca }),
                        page,
                        size,
                    },
                })
                .then((r) => r.data),
    });

    const handleExport = () => {
        window.open("/api/excel/export", "_blank");
    };

    return (
        <div>
            <div className="flex items-center justify-between mb-5">
                <h1 className="text-xl font-bold text-gray-900">Bens</h1>
                <div className="flex gap-2">
                    <button
                        onClick={handleExport}
                        className="btn-secondary flex items-center gap-1.5"
                    >
                        <FileDown size={16} /> Exportar
                    </button>
                    {canEdit && (
                        <Link
                            to="/bens/novo"
                            className="btn-primary flex items-center gap-1.5"
                        >
                            <Plus size={16} /> Novo Bem
                        </Link>
                    )}
                </div>
            </div>

            {/* ── Filtros ── */}
            <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-4 mb-5">
                <select
                    className="input"
                    value={categoriaId}
                    onChange={(e) => {
                        setCategoriaId(e.target.value);
                        setPage(0);
                    }}
                >
                    <option value="">Todas as categorias</option>
                    {categorias?.map((c) => (
                        <option key={c.id} value={c.id}>
                            {c.nome}
                        </option>
                    ))}
                </select>

                <select
                    className="input"
                    value={departamentoId}
                    onChange={(e) => {
                        setDepartamentoId(e.target.value);
                        setPage(0);
                    }}
                >
                    <option value="">Todos os departamentos</option>
                    {departamentos?.map((d) => (
                        <option key={d.id} value={d.id}>
                            {d.nome}
                        </option>
                    ))}
                </select>

                <select
                    className="input"
                    value={estado}
                    onChange={(e) => {
                        setEstado(e.target.value);
                        setPage(0);
                    }}
                >
                    <option value="">Todos os estados</option>
                    {ESTADOS.map((e) => (
                        <option key={e} value={e}>
                            {estadoLabel(e)}
                        </option>
                    ))}
                </select>

                <div className="relative">
                    <Search
                        size={16}
                        className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"
                    />
                    <input
                        type="text"
                        placeholder="Buscar placa, descrição ou responsável"
                        className="input pl-9"
                        value={busca}
                        onChange={(e) => {
                            setBusca(e.target.value);
                            setPage(0);
                        }}
                    />
                </div>
            </div>

            {/* ── Tabela ── */}
            <div className="rounded-xl bg-white shadow-sm border border-gray-100 overflow-hidden">
                <div className="overflow-x-auto">
                    <table className="min-w-full text-sm">
                        <thead>
                            <tr className="border-b border-gray-100 bg-gray-50/60 text-left text-xs font-medium uppercase tracking-wider text-gray-500">
                                <th className="px-4 py-3">Placa</th>
                                <th className="px-4 py-3">Descrição</th>
                                <th className="px-4 py-3 hidden md:table-cell">
                                    Departamento
                                </th>
                                <th className="px-4 py-3 hidden lg:table-cell">
                                    Responsável
                                </th>
                                <th className="px-4 py-3">Estado</th>
                                <th className="px-4 py-3 text-right">
                                    Valor Atual
                                </th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-50">
                            {isLoading ? (
                                Array.from({ length: 8 }).map((_, i) => (
                                    <tr key={i} className="animate-pulse">
                                        {Array.from({ length: 6 }).map(
                                            (_, j) => (
                                                <td
                                                    key={j}
                                                    className="px-4 py-3"
                                                >
                                                    <div className="h-4 rounded bg-gray-200 w-3/4" />
                                                </td>
                                            ),
                                        )}
                                    </tr>
                                ))
                            ) : !data || data.content.length === 0 ? (
                                <tr>
                                    <td
                                        colSpan={6}
                                        className="px-4 py-12 text-center text-gray-400"
                                    >
                                        Nenhum bem encontrado.
                                    </td>
                                </tr>
                            ) : (
                                data.content.map((bem) => (
                                    <tr
                                        key={bem.id}
                                        onClick={() => setSelectedBemId(bem.id)}
                                        className="cursor-pointer transition-colors hover:bg-blue-50/50"
                                    >
                                        <td className="px-4 py-3 font-medium text-gray-900 whitespace-nowrap">
                                            {bem.placa}
                                        </td>
                                        <td className="px-4 py-3 text-gray-700 max-w-xs truncate">
                                            {bem.descricao}
                                        </td>
                                        <td className="px-4 py-3 text-gray-600 hidden md:table-cell">
                                            {bem.departamentoNome}
                                        </td>
                                        <td className="px-4 py-3 text-gray-600 hidden lg:table-cell">
                                            {bem.responsavel}
                                        </td>
                                        <td className="px-4 py-3">
                                            <span
                                                className={estadoBadgeClass(
                                                    bem.estado,
                                                )}
                                            >
                                                {estadoLabel(bem.estado)}
                                            </span>
                                        </td>
                                        <td className="px-4 py-3 text-right text-gray-700 whitespace-nowrap">
                                            {formatCurrency(bem.valorAtual)}
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>

                {/* ── Paginação ── */}
                {data && data.totalPages > 1 && (
                    <div className="flex items-center justify-between border-t border-gray-100 px-4 py-3">
                        <span className="text-xs text-gray-500">
                            {data.totalElements}{" "}
                            {data.totalElements === 1 ? "item" : "itens"}
                        </span>
                        <div className="flex items-center gap-1">
                            <button
                                disabled={page === 0}
                                onClick={() => setPage((p) => p - 1)}
                                className="btn-ghost btn-sm"
                            >
                                <ChevronLeft size={16} />
                            </button>
                            <span className="text-sm text-gray-700 px-2">
                                {page + 1} / {data.totalPages}
                            </span>
                            <button
                                disabled={data.last}
                                onClick={() => setPage((p) => p + 1)}
                                className="btn-ghost btn-sm"
                            >
                                <ChevronRight size={16} />
                            </button>
                        </div>
                    </div>
                )}
            </div>

            {/* ── Detail Drawer ── */}
            {selectedBemId != null && (
                <BemDetailDrawer
                    bemId={selectedBemId}
                    onClose={() => setSelectedBemId(null)}
                />
            )}
        </div>
    );
}
