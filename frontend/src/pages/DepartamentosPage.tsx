import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import api from "../lib/api";
import type { DepartamentoResponse } from "../lib/types";
import { useAuth } from "../contexts/AuthContext";
import { Plus, Pencil, X, Save, Loader2 } from "lucide-react";

export function DepartamentosPage() {
    const { canEdit } = useAuth();
    const queryClient = useQueryClient();

    const [editing, setEditing] = useState<number | "new" | null>(null);
    const [nome, setNome] = useState("");

    const { data: departamentos, isLoading } = useQuery({
        queryKey: ["departamentos-all"],
        queryFn: () =>
            api
                .get<DepartamentoResponse[]>("/departamentos")
                .then((r) => r.data),
    });

    const save = useMutation({
        mutationFn: (n: string) =>
            editing === "new"
                ? api.post("/departamentos", { nome: n })
                : api.put(`/departamentos/${editing}`, { nome: n }),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["departamentos-all"] });
            cancel();
        },
    });

    const remove = useMutation({
        mutationFn: (id: number) => api.delete(`/departamentos/${id}`),
        onSuccess: () =>
            queryClient.invalidateQueries({ queryKey: ["departamentos-all"] }),
    });

    const startEdit = (dep: DepartamentoResponse) => {
        setEditing(dep.id);
        setNome(dep.nome);
    };

    const startNew = () => {
        setEditing("new");
        setNome("");
    };

    const cancel = () => {
        setEditing(null);
        setNome("");
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (!nome.trim()) return;
        save.mutate(nome.trim());
    };

    return (
        <div className="max-w-xl">
            <div className="flex items-center justify-between mb-5">
                <h1 className="text-xl font-bold text-gray-900">
                    Departamentos
                </h1>
                {canEdit && editing === null && (
                    <button
                        onClick={startNew}
                        className="btn-primary flex items-center gap-1.5"
                    >
                        <Plus size={16} /> Novo Departamento
                    </button>
                )}
            </div>

            <div className="rounded-xl bg-white shadow-sm border border-gray-100 overflow-hidden">
                <table className="min-w-full text-sm">
                    <thead>
                        <tr className="border-b border-gray-100 bg-gray-50/60 text-left text-xs font-medium uppercase tracking-wider text-gray-500">
                            <th className="px-4 py-3">Nome</th>
                            {canEdit && <th className="px-4 py-3 w-24" />}
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-50">
                        {editing === "new" && (
                            <tr className="bg-blue-50/40" key="new-row">
                                <td className="px-4 py-2">
                                    <form
                                        onSubmit={handleSubmit}
                                        className="flex items-center gap-2"
                                    >
                                        <input
                                            className="input flex-1"
                                            placeholder="Nome do departamento"
                                            autoFocus
                                            required
                                            value={nome}
                                            onChange={(e) =>
                                                setNome(e.target.value)
                                            }
                                        />
                                        <button
                                            type="submit"
                                            disabled={save.isPending}
                                            className="btn-ghost btn-sm text-green-600"
                                        >
                                            {save.isPending ? (
                                                <Loader2
                                                    size={14}
                                                    className="animate-spin"
                                                />
                                            ) : (
                                                <Save size={14} />
                                            )}
                                        </button>
                                        <button
                                            type="button"
                                            onClick={cancel}
                                            className="btn-ghost btn-sm text-gray-400"
                                        >
                                            <X size={14} />
                                        </button>
                                    </form>
                                </td>
                                {canEdit && <td />}
                            </tr>
                        )}

                        {isLoading
                            ? Array.from({ length: 5 }).map((_, i) => (
                                  <tr key={i} className="animate-pulse">
                                      <td className="px-4 py-3">
                                          <div className="h-4 rounded bg-gray-200 w-2/3" />
                                      </td>
                                  </tr>
                              ))
                            : departamentos?.map((dep) =>
                                  editing === dep.id ? (
                                      <tr
                                          key={dep.id}
                                          className="bg-blue-50/40"
                                      >
                                          <td className="px-4 py-2" colSpan={2}>
                                              <form
                                                  onSubmit={handleSubmit}
                                                  className="flex items-center gap-2"
                                              >
                                                  <input
                                                      className="input flex-1"
                                                      required
                                                      autoFocus
                                                      value={nome}
                                                      onChange={(e) =>
                                                          setNome(
                                                              e.target.value,
                                                          )
                                                      }
                                                  />
                                                  <button
                                                      type="submit"
                                                      disabled={save.isPending}
                                                      className="btn-ghost btn-sm text-green-600"
                                                  >
                                                      {save.isPending ? (
                                                          <Loader2
                                                              size={14}
                                                              className="animate-spin"
                                                          />
                                                      ) : (
                                                          <Save size={14} />
                                                      )}
                                                  </button>
                                                  <button
                                                      type="button"
                                                      onClick={cancel}
                                                      className="btn-ghost btn-sm text-gray-400"
                                                  >
                                                      <X size={14} />
                                                  </button>
                                              </form>
                                          </td>
                                      </tr>
                                  ) : (
                                      <tr
                                          key={dep.id}
                                          className="hover:bg-gray-50/50"
                                      >
                                          <td className="px-4 py-3 font-medium text-gray-900">
                                              {dep.nome}
                                          </td>
                                          {canEdit && (
                                              <td className="px-4 py-3 text-right">
                                                  <div className="flex justify-end gap-1">
                                                      <button
                                                          onClick={() =>
                                                              startEdit(dep)
                                                          }
                                                          className="btn-ghost btn-sm"
                                                          title="Editar"
                                                      >
                                                          <Pencil size={14} />
                                                      </button>
                                                      <button
                                                          onClick={() => {
                                                              if (
                                                                  confirm(
                                                                      "Remover este departamento?",
                                                                  )
                                                              )
                                                                  remove.mutate(
                                                                      dep.id,
                                                                  );
                                                          }}
                                                          className="btn-ghost btn-sm text-red-500 hover:text-red-700"
                                                          title="Remover"
                                                      >
                                                          <X size={14} />
                                                      </button>
                                                  </div>
                                              </td>
                                          )}
                                      </tr>
                                  ),
                              )}

                        {!isLoading &&
                            (!departamentos || departamentos.length === 0) &&
                            editing !== "new" && (
                                <tr>
                                    <td
                                        colSpan={2}
                                        className="px-4 py-12 text-center text-gray-400"
                                    >
                                        Nenhum departamento cadastrado.
                                    </td>
                                </tr>
                            )}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
