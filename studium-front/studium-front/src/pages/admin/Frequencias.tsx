import { useEffect, useMemo, useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { api } from "@/services/api"
import {
  BookOpen,
  User,
  Percent
} from "lucide-react"

/* =======================
   TIPOS ALINHADOS AO BACKEND
======================= */

type StatusFrequency = "PRESENT" | "ABSENT" | "JUSTIFIED"

type StudentWithRegistration = {
  id: string
  name: string
  registration?: string
  registrationId: string
}

interface FrequencyResponseDTO {
  id: string
  attendanceDate: string
  statusFrequency: StatusFrequency
  studentId: string
  studentName: string
  studentRegistration?: string
  courseId: string
  courseName: string
  disciplineId: string
  disciplineName: string
}

interface CourseGroup {
  courseId: string
  courseName: string
  attendances: FrequencyResponseDTO[]
  totalClasses?: number
  presentCount?: number
  absentCount?: number
  attendanceRate?: number
}

interface StudentGroup {
  studentId: string
  studentName: string
  studentRegistration?: string
  courses: CourseGroup[]
  overallAttendanceRate?: number
}

/* =======================
   COMPONENTE
======================= */

export default function AdminFrequenciasPage() {
  const [selectedCourse, setSelectedCourse] = useState("")
  const [viewMode, setViewMode] = useState<"byCourse" | "byStudent">("byCourse")
  const [frequencies, setFrequencies] = useState<FrequencyResponseDTO[]>([])
  const [courses, setCourses] = useState<{ id: string; name: string }[]>([])
  const [students, setStudents] = useState<StudentWithRegistration[]>([])
  const [loading, setLoading] = useState(false)
  const [classes, setClasses] = useState<{ id: string; name: string }[]>([])
  const [disciplines, setDisciplines] = useState<{ id: string; name: string }[]>([])
  const [attendanceDate, setAttendanceDate] = useState(
    new Date().toISOString().split("T")[0]
  )
  const [selectedClass, setSelectedClass] = useState("")
  const [selectedDiscipline, setSelectedDiscipline] = useState("")

  /* =======================
     LOADERS INICIAIS
  ======================= */

  useEffect(() => {
    if (!selectedCourse) {
      setClasses([])
      setDisciplines([])
      setSelectedClass("")
      setSelectedDiscipline("")
      return
    }

    loadClassesByCourse()
    loadDisciplinesByCourse()
  }, [selectedCourse])

  useEffect(() => {
    if (selectedCourse && selectedClass) {
      loadStudentsByClass()
    } else {
      setStudents([])
    }
  }, [selectedCourse, selectedClass])

  useEffect(() => {
    loadCourses()
  }, [])

  useEffect(() => {
    loadFrequencies()
  }, [
    selectedCourse,
    selectedClass,
    selectedDiscipline
  ])

  async function loadCourses() {
    try {
      const res = await api.get("/api/courses", { params: { page: 0, size: 100 } })
      setCourses(res.data.content || [])
    } catch (err: any) {
      console.error("Erro ao carregar cursos", err)
      if (err?.response?.status === 403) {
        try {
          setCourses([])
        } catch (e) {
          console.error("Erro ao carregar cursos alternativo", e)
        }
      }
    }
  }

  async function loadClassesByCourse() {
    try {
      const res = await api.get(`/api/classes/course/${selectedCourse}`)
      setClasses(res.data)
    } catch (err) {
      console.error("Erro ao carregar turmas", err)
      setClasses([])
    }
  }

  async function loadDisciplinesByCourse() {
    try {
      const res = await api.get(`/api/disciplines/course/${selectedCourse}`)
      setDisciplines(res.data)
    } catch (err) {
      console.error("Erro ao carregar disciplinas", err)
      setDisciplines([])
    }
  }

  async function loadFrequencies() {
    if (!selectedCourse || !selectedClass || !selectedDiscipline) {
      setFrequencies([])
      return
    }
    setLoading(true)
    try {
      const res = await api.get("/api/frequencies/filter", {
        params: {
          courseId: selectedCourse,
          classId: selectedClass,
          disciplineId: selectedDiscipline,
          size: 50
        }
      })

      setFrequencies(res.data.content || [])
    } catch (err) {
      console.error("Erro ao carregar frequências", err)
      setFrequencies([])
    } finally {
      setLoading(false)
    }
  }

  async function loadStudentsByClass() {
    try {
      const res = await api.get(
        `/api/registrations/class/${selectedClass}/students`
      )

      const normalized = res.data.map((r: any) => ({
        id: r.studentId,
        name: r.studentName,
        registration: r.registrationNumber,
        registrationId: r.registrationId
      }))

      setStudents(normalized)
    } catch (err) {
      console.error("Erro ao carregar alunos da turma", err)
      setStudents([])
    }
  }

  async function handleFrequency(
    student: StudentWithRegistration,
    status: StatusFrequency
  ) {
    try {
      if (!selectedDiscipline) {
        console.error("Disciplina não selecionada")
        return
      }
      if (!student.registrationId) {
        console.error("Aluno sem registrationId", student)
        return
      }

      const existing = frequencies.find(
        f =>
          f.studentId === student.id &&
          f.attendanceDate === attendanceDate &&
          f.disciplineId === selectedDiscipline
      )

      if (existing) {
        const res = await api.put(`/api/frequencies/${existing.id}`, {
          statusFrequency: status
        })

        setFrequencies(prev =>
          prev.map(f => (f.id === existing.id ? res.data : f))
        )
      } else {
        const payload = {
          registrationId: student.registrationId,
          disciplineId: selectedDiscipline,
          attendanceDate,
          statusFrequency: status
        }
        console.log("POST /api/frequencies payload:", payload)

        const res = await api.post("/api/frequencies/by-registration", payload)
        setFrequencies(prev => [...prev, res.data])
      }
    } catch (err) {
      console.error("Erro ao lançar frequência", err)
    }
  }

  /* =======================
     AGRUPAMENTO POR CURSO
  ======================= */

  const byCourse = useMemo<Record<string, CourseGroup>>(() => {
    const map: Record<string, CourseGroup> = {}

    frequencies.forEach(f => {
      if (!map[f.courseId]) {
        map[f.courseId] = {
          courseId: f.courseId,
          courseName: f.courseName,
          attendances: []
        }
      }
      map[f.courseId].attendances.push(f)
    })

    Object.values(map).forEach(course => {
      const uniqueDates = new Set(course.attendances.map(a => a.attendanceDate))
      course.totalClasses = uniqueDates.size
      course.presentCount = course.attendances.filter(a => a.statusFrequency === "PRESENT").length
      course.absentCount = course.attendances.filter(a => a.statusFrequency === "ABSENT").length
      course.attendanceRate =
        course.totalClasses > 0
          ? (course.presentCount / course.totalClasses) * 100
          : 0
    })

    return map
  }, [frequencies])

  /* =======================
     AGRUPAMENTO POR ALUNO
  ======================= */

  const byStudent = useMemo<Record<string, StudentGroup>>(() => {
    const map: Record<string, StudentGroup> = {}

    frequencies.forEach(f => {
      if (!map[f.studentId]) {
        map[f.studentId] = {
          studentId: f.studentId,
          studentName: f.studentName,
          studentRegistration: f.studentRegistration,
          courses: []
        }
      }

      let course = map[f.studentId].courses.find(c => c.courseId === f.courseId)
      if (!course) {
        course = {
          courseId: f.courseId,
          courseName: f.courseName,
          attendances: []
        }
        map[f.studentId].courses.push(course)
      }

      course.attendances.push(f)
    })

    Object.values(map).forEach(student => {
      let present = 0
      let total = 0

      student.courses.forEach(course => {
        const uniqueDates = new Set(course.attendances.map(a => a.attendanceDate))
        course.totalClasses = uniqueDates.size
        course.presentCount = course.attendances.filter(a => a.statusFrequency === "PRESENT").length
        course.absentCount = course.attendances.filter(a => a.statusFrequency === "ABSENT").length
        course.attendanceRate =
          course.totalClasses > 0
            ? (course.presentCount / course.totalClasses) * 100
            : 0
        present += course.presentCount
        total += course.totalClasses
      })

      student.overallAttendanceRate = total > 0 ? (present / total) * 100 : 0
    })

    return map
  }, [frequencies])

  const rateColor = (rate: number) => {
    if (rate >= 75) return "text-green-600"
    if (rate >= 50) return "text-yellow-600"
    return "text-red-600"
  }

  const courseToRender = useMemo(() => {
    if (Object.values(byCourse).length > 0) {
      return Object.values(byCourse)
    }

    if (students.length > 0 && selectedCourse) {
      const course = courses.find(c => c.id === selectedCourse)

      return [
        {
          courseId: selectedCourse,
          courseName: course?.name ?? "Curso",
          attendances: []
        }
      ]
    }

    return []
  }, [byCourse, students, selectedCourse, courses])

  /* =======================
     RENDER
  ======================= */

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-semibold">Gestão de Frequências</h1>
        <p className="text-muted-foreground">Visualize e gerencie frequências dos alunos por curso ou por aluno</p>
      </div>

      {/* FILTROS */}
      <Card>
        <CardHeader>
          <CardTitle>Filtros</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <div>
              <label className="text-sm font-medium mb-2 block">Curso</label>
              <select
                className="w-full h-9 rounded-md border border-input bg-transparent px-3 py-1 text-sm"
                value={selectedCourse}
                onChange={e => setSelectedCourse(e.target.value)}
              >
                <option value="">Todos os cursos</option>
                {courses.map(c => (
                  <option key={c.id} value={c.id}>{c.name}</option>
                ))}
              </select>
            </div>

            <div>
              <label className="text-sm font-medium mb-2 block">Turma</label>
              <select
                className="w-full h-9 rounded-md border px-3 text-sm"
                value={selectedClass}
                onChange={e => setSelectedClass(e.target.value)}
                disabled={!selectedCourse}
              >
                <option value="">Selecione</option>
                {classes.map(c => (
                  <option key={c.id} value={c.id}>{c.name}</option>
                ))}
              </select>
            </div>

            <div>
              <label className="text-sm font-medium mb-2 block">Disciplina</label>
              <select
                className="w-full h-9 rounded-md border px-3 text-sm"
                value={selectedDiscipline}
                onChange={e => setSelectedDiscipline(e.target.value)}
                disabled={!selectedClass}
              >
                <option value="">Selecione</option>
                {disciplines.map(d => (
                  <option key={d.id} value={d.id}>{d.name}</option>
                ))}
              </select>
            </div>

            <div>
              <label className="text-sm font-medium mb-2 block">Data da Aula</label>
              <Input
                type="date"
                value={attendanceDate}
                onChange={(e: React.ChangeEvent<HTMLInputElement>) => setAttendanceDate(e.target.value)}
              />
            </div>

            <div className="md:col-span-2">
              <label className="text-sm font-medium mb-2 block">Visualização</label>
              <div className="flex gap-2">
                <Button
                  onClick={() => setViewMode("byCourse")}
                  variant={viewMode === "byCourse" ? "default" : "outline"}
                  className="flex-1"
                >
                  <BookOpen size={16} className="mr-2" />
                  Por Curso
                </Button>
                <Button
                  onClick={() => setViewMode("byStudent")}
                  variant={viewMode === "byStudent" ? "default" : "outline"}
                  className="flex-1"
                >
                  <User size={16} className="mr-2" />
                  Por Aluno
                </Button>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      {loading && (
        <div className="text-center py-8 text-muted-foreground">Carregando...</div>
      )}

      {!loading && viewMode === "byCourse" && (
        <div className="space-y-4">
          {courseToRender.length === 0 ? (
            <Card>
              <CardContent className="py-8 text-center text-muted-foreground">
                Nenhum aluno encontrado para esta turma
              </CardContent>
            </Card>
          ) : (
            courseToRender.map((course, courseIndex) => {
              const studentStats = course.attendances.reduce((acc, att) => {
                if (!acc[att.studentId]) {
                  acc[att.studentId] = {
                    student: {
                      id: att.studentId,
                      name: att.studentName,
                      registration: att.studentRegistration,
                    },
                    present: 0,
                    absent: 0,
                    justified: 0,
                    dates: new Set<string>(),
                  };
                }
                acc[att.studentId].dates.add(att.attendanceDate);
                if (att.statusFrequency === "PRESENT") acc[att.studentId].present++;
                else if (att.statusFrequency === "ABSENT") acc[att.studentId].absent++;
                else if (att.statusFrequency === "JUSTIFIED") acc[att.studentId].justified++;
                return acc;
              }, {} as Record<string, {
                student: { id: string; name: string; registration?: string };
                present: number;
                absent: number;
                justified: number;
                dates: Set<string>;
              }>);

              return (
                <Card key={course.courseId || `course-${courseIndex}`}>
                  <CardHeader>
                    <div className="flex justify-between items-center">
                      <div>
                        <CardTitle className="flex items-center gap-2">
                          <BookOpen size={20} />
                          {course.courseName}
                        </CardTitle>
                        <div className="flex items-center gap-4 mt-2 text-sm text-muted-foreground">
                          {course.totalClasses !== undefined && (
                            <span>{course.totalClasses} {course.totalClasses === 1 ? "aula" : "aulas"}</span>
                          )}
                          <span className="flex items-center gap-1">
                            <span className="text-green-600">{course.presentCount || 0}</span>
                            <span>/</span>
                            <span className="text-red-600">{course.absentCount || 0}</span>
                          </span>
                        </div>
                      </div>
                      {course.attendanceRate !== undefined && (
                        <div className="text-right">
                          <div className={`text-2xl font-bold ${rateColor(course.attendanceRate)}`}>
                            {course.attendanceRate.toFixed(1)}%
                          </div>
                          <div className="text-xs text-muted-foreground flex items-center gap-1">
                            <Percent size={12} />
                            Frequência média
                          </div>
                        </div>
                      )}
                    </div>
                  </CardHeader>
                  <CardContent>
                    <table className="w-full text-left">
                      <thead>
                        <tr className="text-sm text-muted-foreground border-b">
                          <th className="p-2">Aluno</th>
                          <th className="p-2">Matrícula</th>
                          <th className="p-2 text-center">Presente</th>
                          <th className="p-2 text-center">Faltou</th>
                          <th className="p-2 text-center">Justificado</th>
                          <th className="p-2 text-center">Total</th>
                          <th className="p-2 text-center">Frequência</th>
                          <th className="p-2 text-center">Lançar</th>
                        </tr>
                      </thead>
                      <tbody>
                        {students.map((student, studentIndex) => {
                          const stats = studentStats[student.id] || {
                            present: 0,
                            absent: 0,
                            justified: 0,
                            dates: new Set<string>(),
                          };

                          const total = stats.dates.size;
                          const rate =
                            total > 0
                              ? ((stats.present + stats.justified) / total) * 100
                              : 0;

                          return (
                            <tr
                              key={`${course.courseId || 'course'}-${student.id || `student-${studentIndex}`}`}
                              className="border-b hover:bg-muted/50"
                            >
                              <td className="p-2 font-medium">
                                {student.name}
                              </td>
                              <td className="p-2 text-muted-foreground">
                                {student.registration || "-"}
                              </td>
                              <td className="p-2 text-center text-green-600 font-semibold">
                                {stats.present}
                              </td>
                              <td className="p-2 text-center text-red-600 font-semibold">
                                {stats.absent}
                              </td>
                              <td className="p-2 text-center text-blue-600 font-semibold">
                                {stats.justified}
                              </td>
                              <td className="p-2 text-center">{total}</td>
                              <td className="p-2 text-center">
                                <span className={`font-semibold ${rateColor(rate)}`}>
                                  {rate.toFixed(1)}%
                                </span>
                              </td>
                              <td className="p-2 text-center">
                                <div className="flex justify-center gap-2">
                                  <Button onClick={() => handleFrequency(student, "PRESENT")}>
                                    Presente
                                  </Button>

                                  <Button variant="outline" onClick={() => handleFrequency(student, "ABSENT")}>
                                    Falta
                                  </Button>

                                  <Button variant="secondary" onClick={() => handleFrequency(student, "JUSTIFIED")}>
                                    Justificada
                                  </Button>
                                </div>
                              </td>
                            </tr>
                          );
                        })}
                      </tbody>
                    </table>
                  </CardContent>
                </Card>
              );
            })
          )}
        </div>
      )}

      {!loading && viewMode === "byStudent" && (
        <div className="space-y-4">
          {Object.values(byStudent).length === 0 ? (
            <Card>
              <CardContent className="py-8 text-center text-muted-foreground">
                Nenhuma frequência encontrada
              </CardContent>
            </Card>
          ) : (
            Object.values(byStudent).map((student, studentIndex) => (
              <Card key={student.studentId || `student-${studentIndex}`}>
                <CardHeader>
                  <div className="flex justify-between items-center">
                    <div>
                      <CardTitle className="flex items-center gap-2">
                        <User size={20} />
                        {student.studentName}
                        {student.studentRegistration && (
                          <span className="text-muted-foreground font-normal text-base">
                            ({student.studentRegistration})
                          </span>
                        )}
                      </CardTitle>
                    </div>
                    {student.overallAttendanceRate !== undefined && (
                      <div className="text-right">
                        <div className={`text-2xl font-bold ${rateColor(student.overallAttendanceRate)}`}>
                          {student.overallAttendanceRate.toFixed(1)}%
                        </div>
                        <div className="text-xs text-muted-foreground flex items-center gap-1">
                          <Percent size={12} />
                          Frequência geral
                        </div>
                      </div>
                    )}
                  </div>
                </CardHeader>
                <CardContent>
                  <div className="space-y-4">
                    {student.courses.map((course, courseIndex) => (
                      <div
                        key={`${student.studentId || 'student'}-${course.courseId || `course-${courseIndex}`}`}
                        className="border rounded-lg p-4 hover:bg-muted/30 transition-colors"
                      >
                        <div className="flex justify-between items-center mb-3">
                          <h4 className="font-semibold text-lg">{course.courseName}</h4>
                          {course.attendanceRate !== undefined && (
                            <div className="flex items-center gap-3">
                              <div className="text-right">
                                <div className={`text-xl font-bold ${rateColor(course.attendanceRate)}`}>
                                  {course.attendanceRate.toFixed(1)}%
                                </div>
                                <div className="text-xs text-muted-foreground">
                                  {course.presentCount || 0} presente / {course.absentCount || 0} faltou
                                </div>
                              </div>
                            </div>
                          )}
                        </div>
                        {course.totalClasses !== undefined && (
                          <div className="text-sm text-muted-foreground">
                            Total de {course.totalClasses} {course.totalClasses === 1 ? "aula" : "aulas"}
                          </div>
                        )}
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>
            ))
          )}
        </div>
      )}
    </div>
  )
}