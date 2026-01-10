// hooks/dashboard/useDashboard.ts
import { useState, useEffect } from 'react';
import { api } from '@/services/api';
import { useAuth } from '@/context/AuthContext';

interface Stat {
  label: string;
  value: number;
  extra?: string;
}

interface DisciplinePerformance {
  disciplineName: string;
  className: string;
  studentCount: number;
  averageGrade?: number;
  approvalRate?: number;
}

interface RecentActivity {
  id: string;
  type: 'NOTA' | 'FREQUENCIA' | 'OUTRO';
  text: string;
  createdAt: string;
}

interface ProfessorDashboardData {
  stats: Stat[];
  disciplinePerformance: DisciplinePerformance[];
  recentActivities: RecentActivity[];
}

export function useProfessorDashboard() {
  const { user } = useAuth();
  const [data, setData] = useState<ProfessorDashboardData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!user?.id) return;

    const fetchDashboardData = async () => {
      try {
        setLoading(true);
        
        // 1. Buscar turmas do professor
        const classesRes = await api.get(`/api/teacher-classes/my/classes`);
        const classes = classesRes.data.content || classesRes.data || [];
        
        // 2. Para cada turma, buscar disciplinas
        let totalStudents = 0;
        let totalDisciplines = 0;
        
        // 3. Buscar alunos das turmas
        for (const classItem of classes) {
          try {
            const studentsRes = await api.get(`/api/registrations/class/${classItem.id}/students`);
            totalStudents += (studentsRes.data?.length || 0);
          } catch (err) {
            console.error(`Erro ao buscar alunos da turma ${classItem.id}:`, err);
          }
        }
        
        // 4. Buscar disciplinas de cada turma
        for (const classItem of classes) {
          try {
            const disciplinesRes = await api.get(`/api/teacher-classes/my/disciplines`, {
              params: { classId: classItem.id }
            });
            totalDisciplines += (disciplinesRes.data?.length || 0);
          } catch (err) {
            console.error(`Erro ao buscar disciplinas da turma ${classItem.id}:`, err);
          }
        }
        
        // 5. Buscar dados de desempenho (notas médias)
        const performanceData: DisciplinePerformance[] = [];
        // Implemente a lógica para buscar médias por disciplina
        
        // 6. Buscar atividades recentes
        const recentActivities: RecentActivity[] = [];
        // Implemente a lógica para buscar atividades recentes do professor
        
        // 7. Montar estatísticas
        const stats: Stat[] = [
          {
            label: 'Turmas',
            value: classes.length,
            extra: classes.length === 1 ? '1 turma' : `${classes.length} turmas`
          },
          {
            label: 'Disciplinas',
            value: totalDisciplines,
            extra: `${totalDisciplines} ${totalDisciplines === 1 ? 'disciplina' : 'disciplinas'}`
          },
          {
            label: 'Alunos',
            value: totalStudents,
            extra: `${totalStudents} ${totalStudents === 1 ? 'aluno' : 'alunos'} nas suas turmas`
          },
          {
            label: 'Média Geral',
            value: 0, // Substitua por cálculo real
            extra: 'N/A' // Substitua por cálculo real
          }
        ];

        setData({
          stats,
          disciplinePerformance: performanceData,
          recentActivities
        });
      } catch (err: any) {
        setError(err.message || 'Erro ao carregar dashboard');
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, [user?.id]);

  return { data, loading, error };
}