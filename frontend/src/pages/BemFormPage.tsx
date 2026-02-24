import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import api from "../lib/api";
import type {
    BemRequest,
    BemResponse,
    CategoriaResponse,
    DepartamentoResponse,
    EstadoBem,
    ErrorResponse,
} from "../lib/types";
import { ArrowLeft, Save, Loader2 } from "lucide-react";

const ESTADOS: { value: EstadoBem; label: string }[] = [
    { value: "BOM", label: "Bom" },
    { value: "MEDIO", label: "Médio" },
    { value: "RUIM", label: "Ruim" },
    { value: "TROCAR", label: "Trocar" },
];

const empty: BemRequest = {
    placa: "",
    categoriaId: 0,
    descricao: "",
    valorAquisicao: 0,
    dataCompra: "",
    departamentoId: 0,
    responsavel: "",
    estado: "BOM",
};

export function BemFormPage() {
    const { id } = useParams<{ id: string }>();
    const isEditing = Boolean(id);
    const navigate = useNavigate();
    const queryClient = useQueryClient();

    const [form, setForm] = useState<BemRequest>(empty);
    const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

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

    // — Carregar bem existente —
    const { data: existing } = useQuery({
        queryKey: ["bem", id],
        queryFn: () => api.get<BemResponse>(`/bens/${id}`).then((r) => r.data),
        enabled: isEditing,
    });

    useEffect(() => {
        if (existing) {
            setForm({
                placa: existing.placa,
                categoriaId: existing.categoriaId,
                descricao: existing.descricao,
                valorAquisicao: existing.valorAquisicao,
                dataCompra: existing.dataCompra,
                departamentoId: existing.departamentoId,
                responsavel: existing.responsavel,
                estado: existing.estado,
                fornecedor: existing.fornecedor ?? undefined,
                numeroSerie: existing.numeroSerie ?? undefined,
                numeroNf: existing.numeroNf ?? undefined,
                descricaoLocal: existing.descricaoLocal ?? undefined,
                ultimaRevisao: existing.ultimaRevisao ?? undefined,
                observacoes: existing.observacoes ?? undefined,
            });
        }
    }, [existing]);

    // — Mutation —
    const mutation = useMutation({
        mutationFn: (data: BemRequest) =>
            isEditing ? api.put(`/bens/${id}`, data) : api.post("/bens", data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["bens"] });
            navigate("/bens");
        },
        onError: (err: any) => {
            const resp = err.response?.data as ErrorResponse | undefined;
            if (resp?.fields) {
                const map: Record<string, string> = {};
                resp.fields.forEach((f) => (map[f.field] = f.message));
                setFieldErrors(map);
            }
        },
    });

    const set = (field: keyof BemRequest, value: any) => {
        setForm((prev) => ({ ...prev, [field]: value }));
        setFieldErrors((prev) => {
            const next = { ...prev };
            delete next[field];
            return next;
        });
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        setFieldErrors({});
        mutation.mutate(form);
    };

    return (
        <div className="max-w-3xl">
            <button
                onClick={() => navigate("/bens")}
                className="btn-ghost btn-sm flex items-center gap-1 mb-4"
            >
                <ArrowLeft size={16} /> Voltar
            </button>

            <h1 className="text-xl font-bold text-gray-900 mb-6">
                {isEditing ? "Editar Bem" : "Novo Bem"}
            </h1>

            <form onSubmit={handleSubmit} className="space-y-8">
                {/* ── Informações Principais ── */}
                <fieldset className="rounded-xl bg-white p-5 shadow-sm border border-gray-100 space-y-4">
                    <legend className="text-xs font-semibold uppercase tracking-wider text-gray-500 px-1">
                        Informações Principais
                    </legend>

                    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                        <InputField
                            label="Placa *"
                            value={form.placa}
                            onChange={(v) => set("placa", v)}
                            error={fieldErrors["placa"]}
                            disabled={isEditing}
                        />
                        <SelectField
                            label="Categoria *"
                            value={form.categoriaId || ""}
                            onChange={(v) => set("categoriaId", Number(v))}
                            error={fieldErrors["categoriaId"]}
                            options={
                                categorias?.map((c) => ({
                                    value: c.id,
                                    label: c.nome,
                                })) ?? []
                            }
                            placeholder="Selecione…"
                        />
                        <div className="sm:col-span-2">
                            <InputField
                                label="Descrição *"
                                value={form.descricao}
                                onChange={(v) => set("descricao", v)}
                                error={fieldErrors["descricao"]}
                            />
                        </div>
                        <SelectField
                            label="Departamento *"
                            value={form.departamentoId || ""}
                            onChange={(v) => set("departamentoId", Number(v))}
                            error={fieldErrors["departamentoId"]}
                            options={
                                departamentos?.map((d) => ({
                                    value: d.id,
                                    label: d.nome,
                                })) ?? []
                            }
                            placeholder="Selecione…"
                        />
                        <InputField
                            label="Responsável *"
                            value={form.responsavel}
                            onChange={(v) => set("responsavel", v)}
                            error={fieldErrors["responsavel"]}
                        />
                        <SelectField
                            label="Estado *"
                            value={form.estado}
                            onChange={(v) => set("estado", v as EstadoBem)}
                            options={ESTADOS}
                        />
                        <InputField
                            label="Localização"
                            value={form.descricaoLocal ?? ""}
                            onChange={(v) =>
                                set("descricaoLocal", v || undefined)
                            }
                        />
                    </div>
                </fieldset>

                {/* ── Financeiro ── */}
                <fieldset className="rounded-xl bg-white p-5 shadow-sm border border-gray-100 space-y-4">
                    <legend className="text-xs font-semibold uppercase tracking-wider text-gray-500 px-1">
                        Financeiro
                    </legend>

                    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                        <InputField
                            label="Valor de Aquisição (R$) *"
                            type="number"
                            step="0.01"
                            min="0"
                            value={form.valorAquisicao}
                            onChange={(v) =>
                                set("valorAquisicao", parseFloat(v) || 0)
                            }
                            error={fieldErrors["valorAquisicao"]}
                        />
                        <InputField
                            label="Data de Compra *"
                            type="date"
                            value={form.dataCompra}
                            onChange={(v) => set("dataCompra", v)}
                            error={fieldErrors["dataCompra"]}
                        />
                        <InputField
                            label="Fornecedor"
                            value={form.fornecedor ?? ""}
                            onChange={(v) => set("fornecedor", v || undefined)}
                        />
                        <InputField
                            label="Nota Fiscal"
                            value={form.numeroNf ?? ""}
                            onChange={(v) => set("numeroNf", v || undefined)}
                        />
                    </div>
                </fieldset>

                {/* ── Detalhes Adicionais ── */}
                <fieldset className="rounded-xl bg-white p-5 shadow-sm border border-gray-100 space-y-4">
                    <legend className="text-xs font-semibold uppercase tracking-wider text-gray-500 px-1">
                        Detalhes Adicionais
                    </legend>

                    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                        <InputField
                            label="Nº Série"
                            value={form.numeroSerie ?? ""}
                            onChange={(v) => set("numeroSerie", v || undefined)}
                        />
                        <InputField
                            label="Última Revisão"
                            type="date"
                            value={form.ultimaRevisao ?? ""}
                            onChange={(v) =>
                                set("ultimaRevisao", v || undefined)
                            }
                        />
                        <div className="sm:col-span-2">
                            <label className="label">Observações</label>
                            <textarea
                                className="input min-h-[80px]"
                                value={form.observacoes ?? ""}
                                onChange={(e) =>
                                    set(
                                        "observacoes",
                                        e.target.value || undefined,
                                    )
                                }
                            />
                        </div>
                    </div>
                </fieldset>

                {/* ── Submit ── */}
                <div className="flex justify-end gap-3">
                    <button
                        type="button"
                        onClick={() => navigate("/bens")}
                        className="btn-secondary"
                    >
                        Cancelar
                    </button>
                    <button
                        type="submit"
                        disabled={mutation.isPending}
                        className="btn-primary flex items-center gap-1.5"
                    >
                        {mutation.isPending ? (
                            <Loader2 size={16} className="animate-spin" />
                        ) : (
                            <Save size={16} />
                        )}
                        {isEditing ? "Salvar" : "Cadastrar"}
                    </button>
                </div>
            </form>
        </div>
    );
}

/* ── Reusable field components ── */

function InputField({
    label,
    value,
    onChange,
    error,
    type = "text",
    ...props
}: {
    label: string;
    value: string | number;
    onChange: (v: string) => void;
    error?: string;
    type?: string;
    [k: string]: any;
}) {
    return (
        <div>
            <label className="label">{label}</label>
            <input
                type={type}
                className={`input ${error ? "border-red-400 focus:ring-red-400" : ""}`}
                value={value}
                onChange={(e) => onChange(e.target.value)}
                {...props}
            />
            {error && <p className="text-xs text-red-500 mt-1">{error}</p>}
        </div>
    );
}

function SelectField({
    label,
    value,
    onChange,
    options,
    placeholder,
    error,
}: {
    label: string;
    value: string | number;
    onChange: (v: string) => void;
    options: { value: string | number; label: string }[];
    placeholder?: string;
    error?: string;
}) {
    return (
        <div>
            <label className="label">{label}</label>
            <select
                className={`input ${error ? "border-red-400 focus:ring-red-400" : ""}`}
                value={value}
                onChange={(e) => onChange(e.target.value)}
            >
                {placeholder && <option value="">{placeholder}</option>}
                {options.map((o) => (
                    <option key={o.value} value={o.value}>
                        {o.label}
                    </option>
                ))}
            </select>
            {error && <p className="text-xs text-red-500 mt-1">{error}</p>}
        </div>
    );
}
