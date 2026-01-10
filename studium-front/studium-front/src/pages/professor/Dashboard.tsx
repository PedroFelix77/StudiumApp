import { useProfessorDashboard } from "@/hooks/dashboard/useProfessorDashboard";
import { StatCard } from "@/components/ui/stat-card";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Users, BookOpen, GraduationCap, TrendingUp, Activity, Calendar } from "lucide-react";

const iconMap: Record<string, typeof Users> = {
  "Turmas": BookOpen,
  "Disciplinas": BookOpen,
  "Alunos": Users,
  "Média Geral": TrendingUp,
};

export default function ProfessorDashboard() {
  const { data, loading, error } = useProfessorDashboard();

  if (loading) return <div className="text-center py-12">Carregando dashboard...</div>;
  if (error) return <div className="text-center py-12 text-red-600">{error}</div>;
  if (!data) return null;

  const stats = data.stats ?? [];
  const disciplinePerformance = data.disciplinePerformance ?? [];
  const recentActivities = data.recentActivities ?? [];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-semibold">Dashboard do Professor</h1>
        <p className="text-muted-foreground">Visão geral das suas turmas e disciplinas</p>
      </div>

      {/* Cards de Estatísticas */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
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

      {/* Conteúdo Principal */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Desempenho por Disciplina */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <GraduationCap size={20} />
              Desempenho por Disciplina
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {disciplinePerformance.length === 0 && (
                <div className="text-muted-foreground text-center py-4">
                  Nenhum dado de desempenho disponível
                </div>
              )}
              {disciplinePerformance.map((discipline, index) => (
                <div
                  key={index}
                  className="flex justify-between items-center border-b pb-3 last:border-0 hover:bg-gray-50 p-2 rounded"
                >
                  <div className="flex-1">
                    <div className="font-medium">{discipline.disciplineName}</div>
                    <div className="text-sm text-muted-foreground">
                      {discipline.studentCount} {discipline.studentCount === 1 ? "aluno" : "alunos"} •
                      Turma: {discipline.className}
                    </div>
                  </div>
                  <div className="text-right">
                    {discipline.averageGrade !== null && discipline.averageGrade !== undefined ? (
                      <div>
                        <div className={`font-semibold text-lg ${discipline.averageGrade >= 7 ? 'text-green-600' :
                          discipline.averageGrade >= 5 ? 'text-yellow-600' : 'text-red-600'
                          }`}>
                          {discipline.averageGrade.toFixed(1)}
                        </div>
                        <div className="text-xs text-muted-foreground">
                          {discipline.approvalRate !== undefined ? `${discipline.approvalRate}% de aprovação` : 'Média'}
                        </div>
                      </div>
                    ) : (
                      <div className="text-muted-foreground text-sm">Sem notas lançadas</div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        {/* Atividades Recentes */}
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
                <div className="text-muted-foreground text-center py-4">
                  Nenhuma atividade recente
                </div>
              )}
              <ul className="space-y-3">
                {recentActivities.map((activity) => (
                  <li
                    key={activity.id}
                    className="border-b pb-3 last:border-0 hover:bg-gray-50 p-2 rounded"
                  >
                    <div className="text-sm flex items-start gap-2">
                      <div className={`mt-1 w-2 h-2 rounded-full ${activity.type === 'NOTA' ? 'bg-blue-500' :
                        activity.type === 'FREQUENCIA' ? 'bg-green-500' :
                          'bg-purple-500'
                        }`}></div>
                      <div>
                        <div>{activity.text}</div>
                        <div className="text-xs text-muted-foreground mt-1 flex items-center gap-1">
                          <Calendar size={12} />
                          {new Date(activity.createdAt).toLocaleString('pt-BR', {
                            day: '2-digit',
                            month: '2-digit',
                            year: 'numeric',
                            hour: '2-digit',
                            minute: '2-digit'
                          })}
                        </div>
                      </div>
                    </div>
                  </li>
                ))}
              </ul>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Ações Rápidas */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Activity size={20} />
            Ações Rápidas
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <Card className="hover:shadow-md transition-shadow cursor-pointer">
              <CardContent className="pt-6">
                <div className="text-center">
                  <BookOpen className="h-10 w-10 mx-auto mb-2 text-blue-600" />
                  <h3 className="font-semibold">Lançar Notas</h3>
                  <p className="text-sm text-muted-foreground mt-1">
                    Adicione notas dos alunos
                  </p>
                </div>
              </CardContent>
            </Card>
            <Card className="hover:shadow-md transition-shadow cursor-pointer">
              <CardContent className="pt-6">
                <div className="text-center">
                  <Calendar className="h-10 w-10 mx-auto mb-2 text-green-600" />
                  <h3 className="font-semibold">Registrar Frequência</h3>
                  <p className="text-sm text-muted-foreground mt-1">
                    Controle de presença
                  </p>
                </div>
              </CardContent>
            </Card>
            <Card className="hover:shadow-md transition-shadow cursor-pointer">
              <CardContent className="pt-6">
                <div className="text-center">
                  <GraduationCap className="h-10 w-10 mx-auto mb-2 text-purple-600" />
                  <h3 className="font-semibold">Ver Alunos</h3>
                  <p className="text-sm text-muted-foreground mt-1">
                    Lista de alunos das turmas
                  </p>
                </div>
              </CardContent>
            </Card>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}