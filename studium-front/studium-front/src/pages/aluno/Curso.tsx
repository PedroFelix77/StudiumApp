"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import {
  BookOpen,
  Users,
  Calendar,
  Clock,
  GraduationCap,
  Building2,
  MapPin,
  Mail,
  Phone,
  Globe,
  AlertCircle,
  TrendingUp
} from "lucide-react"
import { api } from "@/services/api"
import { useAuth } from "@/context/AuthContext"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { Progress } from "@/components/ui/progress"
import { Skeleton } from "@/components/ui/skeleton"

interface CourseInfo {
  id: string
  name: string
  code: string
  description: string
  duration: number // em semestres
  currentSemester: number
  workload: number // carga horária total
  completedWorkload: number
  coordinator: {
    name: string
    email: string
    phone?: string
  }
  department: {
    name: string
    building: string
  }
  disciplines: {
    id: string
    name: string
    code: string
    semester: number
    workload: number
    status: "CONCLUIDA" | "CURSANDO" | "PENDENTE"
    grade?: number
  }[]
}

export default function AlunoCurso() {
  const { user } = useAuth()
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [course, setCourse] = useState<CourseInfo | null>(null)

  useEffect(() => {
    if (user?.id) {
      loadCourseInfo()
    }
  }, [user])

  const loadCourseInfo = async () => {
    try {
      setLoading(true)
      setError("")

      // Carregar informações do curso do aluno
      const courseRes = await api.get(`/api/students/${user?.id}/course`)

      if (courseRes.data) {
        setCourse(courseRes.data)
      }
    } catch (err: any) {
      console.error("Erro ao carregar informações do curso:", err)
      setError("Não foi possível carregar as informações do seu curso")
      // Dados mockados para demonstração
      setCourse({
        id: "1",
        name: "Ciência da Computação",
        code: "CC001",
        description: "Bacharelado em Ciência da Computação com foco em desenvolvimento de software, algoritmos e inteligência artificial.",
        duration: 8,
        currentSemester: 4,
        workload: 3200,
        completedWorkload: 1600,
        coordinator: {
          name: "Prof. Dr. Carlos Silva",
          email: "carlos.silva@universidade.edu.br",
          phone: "(11) 99999-9999"
        },
        department: {
          name: "Departamento de Computação",
          building: "Prédio da Tecnologia - Sala 405"
        },
        disciplines: [
          { id: "1", name: "Cálculo 1", code: "CALC1", semester: 1, workload: 60, status: "CONCLUIDA", grade: 8.0 },
          { id: "2", name: "Algoritmos", code: "ALG1", semester: 1, workload: 80, status: "CONCLUIDA", grade: 9.0 },
          { id: "3", name: "Física 1", code: "FIS1", semester: 2, workload: 60, status: "CONCLUIDA", grade: 7.5 },
          { id: "4", name: "Programação OO", code: "POO1", semester: 2, workload: 80, status: "CONCLUIDA", grade: 8.5 },
          { id: "5", name: "Cálculo 2", code: "CALC2", semester: 3, workload: 60, status: "CURSANDO", grade: 7.8 },
          { id: "6", name: "Estrutura de Dados", code: "ED1", semester: 3, workload: 80, status: "CURSANDO" },
          { id: "7", name: "Banco de Dados", code: "BD1", semester: 4, workload: 80, status: "CURSANDO" },
          { id: "8", name: "Redes de Computadores", code: "RC1", semester: 5, workload: 60, status: "PENDENTE" },
        ]
      })
    } finally {
      setLoading(false)
    }
  }

  const getSemesterColor = (semester: number) => {
    const colors = [
      "bg-blue-100 text-blue-800",
      "bg-green-100 text-green-800",
      "bg-yellow-100 text-yellow-800",
      "bg-red-100 text-red-800",
      "bg-purple-100 text-purple-800",
      "bg-pink-100 text-pink-800",
      "bg-indigo-100 text-indigo-800",
      "bg-teal-100 text-teal-800"
    ]
    return colors[(semester - 1) % colors.length]
  }

  const getStatusColor = (status: string) => {
    switch (status) {
      case "CONCLUIDA": return "bg-green-100 text-green-800"
      case "CURSANDO": return "bg-blue-100 text-blue-800"
      case "PENDENTE": return "bg-gray-100 text-gray-800"
      default: return "bg-gray-100 text-gray-800"
    }
  }

  const calculateProgress = () => {
    if (!course) return 0
    const completedDisciplines = course.disciplines.filter(d => d.status === "CONCLUIDA").length
    const totalDisciplines = course.disciplines.length
    return totalDisciplines > 0 ? (completedDisciplines / totalDisciplines) * 100 : 0
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
        <h1 className="text-3xl font-semibold">Meu Curso</h1>
        <p className="text-muted-foreground">
          Informações sobre seu curso e progresso acadêmico
        </p>
      </div>

      {error && (
        <Alert variant="destructive">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      {loading ? (
        <div className="space-y-6">
          <Skeleton className="h-48 w-full" />
          <Skeleton className="h-64 w-full" />
          <Skeleton className="h-96 w-full" />
        </div>
      ) : course ? (
        <>
          {/* Informações do Curso */}
          <Card className="border-l-4 border-l-blue-500">
            <CardContent className="pt-6">
              <div className="flex flex-col md:flex-row gap-6">
                <div className="flex-1">
                  <div className="flex items-center gap-3 mb-4">
                    <div className="p-3 bg-blue-100 rounded-lg">
                      <BookOpen className="h-8 w-8 text-blue-600" />
                    </div>
                    <div>
                      <h2 className="text-2xl font-bold">{course.name}</h2>
                      <div className="flex items-center gap-2 mt-1">
                        <Badge variant="outline" className="bg-blue-50">
                          {course.code}
                        </Badge>
                        <Badge className={getSemesterColor(course.currentSemester)}>
                          {course.currentSemester}º Semestre
                        </Badge>
                      </div>
                    </div>
                  </div>

                  <p className="text-gray-600 mb-6">{course.description}</p>

                  <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                    <div className="text-center p-3 bg-gray-50 rounded-lg">
                      <div className="text-xl font-bold text-gray-800">{course.duration}</div>
                      <div className="text-sm text-gray-600">Semestres</div>
                    </div>
                    <div className="text-center p-3 bg-gray-50 rounded-lg">
                      <div className="text-xl font-bold text-gray-800">{course.workload}h</div>
                      <div className="text-sm text-gray-600">Carga Horária</div>
                    </div>
                    <div className="text-center p-3 bg-gray-50 rounded-lg">
                      <div className="text-xl font-bold text-gray-800">
                        {course.completedWorkload}h
                      </div>
                      <div className="text-sm text-gray-600">Concluídas</div>
                    </div>
                    <div className="text-center p-3 bg-gray-50 rounded-lg">
                      <div className="text-xl font-bold text-gray-800">
                        {Math.round((course.completedWorkload / course.workload) * 100)}%
                      </div>
                      <div className="text-sm text-gray-600">Progresso</div>
                    </div>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Progresso do Curso */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <TrendingUp size={20} />
                Progresso do Curso
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-6">
                <div>
                  <div className="flex justify-between items-center mb-2">
                    <span className="text-sm font-medium">Progresso Geral</span>
                    <span className="font-bold text-blue-600">{calculateProgress().toFixed(1)}%</span>
                  </div>
                  <Progress value={calculateProgress()} className="h-3" />
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <h3 className="font-semibold mb-3 flex items-center gap-2">
                      <Calendar size={18} />
                      Distribuição por Semestre
                    </h3>
                    <div className="space-y-2">
                      {Array.from({ length: course.duration }, (_, i) => i + 1).map(semester => {
                        const semDisciplines = course.disciplines.filter(d => d.semester === semester)
                        const completed = semDisciplines.filter(d => d.status === "CONCLUIDA").length
                        const total = semDisciplines.length

                        return (
                          <div key={semester} className="flex items-center justify-between">
                            <div className="flex items-center gap-2">
                              <Badge className={`text-xs ${getSemesterColor(semester)}`}>
                                {semester}º Semestre
                              </Badge>
                              <span className="text-sm text-gray-600">
                                {completed}/{total} disciplinas
                              </span>
                            </div>
                            <span className="text-sm font-medium">
                              {total > 0 ? Math.round((completed / total) * 100) : 0}%
                            </span>
                          </div>
                        )
                      })}
                    </div>
                  </div>

                  <div>
                    <h3 className="font-semibold mb-3 flex items-center gap-2">
                      <Clock size={18} />
                      Status das Disciplinas
                    </h3>
                    <div className="space-y-4">
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-2">
                          <div className="w-3 h-3 rounded-full bg-green-500"></div>
                          <span>Concluídas</span>
                        </div>
                        <span className="font-semibold">
                          {course.disciplines.filter(d => d.status === "CONCLUIDA").length}
                        </span>
                      </div>
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-2">
                          <div className="w-3 h-3 rounded-full bg-blue-500"></div>
                          <span>Cursando</span>
                        </div>
                        <span className="font-semibold">
                          {course.disciplines.filter(d => d.status === "CURSANDO").length}
                        </span>
                      </div>
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-2">
                          <div className="w-3 h-3 rounded-full bg-gray-400"></div>
                          <span>Pendentes</span>
                        </div>
                        <span className="font-semibold">
                          {course.disciplines.filter(d => d.status === "PENDENTE").length}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Grade Curricular */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <GraduationCap size={20} />
                Grade Curricular
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-6">
                {Array.from({ length: course.duration }, (_, i) => i + 1).map(semester => {
                  const semDisciplines = course.disciplines.filter(d => d.semester === semester)

                  if (semDisciplines.length === 0) return null

                  return (
                    <div key={semester} className="border rounded-lg p-4">
                      <div className="flex items-center gap-2 mb-4">
                        <Badge className={`text-sm ${getSemesterColor(semester)}`}>
                          {semester}º Semestre
                        </Badge>
                        <span className="text-sm text-gray-500">
                          {semDisciplines.length} {semDisciplines.length === 1 ? 'disciplina' : 'disciplinas'}
                        </span>
                      </div>

                      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
                        {semDisciplines.map(discipline => (
                          <Card key={discipline.id} className="border">
                            <CardContent className="p-4">
                              <div className="flex justify-between items-start mb-2">
                                <div>
                                  <div className="font-medium">{discipline.name}</div>
                                  <div className="text-sm text-gray-500">{discipline.code}</div>
                                </div>
                                <Badge className={`text-xs ${getStatusColor(discipline.status)}`}>
                                  {discipline.status}
                                </Badge>
                              </div>

                              <div className="flex justify-between items-center mt-4 text-sm">
                                <div className="flex items-center gap-1 text-gray-600">
                                  <Clock size={14} />
                                  {discipline.workload}h
                                </div>
                                {discipline.grade && (
                                  <div className={`font-bold ${discipline.grade >= 5 ? 'text-green-600' : 'text-red-600'}`}>
                                    {discipline.grade.toFixed(1)}
                                  </div>
                                )}
                              </div>
                            </CardContent>
                          </Card>
                        ))}
                      </div>
                    </div>
                  )
                })}
              </div>
            </CardContent>
          </Card>

          {/* Informações de Contato */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Users size={20} />
                  Coordenação do Curso
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  <div>
                    <div className="font-semibold">{course.coordinator.name}</div>
                    <div className="text-sm text-gray-600">Coordenador</div>
                  </div>

                  <div className="space-y-2">
                    <div className="flex items-center gap-2 text-sm">
                      <Mail size={16} className="text-gray-400" />
                      <a href={`mailto:${course.coordinator.email}`} className="text-blue-600 hover:underline">
                        {course.coordinator.email}
                      </a>
                    </div>
                    {course.coordinator.phone && (
                      <div className="flex items-center gap-2 text-sm">
                        <Phone size={16} className="text-gray-400" />
                        <span>{course.coordinator.phone}</span>
                      </div>
                    )}
                  </div>
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Building2 size={20} />
                  Departamento
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  <div>
                    <div className="font-semibold">{course.department.name}</div>
                    <div className="text-sm text-gray-600">Departamento</div>
                  </div>

                  <div className="space-y-2">
                    <div className="flex items-center gap-2 text-sm">
                      <MapPin size={16} className="text-gray-400" />
                      <span>{course.department.building}</span>
                    </div>
                    <div className="flex items-center gap-2 text-sm">
                      <Globe size={16} className="text-gray-400" />
                      <a href="#" className="text-blue-600 hover:underline">
                        Visite o site do departamento
                      </a>
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>
        </>
      ) : (
        <Card>
          <CardContent className="py-12 text-center">
            <BookOpen className="h-12 w-12 text-gray-300 mx-auto mb-3" />
            <h3 className="font-semibold text-gray-600">Nenhuma informação do curso encontrada</h3>
            <p className="text-gray-500 mt-1 text-sm">
              Você não está matriculado em nenhum curso no momento.
            </p>
          </CardContent>
        </Card>
      )}
    </div>
  )
}