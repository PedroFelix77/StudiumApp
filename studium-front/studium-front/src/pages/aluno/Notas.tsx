"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { BookOpen, TrendingUp, AlertCircle, Filter } from "lucide-react"
import { api } from "@/services/api"
import { useAuth } from "@/context/AuthContext"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Skeleton } from "@/components/ui/skeleton"

interface DisciplineGrade {
  disciplineId: string
  disciplineName: string
  disciplineCode?: string
  p1?: number
  p2?: number
  final?: number
  average: number
  finalAverage?: number
  status: "APROVADO" | "REPROVADO" | "EM ANDAMENTO"
}

interface CourseGrades {
  courseId: string
  courseName: string
  disciplines: DisciplineGrade[]
}

export default function AlunoNotas() {
  const { user } = useAuth()
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [courses, setCourses] = useState<CourseGrades[]>([])
  const [selectedCourse, setSelectedCourse] = useState<string>("all")
  const [selectedSemester, setSelectedSemester] = useState<string>("all")

  useEffect(() => {
    if (user?.id) {
      loadGrades()
    }
  }, [user])

  const loadGrades = async () => {
    try {
      setLoading(true)
      setError("")

      // Carregar notas do aluno
      const gradesRes = await api.get(`/api/students/${user?.id}/grades`)

      if (gradesRes.data) {
        setCourses(gradesRes.data)
      }
    } catch (err: any) {
      console.error("Erro ao carregar notas:", err)
      setError("Não foi possível carregar suas notas")
      // Dados mockados para demonstração
      setCourses([
        {
          courseId: "1",
          courseName: "Ciência da Computação",
          disciplines: [
            { disciplineId: "1", disciplineName: "Cálculo 2", p1: 8.5, p2: 7.0, average: 7.75, status: "APROVADO" },
            { disciplineId: "2", disciplineName: "Física 1", p1: 6.0, p2: 5.0, average: 5.5, final: 6.0, finalAverage: 5.75, status: "APROVADO" },
            { disciplineId: "3", disciplineName: "Programação", p1: 9.0, p2: 8.5, average: 8.75, status: "APROVADO" },
            { disciplineId: "4", disciplineName: "Banco de Dados", p1: 4.0, p2: 5.0, average: 4.5, status: "EM ANDAMENTO" }
          ]
        }
      ])
    } finally {
      setLoading(false)
    }
  }

  const getStatusColor = (status: string) => {
    switch (status) {
      case "APROVADO": return "bg-green-100 text-green-800 border-green-200"
      case "REPROVADO": return "bg-red-100 text-red-800 border-red-200"
      default: return "bg-yellow-100 text-yellow-800 border-yellow-200"
    }
  }

  const getStatusIcon = (status: string) => {
    switch (status) {
      case "APROVADO": return "✅"
      case "REPROVADO": return "❌"
      default: return "⏳"
    }
  }

  const getGradeColor = (grade?: number) => {
    if (grade === undefined || grade === null) return "text-gray-500"
    if (grade >= 7) return "text-green-600"
    if (grade >= 5) return "text-yellow-600"
    return "text-red-600"
  }

  const filteredCourses = courses.filter(course =>
    selectedCourse === "all" || course.courseId === selectedCourse
  )

  const allDisciplines = filteredCourses.flatMap(course =>
    course.disciplines.map(d => ({ ...d, courseName: course.courseName }))
  )

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
        <h1 className="text-3xl font-semibold">Minhas Notas</h1>
        <p className="text-muted-foreground">
          Consulte suas notas por disciplina
        </p>
      </div>

      {error && (
        <Alert variant="destructive">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      {/* Filtros */}
      <Card>
        <CardContent className="pt-6">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <label className="text-sm font-medium mb-2 block">Curso</label>
              <Select value={selectedCourse} onValueChange={setSelectedCourse}>
                <SelectTrigger>
                  <SelectValue placeholder="Todos os cursos" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">Todos os cursos</SelectItem>
                  {courses.map(course => (
                    <SelectItem key={course.courseId} value={course.courseId}>
                      {course.courseName}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div>
              <label className="text-sm font-medium mb-2 block">Semestre</label>
              <Select value={selectedSemester} onValueChange={setSelectedSemester}>
                <SelectTrigger>
                  <SelectValue placeholder="Todos os semestres" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">Todos os semestres</SelectItem>
                  <SelectItem value="2024.1">2024.1</SelectItem>
                  <SelectItem value="2023.2">2023.2</SelectItem>
                  <SelectItem value="2023.1">2023.1</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div>
              <label className="text-sm font-medium mb-2 block">Status</label>
              <Select defaultValue="all">
                <SelectTrigger>
                  <SelectValue placeholder="Todos os status" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">Todos os status</SelectItem>
                  <SelectItem value="aprovado">Aprovado</SelectItem>
                  <SelectItem value="reprovado">Reprovado</SelectItem>
                  <SelectItem value="andamento">Em andamento</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Tabela de Notas */}
      {loading ? (
        <div className="space-y-4">
          <Skeleton className="h-12 w-full" />
          {[1, 2, 3, 4].map((i) => (
            <Skeleton key={i} className="h-16 w-full" />
          ))}
        </div>
      ) : allDisciplines.length === 0 ? (
        <Card>
          <CardContent className="py-12 text-center">
            <BookOpen className="h-12 w-12 text-gray-300 mx-auto mb-3" />
            <h3 className="font-semibold text-gray-600">Nenhuma nota encontrada</h3>
            <p className="text-gray-500 mt-1 text-sm">
              Você não possui notas registradas no momento
            </p>
          </CardContent>
        </Card>
      ) : (
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <BookOpen size={20} />
              Boletim Acadêmico
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="overflow-x-auto">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Disciplina</TableHead>
                    <TableHead className="text-center">P1</TableHead>
                    <TableHead className="text-center">P2</TableHead>
                    <TableHead className="text-center">Média</TableHead>
                    <TableHead className="text-center">Final</TableHead>
                    <TableHead className="text-center">Média Final</TableHead>
                    <TableHead className="text-center">Status</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {allDisciplines.map((discipline) => (
                    <TableRow key={discipline.disciplineId}>
                      <TableCell className="font-medium">
                        <div>{discipline.disciplineName}</div>
                        {discipline.disciplineCode && (
                          <div className="text-sm text-gray-500">{discipline.disciplineCode}</div>
                        )}
                        <div className="text-xs text-gray-400">{discipline.courseName}</div>
                      </TableCell>

                      <TableCell className="text-center">
                        <span className={`font-bold ${getGradeColor(discipline.p1)}`}>
                          {discipline.p1 !== undefined ? discipline.p1.toFixed(1) : "-"}
                        </span>
                      </TableCell>

                      <TableCell className="text-center">
                        <span className={`font-bold ${getGradeColor(discipline.p2)}`}>
                          {discipline.p2 !== undefined ? discipline.p2.toFixed(1) : "-"}
                        </span>
                      </TableCell>

                      <TableCell className="text-center">
                        <span className={`font-bold ${getGradeColor(discipline.average)}`}>
                          {discipline.average.toFixed(1)}
                        </span>
                      </TableCell>

                      <TableCell className="text-center">
                        <span className={`font-bold ${getGradeColor(discipline.final)}`}>
                          {discipline.final !== undefined ? discipline.final.toFixed(1) : "-"}
                        </span>
                      </TableCell>

                      <TableCell className="text-center">
                        {discipline.finalAverage !== undefined ? (
                          <span className={`font-bold ${getGradeColor(discipline.finalAverage)}`}>
                            {discipline.finalAverage.toFixed(1)}
                          </span>
                        ) : (
                          <span className="text-gray-400">-</span>
                        )}
                      </TableCell>

                      <TableCell className="text-center">
                        <Badge className={`${getStatusColor(discipline.status)}`}>
                          {getStatusIcon(discipline.status)} {discipline.status}
                        </Badge>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>
          </CardContent>
        </Card>
      )}

      {/* Resumo */}
      {allDisciplines.length > 0 && (
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <TrendingUp size={20} />
              Resumo
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              <div className="text-center p-4 bg-blue-50 rounded-lg">
                <div className="text-2xl font-bold text-blue-600">
                  {allDisciplines.length}
                </div>
                <div className="text-sm text-blue-700">Disciplinas</div>
              </div>
              <div className="text-center p-4 bg-green-50 rounded-lg">
                <div className="text-2xl font-bold text-green-600">
                  {allDisciplines.filter(d => d.status === "APROVADO").length}
                </div>
                <div className="text-sm text-green-700">Aprovadas</div>
              </div>
              <div className="text-center p-4 bg-yellow-50 rounded-lg">
                <div className="text-2xl font-bold text-yellow-600">
                  {allDisciplines.filter(d => d.status === "EM ANDAMENTO").length}
                </div>
                <div className="text-sm text-yellow-700">Em Andamento</div>
              </div>
              <div className="text-center p-4 bg-red-50 rounded-lg">
                <div className="text-2xl font-bold text-red-600">
                  {allDisciplines.filter(d => d.status === "REPROVADO").length}
                </div>
                <div className="text-sm text-red-700">Reprovadas</div>
              </div>
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  )
}