import { useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";
import axios from "axios";

interface Curso {
  id: number;
  name: string;
}

interface Turma {
  id: number;
  name: string;
  periodo: string;
}

export interface User {
  id: number;
  name: string;
  role: "admin" | "professor" | "aluno";
  token: string;
  refreshToken: string;
  tenant_id: string;
  cursos?: Curso[];
  turma?: Turma;
  matricula?: string;
}

interface LoginCredentials {
  matricula?: string;
  email?: string;
  senha: string;
}

export const useAuthProvider = () => {
  const [user, setUser] = useState<User | null>(null);
  const isAuthenticated = !!user;

  //  Armazena usuário + configura axios globalmente
  const saveAuth = (userData: User) => {
    setUser(userData);
    localStorage.setItem("auth", JSON.stringify(userData));

    axios.defaults.headers.common["Authorization"] = `Bearer ${userData.token}`;
    axios.defaults.headers.common["X-Tenant-ID"] = userData.tenant_id;
  };

  //LOGIN
  const login = async (credentials: LoginCredentials) => {
    try {
      const { data } = await axios.post("/api/auth/login", credentials);
      const { token, refreshToken } = data;

      const decoded: any = jwtDecode(token);
      const userId = decoded.sub;
      const userRole = decoded.role;
      const tenantId = decoded.tenant_id;

      // Busca dados completos do usuário
      const userData = await axios.get(`/api/users/${userId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      const completeUser: User = {
        ...userData.data,
        token,
        refreshToken,
        role: userRole,
        tenant_id: tenantId,
      };

      saveAuth(completeUser);
    } catch (error) {
      console.error("Erro no login:", error);
      throw new Error("Falha ao fazer login.");
    }
  };

  //LOGOUT
  const logout = () => {
    setUser(null);
    localStorage.removeItem("auth");

    delete axios.defaults.headers.common["Authorization"];
    delete axios.defaults.headers.common["X-Tenant-ID"];
  };

  //REFRESH TOKEN AUTOMÁTICO
  const refreshAccessToken = async (refreshToken: string) => {
    try {
      const { data } = await axios.post("/api/auth/refresh", { refreshToken });
      const newToken = data.token;

      const decoded: any = jwtDecode(newToken);
      const tenantId = decoded.tenant_id;

      const updatedUser = {
        ...user!,
        token: newToken,
        tenant_id: tenantId,
      };

      saveAuth(updatedUser);
      return newToken;
    } catch (error) {
      console.warn("Falha ao atualizar o token. Efetuando logout...");
      logout();
      return null;
    }
  };

  //VERIFICA TOKEN SALVO E RENOVA AUTOMATICAMENTE
  useEffect(() => {
    const savedAuth = localStorage.getItem("auth");
    if (!savedAuth) return;

    try {
      const parsedUser: User = JSON.parse(savedAuth);
      const decoded: any = jwtDecode(parsedUser.token);
      const exp = decoded.exp * 1000; // em milissegundos

      if (Date.now() > exp) {
        // token expirado → tenta refresh
        refreshAccessToken(parsedUser.refreshToken);
      } else {
        // token válido → restaura sessão
        saveAuth(parsedUser);

        // agenda refresh automático 1min antes de expirar
        const timeUntilRefresh = exp - Date.now() - 60_000;
        const timer = setTimeout(() => {
          refreshAccessToken(parsedUser.refreshToken);
        }, Math.max(timeUntilRefresh, 0));

        return () => clearTimeout(timer);
      }
    } catch (err) {
      console.error("Erro ao restaurar autenticação:", err);
      logout();
    }
  }, []);

  return { user, isAuthenticated, login, logout, refreshAccessToken };
};