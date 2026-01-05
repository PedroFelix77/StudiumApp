import { useState, useEffect } from "react";
import { api } from "@/services/api";
import { jwtDecode } from "jwt-decode";

export interface Institution {
  id: string;
  name: string;
  CNPJ: string | null;
  addressResponseDTO: any;
  courses: any[];
  departments: any[];
}

export interface Address {
  id: string;
  cep: string;
  street: string;
  number: string;
  city: string;
  state: string;
  complement: string;
}

export interface User {
  id: string;
  name: string;
  email: string;
  role: "ADMIN" | "DIRECTOR" | "TEACHER" | "STUDENT";
  institution: Institution;
  address: Address;
}

export const useAuthProvider = () => {
  const [user, setUser] = useState<User | null | undefined>(undefined);
  const isAuthenticated = !!user;

  const saveAuth = (authData: { token: string; user: User }) => {
    localStorage.setItem("auth", JSON.stringify(authData));

    api.defaults.headers.common["Authorization"] = `Bearer ${authData.token}`;

    setUser(authData.user);
  };

  const login = async ({ email, password }: { email: string; password: string }) => {
    const { data } = await api.post("/api/auth/login", {
      email,
      password,
    });

    saveAuth({
      token: data.token,
      user: data.user,
    });

    return data.user;
  };

  const logout = () => {
    localStorage.removeItem("auth");
    delete api.defaults.headers.common["Authorization"];
    setUser(null);
  };

  useEffect(() => {
    const saved = localStorage.getItem("auth");
    if (!saved) {
      setUser(null);
    return
    }

    try {
      const parsed = JSON.parse(saved);

      const decoded: any = jwtDecode(parsed.token);
      const expirationMs = decoded.exp * 1000;
      
      if (Date.now() > expirationMs) {
        logout();
        return;
      }

      api.defaults.headers.common["Authorization"] = `Bearer ${parsed.token}`;
      setUser(parsed.user);
    } catch (err) {
      logout();
    }
  }, []);

  return { user, isAuthenticated, login, logout };
};
