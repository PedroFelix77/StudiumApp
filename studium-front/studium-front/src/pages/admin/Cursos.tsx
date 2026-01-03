"use client"

import { useEffect, useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Plus, Trash2, UserPlus, Users } from "lucide-react"
import { courseService } from "@/services/api/courseService"
import type { CourseResponseDTO } from "@/services/api/courseService"
import CourseModal from "@/components/course-modal"
import RegistrationModal from "@/components/course-registration-modal"
import CourseClassesModal from "@/components/CourseClassesModal"

export default function AdminCursos() {
  const [courses, setCourses] = useState<CourseResponseDTO[]>([])
  const [loading, setLoading] = useState(false)
  const [showModal, setShowModal] = useState(false)
  const [showRegistrationModal, setShowRegistrationModal] = useState(false)
  const [selectedCourseId, setSelectedCourseId] = useState<string>("")
  const [openClasses, setOpenClasses] = useState(false)

  const loadCourses = async () => {
    setLoading(true)
    try {
      const res = await courseService.list({ page: 0, size: 50 })
      // A resposta pode vir como res.data.content (paginado) ou res.data (array direto)
      const coursesData = res.data?.content ?? res.data ?? []
      setCourses(Array.isArray(coursesData) ? coursesData : [])
    } catch (e) {
      console.error("Erro ao carregar cursos", e)
      setCourses([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadCourses()
  }, [])

  const handleDelete = async (id: string) => {
    if (!confirm("Deseja remover este curso?")) return
    try {
      await courseService.delete(id)
      loadCourses()
    } catch (err) {
      console.error("Erro ao deletar curso:", err)
      alert("Erro ao remover curso")
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold">Gestão de Cursos</h1>

        <Button onClick={() => setShowModal(true)} className="gap-2">
          <Plus size={18} />
          Adicionar Curso
        </Button>
      </div>

      {loading && <p>Carregando...</p>}

      {!loading && courses.length === 0 && (
        <Card>
          <CardContent className="py-8 text-center text-muted-foreground">
            Nenhum curso encontrado.
          </CardContent>
        </Card>
      )}

      {!loading && courses.length > 0 && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {courses.map(course => (
            <Card key={course.id}>
              <CardHeader>
                <CardTitle>{course.name}</CardTitle>
                <p className="text-sm text-muted-foreground">
                  {course.code_course}
                </p>
              </CardHeader>
              <CardContent className="space-y-2">
                <div className="flex justify-between items-center gap-2">
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => {
                      setSelectedCourseId(course.id)
                      setShowRegistrationModal(true)
                    }}
                    className="gap-2 flex-1"
                  >
                    <UserPlus size={16} />
                    Matricular
                  </Button>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => {
                      setSelectedCourseId(course.id)
                      setOpenClasses(true)
                    }}
                    className="gap-2 flex-1"
                  >
                    <Users size={16} />
                    Turmas
                  </Button>
                  <button
                    onClick={() => handleDelete(course.id)}
                    className="text-red-500 hover:text-red-700 p-1"
                    title="Remover curso"
                  >
                    <Trash2 size={18} />
                  </button>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}

      <CourseModal
        open={showModal}
        onOpenChange={setShowModal}
        onSuccess={loadCourses}
      />

      <RegistrationModal
        open={showRegistrationModal}
        onOpenChange={setShowRegistrationModal}
        courseId={selectedCourseId}
        onSuccess={loadCourses}
      />

      {selectedCourseId && (
        <CourseClassesModal
          open={openClasses}
          onOpenChange={setOpenClasses}
          courseId={selectedCourseId}
        />
      )}
    </div>
  )
}
