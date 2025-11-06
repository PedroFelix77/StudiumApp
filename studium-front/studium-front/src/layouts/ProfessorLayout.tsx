import { Outlet, NavLink } from "react-router-dom";
import { LayoutDashboard, Users, BookOpen, FileText, LogOut } from "lucide-react";

export function ProfessorLayout() {
  const menu = [
    { path: "/professor", label: "Dashboard", icon: <LayoutDashboard size={18} /> },
    { path: "/professor/alunos", label: "Alunos", icon: <Users size={18} /> },
    { path: "/professor/cursos", label: "Cursos", icon: <BookOpen size={18} /> },
    { path: "/professor/notas", label: "Notas", icon: <FileText size={18} /> },
  ];

  return (
    <div className="flex min-h-screen bg-gray-50">
      <aside className="w-64 bg-white border-r p-4 flex flex-col">
        <h1 className="text-xl font-bold mb-6">Painel Professor</h1>

        <nav className="flex flex-col gap-2">
          {menu.map((item) => (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                `flex items-center gap-2 p-2 rounded-md ${
                  isActive ? "bg-green-100 text-green-700 font-semibold" : "hover:bg-gray-100"
                }`
              }
            >
              {item.icon}
              {item.label}
            </NavLink>
          ))}
        </nav>

        <button className="mt-auto flex items-center gap-2 p-2 text-gray-600 hover:text-red-500">
          <LogOut size={18} /> Sair
        </button>
      </aside>

      <main className="flex-1 p-6">
        <Outlet />
      </main>
    </div>
  );
}