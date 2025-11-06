import { Outlet, NavLink } from "react-router-dom";
import { LogOut, LayoutDashboard, Users, GraduationCap, FileText, BookOpen} from "lucide-react";

export function AdminLayout() {
    const menu = [
    {path: "/admin", label: "Dashboard", icon: <LayoutDashboard size={18}/>},
    {path: "/admin/alunos", label: "Alunos", icon: <Users size={18}/>},
    {path: "/admin/professores", label: "Professores", icon: <GraduationCap size={18}/>},
    {path: "/admin/cursos", label: "Cursos", icon: <BookOpen size={18}/>},
    {path: "/admin/notas", label: "Notas", icon: <FileText size={18}/>},
    {path: "/admin/relatorios", label: "Relatórios", icon: <FileText size={18}/>},
    ]

    return (
        <div className="flex min-h-screen bg-gray-50"> 
        {/*SIDE BAR*/}
        <aside className="w-64 bg-white border-r p-4 flex flex-col">
            <h1 className="text-xl font-bold mb-6">Painel Admin</h1>
            <nav className="flex flex-col gap-2">
               {menu.map((item) => (
                <NavLink key={item.path} to={item.path} className={({isActive}) => `flex items-center gap-2 p-2 rounded-md ${isActive ? "bg-blue-100 text-blue-700 font-semibold" : "hover:bg-gray-100"}`}>
                    {item.icon}
                    {item.label}
                </NavLink>
               ))}
               </nav>

               <button className="mt-auto flex items-center gap-2 p-2 text-gray-600 hover:text-red-500">
          <LogOut size={18} /> Sair
        </button>
        </aside>
        
        {/* Conteúdo principal */}
      <main className="flex-1 p-6">
        <Outlet />
      </main>
        </div>
    )
}