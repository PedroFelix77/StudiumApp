import { Outlet } from "react-router-dom";
import { Sidebar } from "@/components/SideBar";
import { Topbar } from "@/components/TopBar";

export function AppLayout() {
  return (
    <div className="flex min-h-screen bg-gray-50">
      <Sidebar />

      <div className="flex flex-col flex-1">
        <Topbar />

        <main className="p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
