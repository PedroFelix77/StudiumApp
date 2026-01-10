"use client"

import { useEffect, useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Plus, Trash2, UserPlus, Users, Building2, BookOpen, GraduationCap, Calendar, AlertCircle } from "lucide-react"
import { courseService } from "@/services/api/courseService"
import type { CourseResponseDTO } from "@/services/api/courseService"
import CourseModal from "@/components/course-modal"
import RegistrationModal from "@/components/course-registration-modal"
import CourseClassesModal from "@/components/CourseClassesModal"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { Separator } from "@/components/ui/separator"
import { Skeleton } from "@/components/ui/skeleton"

interface CourseStats {
  totalClasses?: number
  totalStudents?: number
  activeTeachers?: number
}

export default function DiretorCursos() {
  const [courses, setCourses] = useState<CourseResponseDTO[]>([])
  const [loading, setLoading] = useState(true)
  const [stats, setStats] = useState<CourseStats>({})
  const [showModal, setShowModal] = useState(false)
  const [showRegistrationModal, setShowRegistrationModal] = useState(false)
  const [selectedCourseId, setSelectedCourseId] = useState<string | null>(null)
  const [openClasses, setOpenClasses] = useState(false)
  const [error, setError] = useState<string>("")

  const loadCourses = async () => {
    setLoading(true)
    setError("")
    try {
      const res = await courseService.list({ page: 0, size: 50 })
      const coursesData = res.data?.content ?? res.data ?? []
      setCourses(Array.isArray(coursesData) ? coursesData : [])

      // Calcular estatísticas básicas (simulação - ajuste conforme seus dados reais)
      const totalClasses = coursesData.reduce((acc: number, course: any) =>
        acc + (course.classCount || 0), 0)
      const totalStudents = coursesData.reduce((acc: number, course: any) =>
        acc + (course.studentCount || 0), 0)

      setStats({
        totalClasses,
        totalStudents,
        activeTeachers: Math.floor(Math.random() * 50) + 10 // Simulação
      })
    } catch (e: any) {
      console.error("Erro ao carregar cursos", e)
      setCourses([])
      setError(e.response?.data?.message || "Erro ao carregar cursos")
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadCourses()
  }, [])

  const handleDelete = async (id: string) => {
    if (!confirm("Tem certeza que deseja remover este curso? Esta ação não pode ser desfeita.")) return
    try {
      await courseService.delete(id)
      loadCourses()
    } catch (err: any) {
      console.error("Erro ao deletar curso:", err)
      alert("Erro ao remover curso. Verifique se não há turmas ou alunos vinculados.")
    }
  }

  return (
    <div className="space-y-6">
      {/* Cabeçalho */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Gestão de Cursos</h1>
          <p className="text-muted-foreground">
            Gerencie todos os cursos da instituição
          </p>
        </div>
        <Button onClick={() => setShowModal(true)} className="gap-2">
          <Plus size={18} />
          Novo Curso
        </Button>
      </div>

      {/* Estatísticas */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Card className="bg-gradient-to-r from-blue-50 to-blue-100 border-blue-200">
          <CardContent className="pt-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-blue-700">Total de Cursos</p>
                <p className="text-3xl font-bold mt-2">{courses.length}</p>
              </div>
              <div className="p-3 bg-blue-100 rounded-full">
                <Building2 className="h-6 w-6 text-blue-600" />
              </div>
            </div>
          </CardContent>
        </Card>

        <Card className="bg-gradient-to-r from-green-50 to-green-100 border-green-200">
          <CardContent className="pt-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-green-700">Total de Turmas</p>
                <p className="text-3xl font-bold mt-2">{stats.totalClasses || 0}</p>
              </div>
              <div className="p-3 bg-green-100 rounded-full">
                <Users className="h-6 w-6 text-green-600" />
              </div>
            </div>
          </CardContent>
        </Card>

        <Card className="bg-gradient-to-r from-purple-50 to-purple-100 border-purple-200">
          <CardContent className="pt-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-purple-700">Total de Alunos</p>
                <p className="text-3xl font-bold mt-2">{stats.totalStudents || 0}</p>
              </div>
              <div className="p-3 bg-purple-100 rounded-full">
                <GraduationCap className="h-6 w-6 text-purple-600" />
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {error && (
        <Alert variant="destructive">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      {/* Lista de Cursos */}
      <div>
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-xl font-semibold">Todos os Cursos</h2>
          <Badge variant="outline" className="bg-gray-100">
            {loading ? "Carregando..." : `${courses.length} cursos`}
          </Badge>
        </div>

        {loading ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {[1, 2, 3].map((i) => (
              <Card key={i}>
                <CardHeader>
                  <Skeleton className="h-6 w-3/4" />
                  <Skeleton className="h-4 w-1/2 mt-2" />
                </CardHeader>
                <CardContent>
                  <Skeleton className="h-24 w-full" />
                </CardContent>
              </Card>
            ))}
          </div>
        ) : courses.length === 0 ? (
          <Card>
            <CardContent className="py-12 text-center">
              <Building2 className="h-12 w-12 text-gray-300 mx-auto mb-3" />
              <h3 className="font-semibold text-gray-600">Nenhum curso encontrado</h3>
              <p className="text-gray-500 mt-1 text-sm">
                Comece criando seu primeiro curso
              </p>
              <Button
                onClick={() => setShowModal(true)}
                className="mt-4 gap-2"
                variant="outline"
              >
                <Plus size={16} />
                Criar Primeiro Curso
              </Button>
            </CardContent>
          </Card>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {courses.map(course => (
              <Card
                key={course.id}
                className="hover:shadow-lg transition-all duration-300 hover:-translate-y-1 border-l-4 border-l-blue-500"
              >
                <CardHeader className="pb-3">
                  <div className="flex justify-between items-start">
                    <div>
                      <CardTitle className="flex items-center gap-2">
                        <BookOpen className="h-5 w-5 text-blue-600" />
                        {course.name}
                      </CardTitle>
                      <div className="flex items-center gap-2 mt-2">
                        <Badge variant="secondary" className="text-xs">
                          {course.code_course}
                        </Badge>
                        <Badge variant="outline" className="text-xs">
                          {course.status || "Ativo"}
                        </Badge>
                      </div>
                    </div>
                    <button
                      onClick={() => handleDelete(course.id)}
                      className="text-gray-400 hover:text-red-500 p-1 transition-colors"
                      title="Remover curso"
                    >
                      <Trash2 size={18} />
                    </button>
                  </div>
                </CardHeader>

                <CardContent className="space-y-4">
                  {/* Informações adicionais (se existirem) */}
                  {(course.description || course.coordinator) && (
                    <>
                      <Separator />
                      <div className="space-y-2 text-sm">
                        {course.description && (
                          <p className="text-muted-foreground line-clamp-2">
                            {course.description}
                          </p>
                        )}
                        {course.coordinator && (
                          <div className="flex items-center gap-2 text-muted-foreground">
                            <Users size={14} />
                            <span>Coordenador: {course.coordinator}</span>
                          </div>
                        )}
                      </div>
                    </>
                  )}

                  <Separator />

                  {/* Ações */}
                  <div className="grid grid-cols-2 gap-3">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => {
                        setSelectedCourseId(course.id)
                        setShowRegistrationModal(true)
                      }}
                      className="gap-2 h-9"
                    >
                      <UserPlus size={14} />
                      Matricular
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => {
                        setSelectedCourseId(course.id)
                        setOpenClasses(true)
                      }}
                      className="gap-2 h-9"
                    >
                      <Users size={14} />
                      Turmas
                    </Button>
                  </div>

                  {/* Informações de data (se existirem) */}
                  {(course.createdAt || course.updatedAt) && (
                    <div className="flex items-center justify-between text-xs text-muted-foreground pt-2">
                      {course.createdAt && (
                        <div className="flex items-center gap-1">
                          <Calendar size={12} />
                          Criado em {new Date(course.createdAt).toLocaleDateString()}
                        </div>
                      )}
                    </div>
                  )}
                </CardContent>
              </Card>
            ))}
          </div>
        )}
      </div>

      {/* Modais */}
      {selectedCourseId && (
        <CourseClassesModal
          open={openClasses}
          onOpenChange={setOpenClasses}
          courseId={selectedCourseId}
        />
      )}
      <CourseModal
        open={showModal}
        onOpenChange={setShowModal}
        onSuccess={loadCourses}
      />

      {selectedCourseId && (
        <RegistrationModal
          open={showRegistrationModal}
          onOpenChange={setShowRegistrationModal}
          courseId={selectedCourseId}
          onSuccess={loadCourses}
        />
      )}

      {/* Footer de informações */}
      {courses.length > 0 && (
        <Card className="mt-8 bg-gray-50">
          <CardContent className="pt-6">
            <div className="text-sm text-muted-foreground">
              <div className="flex flex-col sm:flex-row items-center justify-between gap-2">
                <div className="flex items-center gap-2">
                  <AlertCircle size={14} />
                  <span>Total de {courses.length} cursos cadastrados</span>
                </div>
                <div className="flex items-center gap-4">
                  <div className="flex items-center gap-1">
                    <div className="w-3 h-3 rounded-full bg-blue-500"></div>
                    <span>Ativos</span>
                  </div>
                  <div className="flex items-center gap-1">
                    <div className="w-3 h-3 rounded-full bg-gray-400"></div>
                    <span>Inativos</span>
                  </div>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  )
}