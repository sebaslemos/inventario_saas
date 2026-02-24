import { Navigate, Route, Routes } from "react-router-dom";
import { useAuth } from "./contexts/AuthContext";
import { AppLayout } from "./components/AppLayout";
import { LoginPage } from "./pages/LoginPage";
import { DashboardPage } from "./pages/DashboardPage";
import { BensPage } from "./pages/BensPage";
import { BemFormPage } from "./pages/BemFormPage";
import { CategoriasPage } from "./pages/CategoriasPage";
import { DepartamentosPage } from "./pages/DepartamentosPage";
import { UsuariosPage } from "./pages/UsuariosPage";

function RequireAuth({ children }: { children: React.ReactNode }) {
    const { token } = useAuth();
    if (!token) return <Navigate to="/login" replace />;
    return <>{children}</>;
}

function RequireAdmin({ children }: { children: React.ReactNode }) {
    const { isAdmin } = useAuth();
    if (!isAdmin) return <Navigate to="/" replace />;
    return <>{children}</>;
}

export function AppRoutes() {
    return (
        <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route
                element={
                    <RequireAuth>
                        <AppLayout />
                    </RequireAuth>
                }
            >
                <Route index element={<DashboardPage />} />
                <Route path="bens" element={<BensPage />} />
                <Route path="bens/novo" element={<BemFormPage />} />
                <Route path="bens/:id/editar" element={<BemFormPage />} />
                <Route path="categorias" element={<CategoriasPage />} />
                <Route path="departamentos" element={<DepartamentosPage />} />
                <Route
                    path="usuarios"
                    element={
                        <RequireAdmin>
                            <UsuariosPage />
                        </RequireAdmin>
                    }
                />
            </Route>
            <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
    );
}
