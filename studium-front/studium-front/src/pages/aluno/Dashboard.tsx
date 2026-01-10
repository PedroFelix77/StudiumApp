"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import {
  BookOpen,
  TrendingUp,
  Calendar,
  User,
  GraduationCap,
  Clock,
  AlertCircle,
  CheckCircle,
  XCircle
} from "lucide-react"
import { api } from "@/services/api"
import { useAuth } from "@/context/AuthContext"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { Progress } from "@/components/ui/progress"
import { Skeleton } from "@/components/ui/skeleton"

interface StudentStats {
  totalCourses: number
  totalDisciplines: number
  averageGrade: number
  attendanceRate: number
  pendingAssignments: number
  completedDisciplines: number
}

interface RecentGrade {
  disciplineName: string
  grade: number
  type: string
  date: string
}

interface UpcomingClass {
  disciplineName: string
  className: string
  date: string
  time: string
}

export default function AlunoDashboard() {
  const { user } = useAuth()
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [stats, setStats] = useState<StudentStats>({
    totalCourses: 0,
    totalDisciplines: 0,
    averageGrade: 0,
    attendanceRate: 0,
    pendingAssignments: 0,
    completedDisciplines: 0
  })
  const [recentGrades, setRecentGrades] = useState<RecentGrade[]>([])
  const [upcomingClasses, setUpcomingClasses] = useState<UpcomingClass[]>([])

  useEffect(() => {
    if (user?.id) {
      loadDashboardData()
    }
  }, [user])

  const loadDashboardData = async () => {
    try {
      setLoading(true)
      setError("")

      // Carregar dados do aluno
      const studentRes = await api.get(`/api/students/${user?.id}/dashboard`)

      if (studentRes.data) {
        setStats(studentRes.data.stats || {})
        setRecentGrades(studentRes.data.recentGrades || [])
        setUpcomingClasses(studentRes.data.upcomingClasses || [])
      }
    } catch (err: any) {
      console.error("Erro ao carregar dashboard:", err)
      setError("Não foi possível carregar seus dados")
      // Dados mockados para demonstração
      setStats({
        totalCourses: 1,
        totalDisciplines: 6,
        averageGrade: 7.5,
        attendanceRate: 85,
        pendingAssignments: 2,
        completedDisciplines: 4
      })
      setRecentGrades([
        { disciplineName: "Cálculo 2", grade: 8.5, type: "P1", date: "2024-03-10" },
        { disciplineName: "Física 1", grade: 7.0, type: "P2", date: "2024-03-08" }
      ])
      setUpcomingClasses([
        { disciplineName: "Programação", className: "Sala 101", date: "2024-03-12", time: "14:00" },
        { disciplineName: "Banco de Dados", className: "Lab 3", date: "2024-03-12", time: "16:00" }
      ])
    } finally {
      setLoading(false)
    }
  }

  const getGradeColor = (grade: number) => {
    if (grade >= 7) return "text-green-600"
    if (grade >= 5) return "text-yellow-600"
    return "text-red-600"
  }

  const getAttendanceColor = (rate: number) => {
    if (rate >= 75) return "text-green-600"
    if (rate >= 50) return "text-yellow-600"
    return "text-red-600"
  }

  if (!user) {
    return (
      <div className="flex justify-center items-center h-96">
        <div className="text-center">
          <AlertCircle className="h-12 w-12 text-gray-400 mx-auto mb-3" />
          <p className="text-gray-600">Você precisa estar autenticado para acessar esta página.</p>
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-semibold">Meu Dashboard</h1>
        <p className="text-muted-foreground">
          Bem-vindo, {user.name}! Aqui está seu resumo acadêmico
        </p>
      </div>

      {error && (
        <Alert variant="destructive">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      {/* Cards de Estatísticas */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <Card className="border-l-4 border-l-blue-500">
          <CardContent className="pt-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-500">Média Geral</p>
                <p className={`text-3xl font-bold mt-2 ${getGradeColor(stats.averageGrade)}`}>
                  {stats.averageGrade.toFixed(1)}
                </p>
              </div>
              <div className="p-3 bg-blue-100 rounded-full">
                <TrendingUp className="h-6 w-6 text-blue-600" />
              </div>
            </div>
          </CardContent>
        </Card>

        <Card className="border-l-4 border-l-green-500">
          <CardContent className="pt-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-500">Frequência</p>
                <p className={`text-3xl font-bold mt-2 ${getAttendanceColor(stats.attendanceRate)}`}>
                  {stats.attendanceRate.toFixed(0)}%
                </p>
              </div>
              <div className="p-3 bg-green-100 rounded-full">
                <CheckCircle className="h-6 w-6 text-green-600" />
              </div>
            </div>
            <Progress value={stats.attendanceRate} className="mt-3" />
          </CardContent>
        </Card>

        <Card className="border-l-4 border-l-purple-500">
          <CardContent className="pt-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-500">Disciplinas</p>
                <p className="text-3xl font-bold mt-2">{stats.totalDisciplines}</p>
                <p className="text-sm text-gray-500 mt-1">
                  {stats.completedDisciplines} concluídas
                </p>
              </div>
              <div className="p-3 bg-purple-100 rounded-full">
                <BookOpen className="h-6 w-6 text-purple-600" />
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Conteúdo Principal */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Notas Recentes */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <TrendingUp size={20} />
              Notas Recentes
            </CardTitle>
          </CardHeader>
          <CardContent>
            {loading ? (
              <div className="space-y-4">
                {[1, 2].map((i) => (
                  <Skeleton key={i} className="h-16 w-full" />
                ))}
              </div>
            ) : recentGrades.length === 0 ? (
              <div className="text-center py-8 text-muted-foreground">
                Nenhuma nota recente
              </div>
            ) : (
              <div className="space-y-4">
                {recentGrades.map((grade, index) => (
                  <div key={index} className="flex items-center justify-between p-3 border rounded-lg hover:bg-gray-50">
                    <div>
                      <div className="font-medium">{grade.disciplineName}</div>
                      <div className="text-sm text-gray-500">
                        {grade.type} • {new Date(grade.date).toLocaleDateString('pt-BR')}
                      </div>
                    </div>
                    <div className={`text-xl font-bold ${getGradeColor(grade.grade)}`}>
                      {grade.grade.toFixed(1)}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </CardContent>
        </Card>

        {/* Próximas Aulas */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Calendar size={20} />
              Próximas Aulas
            </CardTitle>
          </CardHeader>
          <CardContent>
            {loading ? (
              <div className="space-y-4">
                {[1, 2].map((i) => (
                  <Skeleton key={i} className="h-16 w-full" />
                ))}
              </div>
            ) : upcomingClasses.length === 0 ? (
              <div className="text-center py-8 text-muted-foreground">
                Nenhuma aula agendada
              </div>
            ) : (
              <div className="space-y-4">
                {upcomingClasses.map((classItem, index) => (
                  <div key={index} className="p-3 border rounded-lg hover:bg-gray-50">
                    <div className="font-medium">{classItem.disciplineName}</div>
                    <div className="text-sm text-gray-500 mt-1">
                      <div className="flex items-center gap-4">
                        <span className="flex items-center gap-1">
                          <Clock size={14} />
                          {classItem.time}
                        </span>
                        <span>{classItem.className}</span>
                      </div>
                      <div className="mt-1">
                        {new Date(classItem.date).toLocaleDateString('pt-BR', {
                          weekday: 'long',
                          day: 'numeric',
                          month: 'long'
                        })}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </CardContent>
        </Card>
      </div>

      {/* Status Geral */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <User size={20} />
            Status Acadêmico
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div className="text-center p-4 bg-blue-50 rounded-lg">
              <div className="text-2xl font-bold text-blue-600">{stats.completedDisciplines}</div>
              <div className="text-sm text-blue-700">Concluídas</div>
            </div>
            <div className="text-center p-4 bg-yellow-50 rounded-lg">
              <div className="text-2xl font-bold text-yellow-600">
                {stats.totalDisciplines - stats.completedDisciplines}
              </div>
              <div className="text-sm text-yellow-700">Em Andamento</div>
            </div>
            <div className="text-center p-4 bg-green-50 rounded-lg">
              <div className="text-2xl font-bold text-green-600">{stats.pendingAssignments}</div>
              <div className="text-sm text-green-700">Tarefas Pendentes</div>
            </div>
            <div className="text-center p-4 bg-purple-50 rounded-lg">
              <div className="text-2xl font-bold text-purple-600">{stats.totalCourses}</div>
              <div className="text-sm text-purple-700">Cursos</div>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}