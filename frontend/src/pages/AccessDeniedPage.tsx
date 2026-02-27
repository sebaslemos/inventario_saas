import { Link } from "react-router-dom";
import { ShieldX } from "lucide-react";

export function AccessDeniedPage() {
    return (
        <div className="flex flex-col items-center justify-center min-h-[60vh] text-center px-4">
            <ShieldX size={56} className="text-red-400 mb-4" />
            <h1 className="text-2xl font-bold text-gray-900 mb-2">
                Acesso Negado
            </h1>
            <p className="text-gray-500 mb-6 max-w-md">
                Você não tem permissão para acessar esta página. Entre em
                contato com o administrador caso precise de acesso.
            </p>
            <Link to="/" className="btn-primary">
                Voltar para o início
            </Link>
        </div>
    );
}
