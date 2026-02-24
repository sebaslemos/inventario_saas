import { useState, type FormEvent } from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import { Package } from "lucide-react";

export function LoginPage() {
    const { login, token } = useAuth();
    const [email, setEmail] = useState("");
    const [senha, setSenha] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    if (token) return <Navigate to="/" replace />;

    async function handleSubmit(e: FormEvent) {
        e.preventDefault();
        setError("");
        setLoading(true);
        try {
            await login({ email, senha });
        } catch (err: any) {
            setError(
                err.response?.data?.message ?? "Erro ao conectar ao servidor",
            );
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-blue-50 to-blue-100 px-4">
            <div className="w-full max-w-sm">
                <div className="mb-8 text-center">
                    <div className="mx-auto mb-3 flex h-14 w-14 items-center justify-center rounded-xl bg-blue-600 text-white">
                        <Package size={28} />
                    </div>
                    <h1 className="text-2xl font-bold text-gray-900">
                        Inventário
                    </h1>
                    <p className="mt-1 text-sm text-gray-500">
                        Controle patrimonial
                    </p>
                </div>

                <form
                    onSubmit={handleSubmit}
                    className="rounded-xl bg-white p-6 shadow-lg"
                >
                    {error && (
                        <div className="mb-4 rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">
                            {error}
                        </div>
                    )}

                    <div className="mb-4">
                        <label htmlFor="email" className="label">
                            E-mail
                        </label>
                        <input
                            id="email"
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            className="input"
                            placeholder="seu@email.com"
                            required
                            autoFocus
                        />
                    </div>

                    <div className="mb-6">
                        <label htmlFor="senha" className="label">
                            Senha
                        </label>
                        <input
                            id="senha"
                            type="password"
                            value={senha}
                            onChange={(e) => setSenha(e.target.value)}
                            className="input"
                            placeholder="••••••••"
                            required
                        />
                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        className="btn-primary w-full"
                    >
                        {loading ? "Entrando…" : "Entrar"}
                    </button>
                </form>
            </div>
        </div>
    );
}
