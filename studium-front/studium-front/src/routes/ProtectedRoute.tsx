import { Navigate, Outlet } from "react-router-dom";
import type { ReactNode } from "react";
import { useAuth } from "@/context/AuthContext";
import { Loader2 } from "lucide-react";

interface ProtectedRouteProps {
  allowedRoles: string[];
  children?: ReactNode;
}

export function ProtectedRoute({ allowedRoles, children }: ProtectedRouteProps) {
  const { user, isAuthenticated } = useAuth();

  //caso o sistema esteja carregando a sessão inicial
  if (user === undefined) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <Loader2 className="animate-spin h-6 w-6 text-blue-600" />
      </div>
    );
  }

  //se não estiver autenticado, redireciona pro login
  if (!isAuthenticated || !user) {
    return <Navigate to="/login" replace />;
  }

  //se o usuário não tiver uma role permitida, redireciona
  if (!allowedRoles.includes(user.role)) {
    return <Navigate to="/login" replace />;
  }

  //caso tenha permissão
  return children ? <>{children}</> : <Outlet />;
}
