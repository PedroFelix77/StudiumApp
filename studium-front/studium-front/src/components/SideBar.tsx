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
      { label: "Dashboard", path: "/director/dashboard" },
      { label: "Professores", path: "/director/drofessores" },
      { label: "Alunos", path: "/director/alunos" },
      { label: "Cursos", path: "/director/cursos" },
      { label: "Departamentos", path: "/director/departamentos" },
      { label: "Notas", path: "director/notas" },
      { label: "Frequências", path: "director/frequencias" },
      { label: "Relatórios", path: "director/relatorio" },
    ],

    TEACHER: [
      { label: "Dashboard", path: "/teacher/dashboard" },
      { label: "Alunos", path: "/teacher/alunos" },
      { label: "Cursos", path: "/teacher/cursos" },
      { label: "Notas", path: "/teacher/notas" },
      { label: "Frequências", path: "/teacher/frequencias" },
    ],

    STUDENT: [
      { label: "Dashboard", path: "/student/dashboard" },
      { label: "Curso", path: "/student/cursos" },
      { label: "Notas", path: "/student/notas" },
      { label: "Frequências", path: "/student/frequencias" },
    ],
  };

  const menuItems = MENU[role];

  return (
    <aside className="w-64 bg-white shadow-sm border-r">
      <div className="p-4 text-xl font-bold">Studium</div>

      <nav className="mt-4 flex flex-col">
        {menuItems.map((item) => (
          <Link
            key={item.path}
            to={item.path}
            className={cn(
              "px-4 py-3 hover:bg-gray-100",
              location.pathname === item.path && "bg-gray-200 font-semibold"
            )}
          >
            {item.label}
          </Link>
        ))}
      </nav>
    </aside>
  );
}
