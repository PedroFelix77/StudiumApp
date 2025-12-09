import { createContext, useContext, type ReactNode } from "react";
import { useAuthProvider, type User } from "./useAuthProvider";

interface AuthContextType {
  user: ReturnType<typeof useAuthProvider>["user"];
  isAuthenticated: boolean;
  login: (c: { email: string; password: string }) => Promise<User>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const auth = useAuthProvider();

  return (
    <AuthContext.Provider value={auth}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth deve ser usado dentro de um AuthProvider");
  return ctx;
};
