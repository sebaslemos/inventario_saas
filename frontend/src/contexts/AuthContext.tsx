import {
    createContext,
    useContext,
    useState,
    useCallback,
    type ReactNode,
} from "react";
import api from "../lib/api";
import type { LoginRequest, LoginResponse, User } from "../lib/types";

interface AuthContextType {
    user: User | null;
    token: string | null;
    login: (data: LoginRequest) => Promise<void>;
    logout: () => void;
    isAdmin: boolean;
    isGestor: boolean;
    isUsuario: boolean;
    canEdit: boolean;
    canDelete: boolean;
}

const AuthContext = createContext<AuthContextType | null>(null);

function loadUser(): User | null {
    const raw = localStorage.getItem("user");
    return raw ? JSON.parse(raw) : null;
}

export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<User | null>(loadUser);
    const [token, setToken] = useState<string | null>(
        localStorage.getItem("token"),
    );

    const login = useCallback(async (data: LoginRequest) => {
        const res = await api.post<LoginResponse>("/auth/login", data);
        const { token: jwt, ...userData } = res.data;
        localStorage.setItem("token", jwt);
        localStorage.setItem("user", JSON.stringify(userData));
        setToken(jwt);
        setUser(userData);
    }, []);

    const logout = useCallback(() => {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        setToken(null);
        setUser(null);
    }, []);

    const isAdmin = user?.perfil === "ADMIN";
    const isGestor = user?.perfil === "GESTOR";
    const isUsuario = user?.perfil === "USUARIO";
    const canEdit = isAdmin || isGestor;
    const canDelete = isAdmin;

    return (
        <AuthContext.Provider
            value={{
                user,
                token,
                login,
                logout,
                isAdmin,
                isGestor,
                isUsuario,
                canEdit,
                canDelete,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth(): AuthContextType {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error("useAuth must be used within AuthProvider");
    return ctx;
}
