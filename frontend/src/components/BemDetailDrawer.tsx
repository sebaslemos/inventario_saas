import { useQuery } from "@tanstack/react-query";
import { Link } from "react-router-dom";
import api from "../lib/api";
import type { BemResponse, BemHistoricoResponse } from "../lib/types";
import {
    formatCurrency,
    formatDate,
    formatDateTime,
    estadoLabel,
    estadoBadgeClass,
    tipoEventoLabel,
} from "../lib/format";
import { useAuth } from "../contexts/AuthContext";
import {
    X,
    Pencil,
    Clock,
    Tag,
    MapPin,
    DollarSign,
    Info,
    FileText,
} from "lucide-react";

interface Props {
    bemId: number;
    onClose: () => void;
}

export function BemDetailDrawer({ bemId, onClose }: Props) {
    const { canEdit } = useAuth();

    const { data: bem, isLoading } = useQuery({
        queryKey: ["bem", bemId],
        queryFn: () =>
            api.get<BemResponse>(`/bens/${bemId}`).then((r) => r.data),
    });

    const { data: historico } = useQuery({
        queryKey: ["bem-historico", bemId],
        queryFn: () =>
            api
                .get<BemHistoricoResponse[]>(`/bens/${bemId}/historico`)
                .then((r) => r.data),
    });

    return (
        <>
            {/* Backdrop */}
            <div className="fixed inset-0 bg-black/30 z-40" onClick={onClose} />

            {/* Panel */}
            <div className="fixed inset-y-0 right-0 z-50 w-full max-w-lg bg-white shadow-xl flex flex-col animate-slide-in">
                {/* Header */}
                <div className="flex items-center justify-between border-b border-gray-100 px-5 py-4">
                    <h2 className="text-base font-bold text-gray-900 truncate">
                        {isLoading ? "Carregando…" : bem?.placa}
                    </h2>
                    <div className="flex items-center gap-2">
                        {canEdit && bem && (
                            <Link
                                to={`/bens/${bem.id}/editar`}
                                className="btn-ghost btn-sm"
                                title="Editar"
                            >
                                <Pencil size={16} />
                            </Link>
                        )}
                        <button
                            onClick={onClose}
                            className="btn-ghost btn-sm"
                            title="Fechar"
                        >
                            <X size={18} />
                        </button>
                    </div>
                </div>

                {/* Body */}
                <div className="flex-1 overflow-y-auto px-5 py-4 space-y-6">
                    {isLoading || !bem ? (
                        <DrawerSkeleton />
                    ) : (
                        <>
                            {/* ── Informações Principais ── */}
                            <Section
                                icon={<Tag size={16} />}
                                title="Informações Principais"
                            >
                                <Field
                                    label="Descrição"
                                    value={bem.descricao}
                                />
                                <Field
                                    label="Categoria"
                                    value={bem.categoriaNome}
                                />
                                <Field
                                    label="Departamento"
                                    value={bem.departamentoNome}
                                />
                                <Field
                                    label="Responsável"
                                    value={bem.responsavel}
                                />
                                <Field
                                    label="Estado"
                                    value={
                                        <span
                                            className={estadoBadgeClass(
                                                bem.estado,
                                            )}
                                        >
                                            {estadoLabel(bem.estado)}
                                        </span>
                                    }
                                />
                                {bem.descricaoLocal && (
                                    <Field
                                        label="Localização"
                                        value={bem.descricaoLocal}
                                    />
                                )}
                            </Section>

                            {/* ── Valores e Depreciação ── */}
                            <Section
                                icon={<DollarSign size={16} />}
                                title="Valores e Depreciação"
                            >
                                <Field
                                    label="Valor de Aquisição"
                                    value={formatCurrency(bem.valorAquisicao)}
                                />
                                <Field
                                    label="Valor Atual"
                                    value={formatCurrency(bem.valorAtual)}
                                />
                                <Field
                                    label="Data de Compra"
                                    value={formatDate(bem.dataCompra)}
                                />
                                <Field
                                    label="Idade"
                                    value={`${bem.idadeEmAnos} ${bem.idadeEmAnos === 1 ? "ano" : "anos"}`}
                                />
                                <Field
                                    label="Vida Útil"
                                    value={`${bem.vidaUtilAnos} anos`}
                                />
                                {bem.dataTroca && (
                                    <Field
                                        label="Data de Troca Estimada"
                                        value={formatDate(bem.dataTroca)}
                                    />
                                )}
                                {bem.anosRestantesParaTroca > 0 && (
                                    <Field
                                        label="Anos Restantes"
                                        value={`${bem.anosRestantesParaTroca} ${bem.anosRestantesParaTroca === 1 ? "ano" : "anos"}`}
                                    />
                                )}
                            </Section>

                            {/* ── Detalhes Adicionais ── */}
                            {(bem.fornecedor ||
                                bem.numeroSerie ||
                                bem.numeroNf ||
                                bem.ultimaRevisao ||
                                bem.observacoes) && (
                                <Section
                                    icon={<Info size={16} />}
                                    title="Detalhes Adicionais"
                                >
                                    {bem.fornecedor && (
                                        <Field
                                            label="Fornecedor"
                                            value={bem.fornecedor}
                                        />
                                    )}
                                    {bem.numeroSerie && (
                                        <Field
                                            label="Nº Série"
                                            value={bem.numeroSerie}
                                        />
                                    )}
                                    {bem.numeroNf && (
                                        <Field
                                            label="Nota Fiscal"
                                            value={bem.numeroNf}
                                        />
                                    )}
                                    {bem.ultimaRevisao && (
                                        <Field
                                            label="Última Revisão"
                                            value={formatDate(
                                                bem.ultimaRevisao,
                                            )}
                                        />
                                    )}
                                    {bem.proximaRevisao && (
                                        <Field
                                            label="Próxima Revisão"
                                            value={formatDate(
                                                bem.proximaRevisao,
                                            )}
                                        />
                                    )}
                                    {bem.observacoes && (
                                        <Field
                                            label="Observações"
                                            value={bem.observacoes}
                                        />
                                    )}
                                </Section>
                            )}

                            {/* ── Histórico ── */}
                            <Section
                                icon={<Clock size={16} />}
                                title="Histórico"
                            >
                                {!historico || historico.length === 0 ? (
                                    <p className="text-sm text-gray-400">
                                        Nenhum evento registrado.
                                    </p>
                                ) : (
                                    <ol className="relative border-l border-gray-200 ml-2 space-y-4">
                                        {historico.map((h) => (
                                            <li key={h.id} className="ml-4">
                                                <div className="absolute -left-1.5 mt-1.5 h-3 w-3 rounded-full border border-white bg-blue-500" />
                                                <div className="text-xs text-gray-400 mb-0.5">
                                                    {formatDateTime(
                                                        h.dataEvento,
                                                    )}
                                                    {h.usuarioNome && (
                                                        <span className="ml-1 text-gray-500">
                                                            — {h.usuarioNome}
                                                        </span>
                                                    )}
                                                </div>
                                                <span className="inline-block text-xs font-medium bg-gray-100 text-gray-700 rounded px-1.5 py-0.5 mr-1">
                                                    {tipoEventoLabel(h.tipo)}
                                                </span>
                                                <span className="text-sm text-gray-700">
                                                    {h.descricao}
                                                </span>
                                            </li>
                                        ))}
                                    </ol>
                                )}
                            </Section>
                        </>
                    )}
                </div>
            </div>
        </>
    );
}

/* ── Helpers ── */

function Section({
    icon,
    title,
    children,
}: {
    icon: React.ReactNode;
    title: string;
    children: React.ReactNode;
}) {
    return (
        <div>
            <div className="flex items-center gap-1.5 mb-3">
                <span className="text-gray-400">{icon}</span>
                <h3 className="text-xs font-semibold uppercase tracking-wider text-gray-500">
                    {title}
                </h3>
            </div>
            <div className="grid grid-cols-2 gap-x-4 gap-y-2">{children}</div>
        </div>
    );
}

function Field({ label, value }: { label: string; value: React.ReactNode }) {
    return (
        <div className="col-span-2 sm:col-span-1">
            <dt className="text-xs text-gray-400">{label}</dt>
            <dd className="text-sm text-gray-800 mt-0.5">{value}</dd>
        </div>
    );
}

function DrawerSkeleton() {
    return (
        <div className="space-y-6 animate-pulse">
            {[1, 2, 3].map((i) => (
                <div key={i}>
                    <div className="h-4 w-32 bg-gray-200 rounded mb-3" />
                    <div className="grid grid-cols-2 gap-3">
                        {[1, 2, 3, 4].map((j) => (
                            <div key={j}>
                                <div className="h-3 w-16 bg-gray-200 rounded mb-1" />
                                <div className="h-4 w-24 bg-gray-200 rounded" />
                            </div>
                        ))}
                    </div>
                </div>
            ))}
        </div>
    );
}
