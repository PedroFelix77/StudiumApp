import { useAuth } from "@/context/AuthContext";
import { Button } from "@/components/ui/button";

export function Topbar() {
  const { user, logout } = useAuth();

  return (
    <header className="h-16 bg-white border-b shadow-sm flex items-center justify-between px-6">
      <span className="text-lg font-medium">
        Olá, {user?.name}
      </span>

      <Button variant="outline" onClick={logout}>
        Sair
      </Button>
    </header>
  );
}
