import { createContext, useContext, type ReactNode } from "react";
import { useAuthProvider, type User } from "./useAuthProvider";

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  login: (credentials: {email?: string; matricula?: string; senha: string}) => Promise<void>;
  logout: () => void;
  refreshAccessToken: (refreshToken: string) => Promise<string | null>;  
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const auth = useAuthProvider(); // <- toda lógica vem daqui

  const value: AuthContextType = {
    user: auth.user,
    isAuthenticated: auth.isAuthenticated,
    login: auth.login,
    logout: auth.logout,
    refreshAccessToken: auth.refreshAccessToken,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

// Hook para usar o context
export const useAuth = () => {
  const context = useContext(AuthContext);

  if(!context) throw new Error("useAuth deve ser usado dentro de um AuthProvider");
  return context;
} 