import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import api from "../lib/api";
import type { UsuarioResponse, UsuarioRequest } from "../lib/types";
import { perfilLabel } from "../lib/format";
import { Plus, X, Loader2, UserPlus } from "lucide-react";

const emptyUser: UsuarioRequest = {
    nome: "",
    email: "",
    senha: "",
    perfil: "USUARIO",
};

export function UsuariosPage() {
    const queryClient = useQueryClient();
    const [showForm, setShowForm] = useState(false);
    const [form, setForm] = useState<UsuarioRequest>(emptyUser);

    const { data: usuarios, isLoading } = useQuery({
        queryKey: ["usuarios"],
        queryFn: () =>
            api.get<UsuarioResponse[]>("/usuarios").then((r) => r.data),
    });

    const create = useMutation({
        mutationFn: (data: UsuarioRequest) => api.post("/usuarios", data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["usuarios"] });
            setShowForm(false);
            setForm(emptyUser);
        },
    });

    const remove = useMutation({
        mutationFn: (id: number) => api.delete(`/usuarios/${id}`),
        onSuccess: () =>
            queryClient.invalidateQueries({ queryKey: ["usuarios"] }),
    });

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        create.mutate(form);
    };

    return (
        <div className="max-w-3xl">
            <div className="flex items-center justify-between mb-5">
                <h1 className="text-xl font-bold text-gray-900">Usuários</h1>
                {!showForm && (
                    <button
                        onClick={() => setShowForm(true)}
                        className="btn-primary flex items-center gap-1.5"
                    >
                        <Plus size={16} /> Novo Usuário
                    </button>
                )}
            </div>

            {/* ── Formulário de criação ── */}
            {showForm && (
                <form
                    onSubmit={handleSubmit}
                    className="rounded-xl bg-white p-5 shadow-sm border border-gray-100 mb-5 space-y-4"
                >
                    <h2 className="text-sm font-semibold text-gray-900">
                        Criar Usuário
                    </h2>
                    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                        <div>
                            <label className="label">Nome *</label>
                            <input
                                className="input"
                                required
                                value={form.nome}
                                onChange={(e) =>
                                    setForm((f) => ({
                                        ...f,
                                        nome: e.target.value,
                                    }))
                                }
                            />
                        </div>
                        <div>
                            <label className="label">Email *</label>
                            <input
                                className="input"
                                type="email"
                                required
                                value={form.email}
                                onChange={(e) =>
                                    setForm((f) => ({
                                        ...f,
                                        email: e.target.value,
                                    }))
                                }
                            />
                        </div>
                        <div>
                            <label className="label">Senha *</label>
                            <input
                                className="input"
                                type="password"
                                required
                                minLength={6}
                                value={form.senha}
                                onChange={(e) =>
                                    setForm((f) => ({
                                        ...f,
                                        senha: e.target.value,
                                    }))
                                }
                            />
                        </div>
                        <div>
                            <label className="label">Perfil *</label>
                            <select
                                className="input"
                                value={form.perfil}
                                onChange={(e) =>
                                    setForm((f) => ({
                                        ...f,
                                        perfil: e.target
                                            .value as UsuarioRequest["perfil"],
                                    }))
                                }
                            >
                                <option value="USUARIO">Usuário</option>
                                <option value="GESTOR">Gestor</option>
                                <option value="ADMIN">Administrador</option>
                            </select>
                        </div>
                    </div>
                    <div className="flex justify-end gap-2">
                        <button
                            type="button"
                            onClick={() => {
                                setShowForm(false);
                                setForm(emptyUser);
                            }}
                            className="btn-secondary"
                        >
                            Cancelar
                        </button>
                        <button
                            type="submit"
                            disabled={create.isPending}
                            className="btn-primary flex items-center gap-1.5"
                        >
                            {create.isPending ? (
                                <Loader2 size={16} className="animate-spin" />
                            ) : (
                                <UserPlus size={16} />
                            )}
                            Criar
                        </button>
                    </div>
                </form>
            )}

            {/* ── Lista ── */}
            <div className="rounded-xl bg-white shadow-sm border border-gray-100 overflow-hidden">
                <table className="min-w-full text-sm">
                    <thead>
                        <tr className="border-b border-gray-100 bg-gray-50/60 text-left text-xs font-medium uppercase tracking-wider text-gray-500">
                            <th className="px-4 py-3">Nome</th>
                            <th className="px-4 py-3">Email</th>
                            <th className="px-4 py-3">Perfil</th>
                            <th className="px-4 py-3 w-16" />
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-50">
                        {isLoading
                            ? Array.from({ length: 4 }).map((_, i) => (
                                  <tr key={i} className="animate-pulse">
                                      {[1, 2, 3].map((j) => (
                                          <td key={j} className="px-4 py-3">
                                              <div className="h-4 rounded bg-gray-200 w-3/4" />
                                          </td>
                                      ))}
                                      <td />
                                  </tr>
                              ))
                            : usuarios?.map((u) => (
                                  <tr
                                      key={u.id}
                                      className="hover:bg-gray-50/50"
                                  >
                                      <td className="px-4 py-3 font-medium text-gray-900">
                                          {u.nome}
                                      </td>
                                      <td className="px-4 py-3 text-gray-700">
                                          {u.email}
                                      </td>
                                      <td className="px-4 py-3">
                                          <span className="inline-block rounded-full bg-gray-100 px-2 py-0.5 text-xs font-medium text-gray-700">
                                              {perfilLabel(u.perfil)}
                                          </span>
                                      </td>
                                      <td className="px-4 py-3 text-right">
                                          <button
                                              onClick={() => {
                                                  if (
                                                      confirm(
                                                          `Remover ${u.nome}?`,
                                                      )
                                                  )
                                                      remove.mutate(u.id);
                                              }}
                                              className="btn-ghost btn-sm text-red-500 hover:text-red-700"
                                              title="Remover"
                                          >
                                              <X size={14} />
                                          </button>
                                      </td>
                                  </tr>
                              ))}

                        {!isLoading && (!usuarios || usuarios.length === 0) && (
                            <tr>
                                <td
                                    colSpan={4}
                                    className="px-4 py-12 text-center text-gray-400"
                                >
                                    Nenhum usuário cadastrado.
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
