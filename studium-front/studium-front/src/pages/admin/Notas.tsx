"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Search, Save } from "lucide-react"
import { api } from "@/services/api"

interface Course {
  id: string
  name: string
}

interface Class {
  id: string
  name: string
}

interface StudentRow {
  registrationId: string
  studentName: string
  enrollment: string
  p1?: number
  p2?: number
  final?: number
  media: number
}

export default function NotasDiretorPage() {
  const [courses, setCourses] = useState<Course[]>([])
  const [classes, setClasses] = useState<Class[]>([])
  const [rows, setRows] = useState<StudentRow[]>([])
  const [search, setSearch] = useState("")

  const [selectedCourse, setSelectedCourse] = useState("")
  const [selectedClass, setSelectedClass] = useState("")

  useEffect(() => {
    loadCourses()
  }, [])

  useEffect(() => {
    if (selectedCourse) {
      loadClasses()
      setSelectedClass("")
      setRows([])
    }
  }, [selectedCourse])

  useEffect(() => {
    if (selectedClass) {
      loadStudentsAndGrades()
    }
  }, [selectedClass])

  async function loadCourses() {
    const res = await api.get("/api/courses", { params: { page: 0, size: 100 } })
    setCourses(res.data.content || [])
  }

  async function loadClasses() {
    try {
      const res = await api.get(`/api/classes/course/${selectedCourse}`)
      const classesData = res.data.content || res.data || []
      setClasses(Array.isArray(classesData) ? classesData : [])
      console.log("Turmas carregadas:", classesData)
    } catch (err) {
      console.error("Erro ao carregar turmas:", err)
      setClasses([])
    }
  }

  async function loadStudentsAndGrades() {
    try {
      // 1️⃣ Alunos da turma
      const studentsRes = await api.get(
        `/api/registrations/class/${selectedClass}/students`
      )

      // A resposta pode vir como array direto ou dentro de data
      const students = Array.isArray(studentsRes.data)
        ? studentsRes.data
        : (studentsRes.data?.content || studentsRes.data || [])

      console.log("Alunos carregados:", students)

      // 2️⃣ Notas da turma - buscar notas do curso e filtrar por classId no frontend
      // Primeiro precisamos do courseId da turma, então vamos buscar todas as notas do curso
      const gradesRes = await api.get("/api/grades/filter/course", {
        params: {
          courseId: selectedCourse,
          page: 0,
          size: 200
        }
      })

      const allGrades = gradesRes.data.content || []
      // Filtrar notas apenas da turma selecionada
      const grades = allGrades.filter((g: any) => g.classId === selectedClass)
      console.log("Notas carregadas:", grades)

      const grouped: Record<string, StudentRow> = {}

      students.forEach((s: any) => {
        grouped[s.registrationId] = {
          registrationId: s.registrationId,
          studentName: s.studentName,
          enrollment: s.registrationNumber || s.enrollment,
          media: 0
        }
      })

      grades.forEach((g: any) => {
        const row = grouped[g.registrationId]
        if (!row) return

        if (g.typeGrade === "PROVA1" || g.type === "PROVA1") row.p1 = g.grade || g.value
        if (g.typeGrade === "PROVA2" || g.type === "PROVA2") row.p2 = g.grade || g.value
        if (g.typeGrade === "FINAL" || g.type === "FINAL") row.final = g.grade || g.value
      })

      const parsed = Object.values(grouped).map((r) => {
        const p1 = r.p1 ?? 0
        const p2 = r.p2 ?? 0
        return {
          ...r,
          media: Number(((p1 + p2) / 2).toFixed(1))
        }
      })

      console.log("Rows processados:", parsed)
      setRows(parsed)
    } catch (err) {
      console.error("Erro ao carregar alunos e notas:", err)
      setRows([])
    }
  }

  const updateValue = (
    id: string,
    field: "p1" | "p2" | "final",
    value: string
  ) => {
    const num = Number(value)
    if (num < 0 || num > 10) return

    setRows((prev) =>
      prev.map((r) =>
        r.registrationId === id
          ? {
            ...r,
            [field]: num,
            media: Number((((r.p1 ?? 0) + (r.p2 ?? 0)) / 2).toFixed(1))
          }
          : r
      )
    )
  }

  const saveGrades = async () => {
    for (const r of rows) {
      if (r.p1 !== undefined) {
        await api.post("/api/grades", {
          registrationId: r.registrationId,
          typeGrade: "PROVA1",
          grade: r.p1
        })
      }

      if (r.p2 !== undefined) {
        await api.post("/api/grades", {
          registrationId: r.registrationId,
          typeGrade: "PROVA2",
          grade: r.p2
        })
      }

      if (r.media < 7 && r.final !== undefined) {
        await api.post("/api/grades", {
          registrationId: r.registrationId,
          typeGrade: "FINAL",
          grade: r.final
        })
      }
    }

    loadStudentsAndGrades()
  }

  const filtered = rows.filter(
    (r) =>
      r.studentName.toLowerCase().includes(search.toLowerCase()) ||
      r.enrollment.includes(search)
  )

  return (
    <Card>
      <CardHeader className="flex justify-between items-center">
        <CardTitle>Notas por Turma</CardTitle>
        <Button onClick={saveGrades} className="gap-2">
          <Save size={18} /> Salvar
        </Button>
      </CardHeader>

      <CardContent>
        {/* Curso */}
        <label className="text-sm font-medium">Curso</label>
        <select
          className="w-full h-9 border rounded-md mb-4"
          value={selectedCourse}
          onChange={(e) => setSelectedCourse(e.target.value)}
        >
          <option value="">Selecione o curso</option>
          {courses.map((c) => (
            <option key={c.id} value={c.id}>
              {c.name}
            </option>
          ))}
        </select>

        {/* Turma */}
        {classes.length > 0 && (
          <>
            <label className="text-sm font-medium">Turma</label>
            <select
              className="w-full h-9 border rounded-md mb-4"
              value={selectedClass}
              onChange={(e) => setSelectedClass(e.target.value)}
            >
              <option value="">Selecione a turma</option>
              {classes.map((t) => (
                <option key={t.id} value={t.id}>
                  {t.name}
                </option>
              ))}
            </select>
          </>
        )}

        {/* Busca */}
        {rows.length > 0 && (
          <>
            <div className="relative mb-4">
              <Search className="absolute left-3 top-2.5 h-5 w-5 text-gray-400" />
              <Input
                placeholder="Buscar aluno..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="pl-10"
              />
            </div>

            {/* Tabela */}
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b">
                  <th>Aluno</th>
                  <th>Matrícula</th>
                  <th>P1</th>
                  <th>P2</th>
                  <th>Média</th>
                  <th>Final</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((r) => (
                  <tr key={r.registrationId} className="border-b">
                    <td>{r.studentName}</td>
                    <td>{r.enrollment}</td>
                    <td>
                      <Input
                        type="number"
                        value={r.p1 ?? ""}
                        onChange={(e) =>
                          updateValue(r.registrationId, "p1", e.target.value)
                        }
                        className="w-16 text-center"
                      />
                    </td>
                    <td>
                      <Input
                        type="number"
                        value={r.p2 ?? ""}
                        onChange={(e) =>
                          updateValue(r.registrationId, "p2", e.target.value)
                        }
                        className="w-16 text-center"
                      />
                    </td>
                    <td
                      className={`font-semibold ${r.media >= 7 ? "text-green-600" : "text-red-600"
                        }`}
                    >
                      {r.media}
                    </td>
                    <td>
                      {r.media < 7 ? (
                        <Input
                          type="number"
                          value={r.final ?? ""}
                          onChange={(e) =>
                            updateValue(r.registrationId, "final", e.target.value)
                          }
                          className="w-16 text-center"
                        />
                      ) : (
                        "—"
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </>
        )}
      </CardContent>
    </Card>
  )
}
