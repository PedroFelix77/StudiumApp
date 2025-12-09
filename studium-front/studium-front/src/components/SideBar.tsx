import { useAuth } from "@/context/AuthContext";
import { Link, useLocation } from "react-router-dom";
import { cn } from "@/lib/utils";

export function Sidebar() {
  const { user } = useAuth();
  const location = useLocation();

  if (!user) return null;

  const role = user.role;

  const MENU: Record<string, Array<{ label: string; path: string }>> = {
    ADMIN: [
      { label: "Dashboard", path: "/admin/dashboard" },
      { label: "Alunos", path: "/admin/alunos" },
      { label: "Professores", path: "/admin/professores" },
      { label: "Cursos", path: "/admin/cursos" },
      { label: "Departamentos", path: "/admin/departamentos" },
      { label: "Notas", path: "/admin/notas" },
      { label: "Frequências", path: "/admin/frequencias" },
      { label: "Relatórios", path: "/admin/relatorios" }
    ],

    DIRECTOR: [
      { label: "Dashboard", path: "/diretor/dashboard" },
      { label: "Professores", path: "/diretor/professores" },
      { label: "Alunos", path: "/diretor/alunos" },
      { label: "Cursos", path: "/diretor/cursos" },
      { label: "Departamentos", path: "/diretor/departamentos" },
      { label: "Notas", path: "/diretor/notas" },
      { label: "Frequências", path: "/diretor/frequencia" },
      { label: "Relatórios", path: "/diretor/relatorios" },
    ],

    TEACHER: [
      { label: "Dashboard", path: "/professor/dashboard" },
      { label: "Alunos", path: "/professor/alunos" },
      { label: "Cursos", path: "/professor/cursos" },
      { label: "Notas", path: "/professor/notas" },
      { label: "Frequências", path: "/professor/frequencias" },
    ],

    STUDENT: [
      { label: "Dashboard", path: "/aluno/dashboard" },
      { label: "Curso", path: "/aluno/curso" },
      { label: "Notas", path: "/aluno/notas" },
      { label: "Frequências", path: "/aluno/frequencias" },
    ],
  };

  const menuItems = MENU[role];

  return (
    <aside className="w-64 bg-[#0F172A] shadow-sm border-r border-[#0891B2]/20">
      <div className="p-4 text-xl font-bold text-[#E5E7EB]">Studium</div>

      <nav className="mt-4 flex flex-col">
        {menuItems.map((item) => (
          <Link
            key={item.path}
            to={item.path}
            className={cn(
              "px-4 py-3 text-[#E5E7EB] hover:bg-[#0891B2]/20 transition-colors",
              location.pathname === item.path && "bg-[#0891B2] text-white font-semibold"
            )}
          >
            {item.label}
          </Link>
        ))}
      </nav>
    </aside>
  );
}
