import { useDirectorDashboard } from "@/hooks/dashboard/useDashboard";
import { StatCard } from "@/components/ui/stat-card";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Users, GraduationCap, BookOpen, Building2, TrendingUp, Activity } from "lucide-react";

const iconMap: Record<string, typeof Users> = {
  "Alunos": Users,
  "Professores": GraduationCap,
  "Cursos": BookOpen,
  "Departamentos": Building2,
};

export default function DiretorDashboard() {
  const { data, loading, error } = useDirectorDashboard();

  if (loading) return <div>Carregando dashboard...</div>;
  if (error) return <div className="text-red-600">{error}</div>;
  if (!data) return null;

  const stats = data.stats ?? [];
  const coursePerformance = data.coursePerformance ?? [];
  const recentActivities = data.recentActivities ?? [];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-semibold">Dashboard</h1>
        <p className="text-muted-foreground">Visão geral da instituição</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-4">
        {stats.map((stat, index) => {
          const Icon = iconMap[stat.label] || TrendingUp;
          return (
            <StatCard
              key={index}
              title={stat.label}
              value={String(stat.value)}
              icon={Icon}
              description={stat.extra || undefined}
            />
          );
        })}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <TrendingUp size={20} />
              Desempenho dos Cursos
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {coursePerformance.length === 0 && (
                <div className="text-muted-foreground">Nenhum dado de desempenho disponível</div>
              )}
              {coursePerformance.map((course, index) => (
                <div key={index} className="flex justify-between items-center border-b pb-3 last:border-0">
                  <div>
                    <div className="font-medium">{course.courseName}</div>
                    <div className="text-sm text-muted-foreground">
                      {course.studentCount} {course.studentCount === 1 ? "aluno" : "alunos"}
                    </div>
                  </div>
                  <div className="text-right">
                    {course.averageGrade !== null && course.averageGrade !== undefined ? (
                      <div className="font-semibold">{course.averageGrade.toFixed(1)}</div>
                    ) : (
                      <div className="text-muted-foreground text-sm">Sem média</div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Activity size={20} />
              Atividades Recentes
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-2">
              {recentActivities.length === 0 && (
                <div className="text-muted-foreground">Nenhuma atividade recente</div>
              )}
              <ul className="space-y-3">
                {recentActivities.map((activity) => (
                  <li key={activity.id} className="border-b pb-3 last:border-0">
                    <div className="text-sm">{activity.text}</div>
                    <div className="text-xs text-muted-foreground mt-1">
                      {new Date(activity.createdAt).toLocaleString()}
                    </div>
                  </li>
                ))}
              </ul>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}

