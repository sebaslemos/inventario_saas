import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import {
    LayoutDashboard,
    Package,
    Tags,
    Building2,
    Users,
    LogOut,
    Menu,
    X,
} from "lucide-react";
import { useState } from "react";

const baseItems = [
    { to: "/", label: "Dashboard", icon: LayoutDashboard, end: true },
    { to: "/bens", label: "Bens", icon: Package },
];

const gestorItems = [
    { to: "/categorias", label: "Categorias", icon: Tags },
    { to: "/departamentos", label: "Departamentos", icon: Building2 },
];

const adminItems = [{ to: "/usuarios", label: "Usuários", icon: Users }];

export function AppLayout() {
    const { user, logout, isAdmin, canEdit } = useAuth();
    const navigate = useNavigate();
    const [sidebarOpen, setSidebarOpen] = useState(false);

    const items = [
        ...baseItems,
        ...(canEdit ? gestorItems : []),
        ...(isAdmin ? adminItems : []),
    ];

    function handleLogout() {
        logout();
        navigate("/login");
    }

    return (
        <div className="flex h-screen overflow-hidden bg-gray-50">
            {/* Overlay mobile */}
            {sidebarOpen && (
                <div
                    className="fixed inset-0 z-30 bg-black/30 lg:hidden"
                    onClick={() => setSidebarOpen(false)}
                />
            )}

            {/* Sidebar */}
            <aside
                className={`fixed inset-y-0 left-0 z-40 flex w-60 flex-col bg-white border-r border-gray-200
          transform transition-transform lg:relative lg:translate-x-0
          ${sidebarOpen ? "translate-x-0" : "-translate-x-full"}`}
            >
                {/* Logo */}
                <div className="flex h-14 items-center gap-2 border-b border-gray-200 px-4">
                    <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-blue-600 text-white">
                        <Package size={18} />
                    </div>
                    <span className="text-sm font-semibold text-gray-900">
                        Inventário
                    </span>
                    <button
                        className="ml-auto lg:hidden"
                        onClick={() => setSidebarOpen(false)}
                    >
                        <X size={18} className="text-gray-500" />
                    </button>
                </div>

                {/* Nav */}
                <nav className="flex-1 overflow-y-auto p-3 space-y-1">
                    {items.map(({ to, label, icon: Icon, ...rest }) => (
                        <NavLink
                            key={to}
                            to={to}
                            end={"end" in rest}
                            onClick={() => setSidebarOpen(false)}
                            className={({ isActive }) =>
                                `flex items-center gap-2.5 rounded-lg px-3 py-2 text-sm font-medium transition-colors
                 ${isActive ? "bg-blue-50 text-blue-700" : "text-gray-600 hover:bg-gray-100 hover:text-gray-900"}`
                            }
                        >
                            <Icon size={18} />
                            {label}
                        </NavLink>
                    ))}
                </nav>

                {/* User footer */}
                <div className="border-t border-gray-200 p-3">
                    <div className="mb-2 px-3">
                        <p className="text-sm font-medium text-gray-900 truncate">
                            {user?.nome}
                        </p>
                        <p className="text-xs text-gray-500 truncate">
                            {user?.tenantNome}
                        </p>
                    </div>
                    <button
                        onClick={handleLogout}
                        className="btn-ghost w-full justify-start text-gray-600 text-sm"
                    >
                        <LogOut size={16} /> Sair
                    </button>
                </div>
            </aside>

            {/* Main content */}
            <div className="flex flex-1 flex-col overflow-hidden">
                {/* Top bar (mobile) */}
                <header className="flex h-14 items-center border-b border-gray-200 bg-white px-4 lg:hidden">
                    <button onClick={() => setSidebarOpen(true)}>
                        <Menu size={20} className="text-gray-600" />
                    </button>
                    <span className="ml-3 text-sm font-semibold text-gray-900">
                        Inventário
                    </span>
                </header>

                <main className="flex-1 overflow-y-auto p-4 lg:p-6">
                    <Outlet />
                </main>
            </div>
        </div>
    );
}
