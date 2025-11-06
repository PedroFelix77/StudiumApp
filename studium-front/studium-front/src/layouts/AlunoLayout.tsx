import { Outlet, NavLink } from "react-router-dom";
import { LayoutDashboard, BookOpen, FileText, CalendarCheck, LogOut } from "lucide-react";

export function AlunoLayout() {
  const menu = [
    { path: "/aluno", label: "Dashboard", icon: <LayoutDashboard size={18} /> },
    { path: "/aluno/curso", label: "Curso", icon: <BookOpen size={18} /> },
    { path: "/aluno/frequencia", label: "Frequência", icon: <CalendarCheck size={18} /> },
    { path: "/aluno/notas", label: "Notas", icon: <FileText size={18} /> },
  ];

  return (
    <div className="flex min-h-screen bg-gray-50">
      <aside className="w-64 bg-white border-r p-4 flex flex-col">
        <h1 className="text-xl font-bold mb-6">Área do Aluno</h1>

        <nav className="flex flex-col gap-2">
          {menu.map((item) => (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                `flex items-center gap-2 p-2 rounded-md ${
                  isActive ? "bg-yellow-100 text-yellow-700 font-semibold" : "hover:bg-gray-100"
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