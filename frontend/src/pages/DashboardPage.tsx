import { useQuery } from "@tanstack/react-query";
import api from "../lib/api";
import type { DashboardResponse } from "../lib/types";
import { formatCurrency, estadoLabel, estadoBadgeClass } from "../lib/format";
import { Package, DollarSign, AlertTriangle, CheckCircle } from "lucide-react";

export function DashboardPage() {
    const { data, isLoading } = useQuery({
        queryKey: ["dashboard"],
        queryFn: () =>
            api.get<DashboardResponse>("/dashboard").then((r) => r.data),
    });

    if (isLoading) return <LoadingSkeleton />;
    if (!data) return null;

    const cards = [
        {
            title: "Total de Bens",
            value: data.totalBens.toString(),
            icon: Package,
            color: "text-blue-600 bg-blue-50",
        },
        {
            title: "Valor Total de Aquisição",
            value: formatCurrency(data.valorTotalAquisicao),
            icon: DollarSign,
            color: "text-green-600 bg-green-50",
        },
        {
            title: "Em Bom Estado",
            value: (data.bensPorEstado["BOM"] ?? 0).toString(),
            icon: CheckCircle,
            color: "text-emerald-600 bg-emerald-50",
        },
        {
            title: "Precisam de Atenção",
            value: (
                (data.bensPorEstado["RUIM"] ?? 0) +
                (data.bensPorEstado["TROCAR"] ?? 0)
            ).toString(),
            icon: AlertTriangle,
            color: "text-orange-600 bg-orange-50",
        },
    ];

    return (
        <div>
            <h1 className="text-xl font-bold text-gray-900 mb-6">Dashboard</h1>

            {/* Cards */}
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4 mb-8">
                {cards.map((card) => (
                    <div
                        key={card.title}
                        className="rounded-xl bg-white p-5 shadow-sm border border-gray-100"
                    >
                        <div className="flex items-center gap-3">
                            <div
                                className={`flex h-10 w-10 items-center justify-center rounded-lg ${card.color}`}
                            >
                                <card.icon size={20} />
                            </div>
                            <div>
                                <p className="text-xs font-medium text-gray-500">
                                    {card.title}
                                </p>
                                <p className="text-lg font-bold text-gray-900">
                                    {card.value}
                                </p>
                            </div>
                        </div>
                    </div>
                ))}
            </div>

            {/* Distribuição por estado */}
            <div className="rounded-xl bg-white p-5 shadow-sm border border-gray-100">
                <h2 className="text-sm font-semibold text-gray-900 mb-4">
                    Distribuição por Estado
                </h2>
                <div className="space-y-3">
                    {Object.entries(data.bensPorEstado).map(([estado, qtd]) => {
                        const pct =
                            data.totalBens > 0
                                ? (qtd / data.totalBens) * 100
                                : 0;
                        return (
                            <div
                                key={estado}
                                className="flex items-center gap-3"
                            >
                                <span className={estadoBadgeClass(estado)}>
                                    {estadoLabel(estado)}
                                </span>
                                <div className="flex-1 h-2 rounded-full bg-gray-100">
                                    <div
                                        className={`h-2 rounded-full ${
                                            estado === "BOM"
                                                ? "bg-green-500"
                                                : estado === "MEDIO"
                                                  ? "bg-yellow-500"
                                                  : estado === "RUIM"
                                                    ? "bg-orange-500"
                                                    : "bg-red-500"
                                        }`}
                                        style={{ width: `${pct}%` }}
                                    />
                                </div>
                                <span className="text-sm font-medium text-gray-700 w-10 text-right">
                                    {qtd}
                                </span>
                            </div>
                        );
                    })}
                </div>
            </div>
        </div>
    );
}

function LoadingSkeleton() {
    return (
        <div>
            <div className="h-7 w-32 bg-gray-200 rounded mb-6 animate-pulse" />
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4 mb-8">
                {[1, 2, 3, 4].map((i) => (
                    <div
                        key={i}
                        className="h-24 rounded-xl bg-gray-200 animate-pulse"
                    />
                ))}
            </div>
            <div className="h-48 rounded-xl bg-gray-200 animate-pulse" />
        </div>
    );
}
