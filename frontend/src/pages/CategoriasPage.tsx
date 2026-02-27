import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import api from "../lib/api";
import type { CategoriaResponse, CategoriaRequest } from "../lib/types";
import { formatPercent } from "../lib/format";
import { useAuth } from "../contexts/AuthContext";
import { Plus, Pencil, X, Save, Loader2 } from "lucide-react";

const empty: CategoriaRequest = {
    nome: "",
    taxaAnual: 10,
    vidaUtilAnos: 10,
    revisarEmAnos: 2,
};

export function CategoriasPage() {
    const { canEdit, canDelete } = useAuth();
    const queryClient = useQueryClient();

    const [editing, setEditing] = useState<number | "new" | null>(null);
    const [form, setForm] = useState<CategoriaRequest>(empty);

    const { data: categorias, isLoading } = useQuery({
        queryKey: ["categorias-all"],
        queryFn: () =>
            api.get<CategoriaResponse[]>("/categorias").then((r) => r.data),
    });

    const save = useMutation({
        mutationFn: (data: CategoriaRequest) =>
            editing === "new"
                ? api.post("/categorias", data)
                : api.put(`/categorias/${editing}`, data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["categorias-all"] });
            cancel();
        },
    });

    const remove = useMutation({
        mutationFn: (id: number) => api.delete(`/categorias/${id}`),
        onSuccess: () =>
            queryClient.invalidateQueries({ queryKey: ["categorias-all"] }),
    });

    const startEdit = (cat: CategoriaResponse) => {
        setEditing(cat.id);
        setForm({
            nome: cat.nome,
            taxaAnual: parseFloat((cat.taxaAnual * 100).toFixed(10)),
            vidaUtilAnos: cat.vidaUtilAnos,
            revisarEmAnos: cat.revisarEmAnos,
        });
    };

    const startNew = () => {
        setEditing("new");
        setForm(empty);
    };

    const cancel = () => {
        setEditing(null);
        setForm(empty);
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        save.mutate({ ...form, taxaAnual: form.taxaAnual / 100 });
    };

    return (
        <div className="max-w-3xl">
            <div className="flex items-center justify-between mb-5">
                <h1 className="text-xl font-bold text-gray-900">Categorias</h1>
                {canEdit && editing === null && (
                    <button
                        onClick={startNew}
                        className="btn-primary flex items-center gap-1.5"
                    >
                        <Plus size={16} /> Nova Categoria
                    </button>
                )}
            </div>

            <div className="rounded-xl bg-white shadow-sm border border-gray-100 overflow-hidden">
                <table className="min-w-full text-sm">
                    <thead>
                        <tr className="border-b border-gray-100 bg-gray-50/60 text-left text-xs font-medium uppercase tracking-wider text-gray-500">
                            <th className="px-4 py-3">Nome</th>
                            <th className="px-4 py-3 text-right">
                                Taxa Anual (%)
                            </th>
                            <th className="px-4 py-3 text-right">
                                Vida Útil (anos)
                            </th>
                            <th className="px-4 py-3 text-right">
                                Revisar em (anos)
                            </th>
                            {canEdit && <th className="px-4 py-3 w-24" />}
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-50">
                        {/* Inline form for NEW */}
                        {editing === "new" && (
                            <InlineForm
                                form={form}
                                setForm={setForm}
                                onSubmit={handleSubmit}
                                onCancel={cancel}
                                isPending={save.isPending}
                            />
                        )}

                        {isLoading
                            ? Array.from({ length: 4 }).map((_, i) => (
                                  <tr key={i} className="animate-pulse">
                                      {[1, 2, 3, 4].map((j) => (
                                          <td key={j} className="px-4 py-3">
                                              <div className="h-4 rounded bg-gray-200 w-3/4" />
                                          </td>
                                      ))}
                                  </tr>
                              ))
                            : categorias?.map((cat) =>
                                  editing === cat.id ? (
                                      <InlineForm
                                          key={cat.id}
                                          form={form}
                                          setForm={setForm}
                                          onSubmit={handleSubmit}
                                          onCancel={cancel}
                                          isPending={save.isPending}
                                      />
                                  ) : (
                                      <tr
                                          key={cat.id}
                                          className="hover:bg-gray-50/50"
                                      >
                                          <td className="px-4 py-3 font-medium text-gray-900">
                                              {cat.nome}
                                          </td>
                                          <td className="px-4 py-3 text-right text-gray-700">
                                              {formatPercent(cat.taxaAnual)}
                                          </td>
                                          <td className="px-4 py-3 text-right text-gray-700">
                                              {cat.vidaUtilAnos}
                                          </td>
                                          <td className="px-4 py-3 text-right text-gray-700">
                                              {cat.revisarEmAnos}
                                          </td>
                                          {canEdit && (
                                              <td className="px-4 py-3 text-right">
                                                  <div className="flex justify-end gap-1">
                                                      <button
                                                          onClick={() =>
                                                              startEdit(cat)
                                                          }
                                                          className="btn-ghost btn-sm"
                                                          title="Editar"
                                                      >
                                                          <Pencil size={14} />
                                                      </button>
                                                      {canDelete && (
                                                          <button
                                                              onClick={() => {
                                                                  if (
                                                                      confirm(
                                                                          "Remover esta categoria?",
                                                                      )
                                                                  )
                                                                      remove.mutate(
                                                                          cat.id,
                                                                      );
                                                              }}
                                                              className="btn-ghost btn-sm text-red-500 hover:text-red-700"
                                                              title="Remover"
                                                          >
                                                              <X size={14} />
                                                          </button>
                                                      )}
                                                  </div>
                                              </td>
                                          )}
                                      </tr>
                                  ),
                              )}

                        {!isLoading &&
                            (!categorias || categorias.length === 0) &&
                            editing !== "new" && (
                                <tr>
                                    <td
                                        colSpan={5}
                                        className="px-4 py-12 text-center text-gray-400"
                                    >
                                        Nenhuma categoria cadastrada.
                                    </td>
                                </tr>
                            )}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

function InlineForm({
    form,
    setForm,
    onSubmit,
    onCancel,
    isPending,
}: {
    form: CategoriaRequest;
    setForm: React.Dispatch<React.SetStateAction<CategoriaRequest>>;
    onSubmit: (e: React.FormEvent) => void;
    onCancel: () => void;
    isPending: boolean;
}) {
    return (
        <tr className="bg-blue-50/40" key="inline-form">
            <td className="px-4 py-2">
                <input
                    className="input"
                    placeholder="Nome"
                    required
                    value={form.nome}
                    onChange={(e) =>
                        setForm((f) => ({ ...f, nome: e.target.value }))
                    }
                />
            </td>
            <td className="px-4 py-2">
                <input
                    className="input text-right"
                    type="number"
                    step="0.01"
                    min="0"
                    required
                    value={form.taxaAnual}
                    onChange={(e) =>
                        setForm((f) => ({
                            ...f,
                            taxaAnual: parseFloat(e.target.value) || 0,
                        }))
                    }
                />
            </td>
            <td className="px-4 py-2">
                <input
                    className="input text-right"
                    type="number"
                    min="1"
                    required
                    value={form.vidaUtilAnos}
                    onChange={(e) =>
                        setForm((f) => ({
                            ...f,
                            vidaUtilAnos: parseInt(e.target.value) || 1,
                        }))
                    }
                />
            </td>
            <td className="px-4 py-2">
                <input
                    className="input text-right"
                    type="number"
                    min="1"
                    required
                    value={form.revisarEmAnos}
                    onChange={(e) =>
                        setForm((f) => ({
                            ...f,
                            revisarEmAnos: parseInt(e.target.value) || 1,
                        }))
                    }
                />
            </td>
            <td className="px-4 py-2 text-right">
                <form onSubmit={onSubmit} className="flex justify-end gap-1">
                    <button
                        type="submit"
                        disabled={isPending}
                        className="btn-ghost btn-sm text-green-600"
                    >
                        {isPending ? (
                            <Loader2 size={14} className="animate-spin" />
                        ) : (
                            <Save size={14} />
                        )}
                    </button>
                    <button
                        type="button"
                        onClick={onCancel}
                        className="btn-ghost btn-sm text-gray-400"
                    >
                        <X size={14} />
                    </button>
                </form>
            </td>
        </tr>
    );
}
