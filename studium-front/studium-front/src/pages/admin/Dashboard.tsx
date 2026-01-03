import { useAdminDashboard } from "@/hooks/dashboard/useDashboard";
import { StatCard } from "@/components/ui/stat-card";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Users, GraduationCap, BookOpen, Building2 } from "lucide-react";

export default function AdminDashboardPage() {
  const { data, loading, error } = useAdminDashboard();

  if (loading) return <div>Carregando dashboard...</div>;
  if (error) return <div className="text-red-600">{error}</div>;
  if (!data) return null;

  const registrations = data.recentRegistrations ?? [];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-semibold">Dashboard</h1>
        <p className="text-muted-foreground">Visão geral do sistema acadêmico</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-4">
        <StatCard title="Total de Alunos" value={String(data.totalStudents)} icon={Users} />
        <StatCard title="Professores Ativos" value={String(data.totalTeachers)} icon={GraduationCap} />
        <StatCard title="Cursos Ativos" value={String(data.totalCourses)} icon={BookOpen} />
        <StatCard title="Departamentos" value={String(data.totalDepartments)} icon={Building2} />
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Últimas Matrículas</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-2">
            {registrations.length === 0 && <div>Nenhuma matrícula recente</div>}
            <ul className="space-y-2">
              {registrations.map((r, idx) => (
                <li key={idx} className="flex justify-between items-center border-b py-2">
                  <div>
                    <div className="font-medium">{r.studentName}</div>
                    <div className="text-sm text-muted-foreground">{r.courseName}</div>
                  </div>
                  <div className="text-sm text-muted-foreground">{new Date(r.createdAt).toLocaleString()}</div>
                </li>
              ))}
            </ul>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
