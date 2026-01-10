"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { BookOpen, Users, Calendar, Clock } from "lucide-react"
import { api } from "@/services/api"
import { useAuth } from "@/context/AuthContext"
import { Badge } from "@/components/ui/badge"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { Button } from "@/components/ui/button"
import { Link } from "react-router-dom" // ou use o seu sistema de navegação

interface Class {
  id: string
  name: string
  codeClass?: string
  academicYear?: string
}

interface Discipline {
  id: string
  name: string
  code?: string
  workload?: number
}

interface ClassWithDisciplines extends Class {
  disciplines: Discipline[]
  studentCount?: number
}

export default function ProfessorTurmas() {
  const { user } = useAuth()
  const [classes, setClasses] = useState<ClassWithDisciplines[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string>("")

  useEffect(() => {
    if (user?.id) {
      loadTurmas()
    }
  }, [user])

  async function loadTurmas() {
    try {
      setLoading(true)
      setError("")

      // 1. Buscar turmas do professor
      const classesRes = await api.get(`/api/teacher-classes/my/classes`)
      const classesData = classesRes.data.content || classesRes.data || []

      // 2. Para cada turma, buscar disciplinas e alunos
      const classesWithDetails: ClassWithDisciplines[] = []

      for (const classItem of classesData) {
        try {
          // Buscar disciplinas desta turma
          const discRes = await api.get(`/api/teacher-classes/my/disciplines`, {
            params: { classId: classItem.id }
          })

          // Buscar quantidade de alunos na turma
          const studentsRes = await api.get(`/api/registrations/class/${classItem.id}/students`)
          const studentCount = Array.isArray(studentsRes.data) ? studentsRes.data.length : 0

          classesWithDetails.push({
            ...classItem,
            disciplines: discRes.data || [],
            studentCount
          })
        } catch (err) {
          console.error(`Erro ao carregar detalhes da turma ${classItem.id}:`, err)
          classesWithDetails.push({
            ...classItem,
            disciplines: [],
            studentCount: 0
          })
        }
      }

      setClasses(classesWithDetails)

      if (classesWithDetails.length === 0) {
        setError("Você não está alocado em nenhuma turma no momento.")
      }
    } catch (err: any) {
      console.error("Erro ao carregar turmas", err)
      setError("Não foi possível carregar suas turmas")
      setClasses([])
    } finally {
      setLoading(false)
    }
  }

  if (!user) {
    return (
      <div className="flex justify-center items-center h-96">
        <div className="text-center">
          <p className="text-gray-600">Você precisa estar autenticado para acessar esta página.</p>
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-semibold">Minhas Turmas</h1>
        <p className="text-muted-foreground">
          Gerencie suas disciplinas e alunos
          <span className="block text-sm text-gray-500 mt-1">
            Professor: <span className="font-medium">{user.name}</span>
          </span>
        </p>
      </div>

      {error && (
        <Alert variant="destructive">
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      {loading ? (
        <div className="text-center py-12">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="text-gray-500 mt-4">Carregando suas turmas...</p>
        </div>
      ) : classes.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {classes.map((classItem) => (
            <Card
              key={classItem.id}
              className="hover:shadow-lg transition-shadow border-l-4 border-l-blue-500"
            >
              <CardHeader>
                <div className="flex justify-between items-start">
                  <div>
                    <CardTitle className="flex items-center gap-2">
                      <Users size={20} />
                      {classItem.name}
                    </CardTitle>
                    <div className="text-sm text-muted-foreground mt-1">
                      {classItem.codeClass && `Código: ${classItem.codeClass}`}
                      {classItem.academicYear && ` • Ano: ${classItem.academicYear}`}
                    </div>
                  </div>
                  <Badge variant="outline" className="bg-blue-50 text-blue-700">
                    {classItem.studentCount} {classItem.studentCount === 1 ? 'aluno' : 'alunos'}
                  </Badge>
                </div>
              </CardHeader>
              <CardContent className="space-y-4">
                {/* Disciplinas */}
                <div>
                  <div className="flex items-center gap-2 text-sm font-medium mb-2">
                    <BookOpen size={16} />
                    Disciplinas
                  </div>
                  {classItem.disciplines.length === 0 ? (
                    <p className="text-sm text-muted-foreground italic">Nenhuma disciplina atribuída</p>
                  ) : (
                    <div className="space-y-2">
                      {classItem.disciplines.map((discipline) => (
                        <div
                          key={discipline.id}
                          className="flex items-center justify-between p-2 bg-gray-50 rounded hover:bg-gray-100"
                        >
                          <div>
                            <div className="font-medium">{discipline.name}</div>
                            {discipline.code && (
                              <div className="text-xs text-gray-500">Código: {discipline.code}</div>
                            )}
                          </div>
                          <div className="flex items-center gap-2">
                            {discipline.workload && (
                              <Badge variant="secondary" className="text-xs">
                                <Clock size={12} className="mr-1" />
                                {discipline.workload}h
                              </Badge>
                            )}
                            {/* Botões para ações rápidas */}
                            <div className="flex gap-1">
                              <Button
                                size="sm"
                                variant="outline"
                                className="h-7 text-xs"
                                asChild
                              >
                                <Link to={`/professor/notas?classId=${classItem.id}&disciplineId=${discipline.id}`}>
                                  Notas
                                </Link>
                              </Button>
                              <Button
                                size="sm"
                                variant="outline"
                                className="h-7 text-xs"
                                asChild
                              >
                                <Link to={`/professor/frequencia?classId=${classItem.id}&disciplineId=${discipline.id}`}>
                                  Frequência
                                </Link>
                              </Button>
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>

                {/* Ações rápidas */}
                <div className="pt-3 border-t">
                  <div className="grid grid-cols-2 gap-2">
                    <Button
                      variant="outline"
                      size="sm"
                      className="w-full text-xs"
                      asChild
                    >
                      <Link to={`/professor/notas?classId=${classItem.id}`}>
                        Ver Todas Notas
                      </Link>
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      className="w-full text-xs"
                      asChild
                    >
                      <Link to={`/professor/frequencia?classId=${classItem.id}`}>
                        Ver Todas Frequências
                      </Link>
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      ) : (
        <Card>
          <CardContent className="py-12 text-center">
            <Users className="h-12 w-12 text-gray-300 mx-auto mb-3" />
            <h3 className="font-semibold text-gray-600">Nenhuma turma encontrada</h3>
            <p className="text-gray-500 mt-1 text-sm">
              Você não está alocado em nenhuma turma no momento.
            </p>
            <p className="text-gray-500 text-xs mt-2">
              Entre em contato com a coordenação para ser alocado em uma turma.
            </p>
          </CardContent>
        </Card>
      )}

      {/* Resumo */}
      {classes.length > 0 && (
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Calendar size={20} />
              Resumo
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="bg-blue-50 p-4 rounded-lg">
                <div className="text-2xl font-bold text-blue-700">
                  {classes.length}
                </div>
                <div className="text-sm text-blue-600">Turmas Ativas</div>
              </div>
              <div className="bg-green-50 p-4 rounded-lg">
                <div className="text-2xl font-bold text-green-700">
                  {classes.reduce((acc, c) => acc + c.disciplines.length, 0)}
                </div>
                <div className="text-sm text-green-600">Disciplinas Ministradas</div>
              </div>
              <div className="bg-purple-50 p-4 rounded-lg">
                <div className="text-2xl font-bold text-purple-700">
                  {classes.reduce((acc, c) => acc + (c.studentCount || 0), 0)}
                </div>
                <div className="text-sm text-purple-600">Alunos no Total</div>
              </div>
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  )
}