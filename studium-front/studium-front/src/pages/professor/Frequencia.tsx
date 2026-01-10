"use client"

import { useEffect, useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { api } from "@/services/api"
import { useAuth } from "@/context/AuthContext"
import {
  BookOpen,
  User,
  Percent,
  Calendar,
  CheckCircle,
  XCircle,
  Clock,
  AlertCircle,
  Loader2
} from "lucide-react"
import { Alert, AlertDescription } from "@/components/ui/alert"

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
  classId: string
  className: string
}

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

/* =======================
   COMPONENTE PROFESSOR
======================= */

export default function ProfessorFrequencias() {
  const { user } = useAuth()
  const [frequencies, setFrequencies] = useState<FrequencyResponseDTO[]>([])
  const [students, setStudents] = useState<StudentWithRegistration[]>([])
  const [loading, setLoading] = useState(false)
  const [classes, setClasses] = useState<Class[]>([])
  const [disciplines, setDisciplines] = useState<Discipline[]>([])
  const [attendanceDate, setAttendanceDate] = useState(
    new Date().toISOString().split("T")[0]
  )
  const [selectedClass, setSelectedClass] = useState("")
  const [selectedDiscipline, setSelectedDiscipline] = useState("")
  const [error, setError] = useState<string>("")
  const [saving, setSaving] = useState<string | null>(null)
  const [todayStats, setTodayStats] = useState({
    present: 0,
    absent: 0,
    justified: 0,
    total: 0,
    rate: 0
  })

  /* =======================
     LOADERS INICIAIS
  ======================= */

  // Carrega turmas do professor ao iniciar
  useEffect(() => {
    if (user?.id) {
      loadClasses()
    }
  }, [user])

  // Carrega disciplinas quando seleciona turma
  useEffect(() => {
    if (selectedClass && user?.id) {
      loadDisciplines()
    } else {
      setDisciplines([])
      setSelectedDiscipline("")
    }
  }, [selectedClass, user])

  // Carrega alunos quando seleciona turma
  useEffect(() => {
    if (selectedClass) {
      loadStudentsByClass()
    } else {
      setStudents([])
    }
  }, [selectedClass])

  // Carrega frequências quando seleciona turma, disciplina ou data
  useEffect(() => {
    if (selectedClass && selectedDiscipline) {
      loadFrequencies()
    } else {
      setFrequencies([])
    }
  }, [selectedClass, selectedDiscipline, attendanceDate])

  // Atualiza estatísticas do dia quando frequências mudam
  useEffect(() => {
    updateTodayStats()
  }, [frequencies, attendanceDate])

  async function loadClasses() {
    if (!user?.id) return

    try {
      // Buscar turmas do professor logado usando o endpoint /my/classes
      const res = await api.get(`/api/teacher-classes/my/classes`)
      const classesData = res.data.content || res.data || []
      setClasses(Array.isArray(classesData) ? classesData : [])

      if (classesData.length === 0) {
        setError("Você não está alocado em nenhuma turma")
      } else {
        setError("")
      }
    } catch (err: any) {
      console.error("Erro ao carregar turmas", err)
      setClasses([])
      setError("Não foi possível carregar suas turmas")
    }
  }

  async function loadDisciplines() {
    if (!user?.id || !selectedClass) return

    try {
      // Buscar disciplinas do professor na turma selecionada usando /my/disciplines
      const res = await api.get(`/api/teacher-classes/my/disciplines`, {
        params: { classId: selectedClass }
      })

      setDisciplines(res.data || [])

      if (res.data.length === 0) {
        setError("Você não leciona disciplinas nesta turma")
      } else {
        setError("")
      }
    } catch (err) {
      console.error("Erro ao carregar disciplinas", err)
      setDisciplines([])
    }
  }

  async function loadFrequencies() {
    if (!selectedClass || !selectedDiscipline) {
      setFrequencies([])
      return
    }

    setLoading(true)
    try {
      // Buscar frequências da turma e disciplina para a data selecionada
      const res = await api.get("/api/frequencies/filter", {
        params: {
          classId: selectedClass,
          disciplineId: selectedDiscipline,
          attendanceDate: attendanceDate,
          size: 100
        }
      })

      setFrequencies(res.data.content || res.data || [])
    } catch (err) {
      console.error("Erro ao carregar frequências", err)
      setFrequencies([])
      setError("Não foi possível carregar as frequências")
    } finally {
      setLoading(false)
    }
  }

  async function loadStudentsByClass() {
    if (!selectedClass) return

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
      setError("Não foi possível carregar os alunos da turma")
    }
  }

  async function handleFrequency(
    student: StudentWithRegistration,
    status: StatusFrequency
  ) {
    if (!selectedDiscipline || !student.registrationId) {
      setError("Disciplina não selecionada ou aluno sem matrícula")
      return
    }

    setSaving(student.id)

    try {
      const existing = frequencies.find(
        f =>
          f.studentId === student.id &&
          f.attendanceDate === attendanceDate &&
          f.disciplineId === selectedDiscipline
      )

      if (existing) {
        // Atualizar frequência existente
        const res = await api.put(`/api/frequencies/${existing.id}`, {
          statusFrequency: status
        })

        setFrequencies(prev =>
          prev.map(f => (f.id === existing.id ? res.data : f))
        )
      } else {
        // Criar nova frequência
        const payload = {
          registrationId: student.registrationId,
          disciplineId: selectedDiscipline,
          classId: selectedClass,
          attendanceDate,
          statusFrequency: status
        }

        const res = await api.post("/api/frequencies/by-registration", payload)
        setFrequencies(prev => [...prev, res.data])
      }
    } catch (err) {
      console.error("Erro ao lançar frequência", err)
      setError("Erro ao salvar frequência. Tente novamente.")
    } finally {
      setSaving(null)
    }
  }

  function updateTodayStats() {
    const todayFrequencies = frequencies.filter(f => f.attendanceDate === attendanceDate)
    const presentCount = todayFrequencies.filter(f => f.statusFrequency === "PRESENT").length
    const absentCount = todayFrequencies.filter(f => f.statusFrequency === "ABSENT").length
    const justifiedCount = todayFrequencies.filter(f => f.statusFrequency === "JUSTIFIED").length
    const totalToday = presentCount + absentCount + justifiedCount

    setTodayStats({
      present: presentCount,
      absent: absentCount,
      justified: justifiedCount,
      total: totalToday,
      rate: totalToday > 0 ? (presentCount / totalToday) * 100 : 0
    })
  }

  // Verifica se já tem frequência lançada para o aluno na data atual
  const getStudentFrequencyStatus = (studentId: string) => {
    const todayFrequency = frequencies.find(
      f => f.studentId === studentId && f.attendanceDate === attendanceDate
    )
    return todayFrequency ? todayFrequency.statusFrequency : null
  }

  const rateColor = (rate: number) => {
    if (rate >= 75) return "text-green-600"
    if (rate >= 50) return "text-yellow-600"
    return "text-red-600"
  }

  const statusColor = (status: StatusFrequency | null) => {
    switch (status) {
      case "PRESENT": return "bg-green-100 text-green-800 border-green-200"
      case "ABSENT": return "bg-red-100 text-red-800 border-red-200"
      case "JUSTIFIED": return "bg-blue-100 text-blue-800 border-blue-200"
      default: return "bg-gray-100 text-gray-800 border-gray-200"
    }
  }

  const statusIcon = (status: StatusFrequency | null) => {
    switch (status) {
      case "PRESENT": return <CheckCircle size={14} />
      case "ABSENT": return <XCircle size={14} />
      case "JUSTIFIED": return <Clock size={14} />
      default: return null
    }
  }

  const statusText = (status: StatusFrequency | null) => {
    switch (status) {
      case "PRESENT": return "Presente"
      case "ABSENT": return "Falta"
      case "JUSTIFIED": return "Justificada"
      default: return "Não registrado"
    }
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
        <h1 className="text-3xl font-semibold">Controle de Frequência</h1>
        <p className="text-muted-foreground">Registre e consulte a frequência dos alunos</p>
        <p className="text-sm text-gray-500 mt-1">
          Professor: <span className="font-medium">{user.name}</span>
        </p>
      </div>

      {/* FILTROS */}
      <Card>
        <CardHeader>
          <CardTitle>Filtros</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {/* Mensagem de erro */}
          {error && (
            <Alert variant="destructive">
              <AlertCircle className="h-4 w-4" />
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}

          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            {/* Turma */}
            <div>
              <label className="text-sm font-medium mb-2 block">Turma</label>
              <select
                className="w-full h-9 rounded-md border border-input bg-transparent px-3 py-1 text-sm"
                value={selectedClass}
                onChange={e => setSelectedClass(e.target.value)}
                disabled={loading}
              >
                <option value="">Selecione a turma</option>
                {classes.map(c => (
                  <option key={c.id} value={c.id}>
                    {c.name} {c.codeClass ? `(${c.codeClass})` : ''}
                  </option>
                ))}
              </select>
            </div>

            {/* Disciplina */}
            {selectedClass && (
              <div>
                <label className="text-sm font-medium mb-2 block">Disciplina</label>
                <select
                  className="w-full h-9 rounded-md border px-3 text-sm"
                  value={selectedDiscipline}
                  onChange={e => setSelectedDiscipline(e.target.value)}
                  disabled={!selectedClass || loading}
                >
                  <option value="">Selecione a disciplina</option>
                  {disciplines.map(d => (
                    <option key={d.id} value={d.id}>
                      {d.name} {d.code ? `(${d.code})` : ''}
                    </option>
                  ))}
                </select>
              </div>
            )}

            {/* Data da Aula */}
            <div>
              <label className="text-sm font-medium mb-2 block flex items-center gap-2">
                <Calendar size={14} />
                Data da Aula
              </label>
              <Input
                type="date"
                value={attendanceDate}
                onChange={(e: React.ChangeEvent<HTMLInputElement>) => setAttendanceDate(e.target.value)}
                disabled={loading}
              />
            </div>

            {/* Resumo do dia */}
            <div className="md:col-span-1">
              <label className="text-sm font-medium mb-2 block">Resumo do Dia</label>
              <div className="bg-gray-50 p-3 rounded-md border">
                <div className="flex items-center justify-between">
                  <span className="text-sm text-gray-600">Presentes:</span>
                  <span className="font-semibold text-green-600">{todayStats.present}</span>
                </div>
                <div className="flex items-center justify-between mt-1">
                  <span className="text-sm text-gray-600">Faltas:</span>
                  <span className="font-semibold text-red-600">{todayStats.absent}</span>
                </div>
                <div className="flex items-center justify-between mt-1">
                  <span className="text-sm text-gray-600">Justificadas:</span>
                  <span className="font-semibold text-blue-600">{todayStats.justified}</span>
                </div>
                <div className="flex items-center justify-between mt-2 pt-2 border-t">
                  <span className="text-sm text-gray-600">Frequência:</span>
                  <span className={`font-bold ${rateColor(todayStats.rate)}`}>
                    {todayStats.rate.toFixed(1)}%
                  </span>
                </div>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* CONTEÚDO PRINCIPAL */}
      {loading ? (
        <div className="text-center py-12">
          <Loader2 className="h-8 w-8 animate-spin text-gray-400 mx-auto" />
          <p className="text-gray-500 mt-2">Carregando frequências...</p>
        </div>
      ) : selectedClass && selectedDiscipline ? (
        <div className="space-y-4">
          {students.length === 0 ? (
            <Card>
              <CardContent className="py-8 text-center text-muted-foreground">
                <User className="h-12 w-12 text-gray-300 mx-auto mb-3" />
                <p>Nenhum aluno encontrado nesta turma.</p>
                <p className="text-sm mt-1">Verifique se há alunos matriculados.</p>
              </CardContent>
            </Card>
          ) : (
            <Card>
              <CardHeader>
                <div className="flex justify-between items-center">
                  <div>
                    <CardTitle className="flex items-center gap-2">
                      <BookOpen size={20} />
                      Lista de Chamada
                    </CardTitle>
                    <div className="flex items-center gap-4 mt-2 text-sm text-muted-foreground">
                      <span>Total de alunos: {students.length}</span>
                      <span className="flex items-center gap-1">
                        <CheckCircle size={14} className="text-green-600" />
                        <span className="text-green-600">{todayStats.present} presentes</span>
                      </span>
                      <span className="flex items-center gap-1">
                        <XCircle size={14} className="text-red-600" />
                        <span className="text-red-600">{todayStats.absent} faltas</span>
                      </span>
                    </div>
                  </div>
                  <div className="text-right">
                    <div className={`text-2xl font-bold ${rateColor(todayStats.rate)}`}>
                      {todayStats.rate.toFixed(1)}%
                    </div>
                    <div className="text-xs text-muted-foreground flex items-center gap-1">
                      <Percent size={12} />
                      Frequência do dia
                    </div>
                  </div>
                </div>
              </CardHeader>
              <CardContent>
                <div className="overflow-x-auto">
                  <table className="w-full text-left">
                    <thead>
                      <tr className="text-sm text-muted-foreground border-b">
                        <th className="p-3">Aluno</th>
                        <th className="p-3">Matrícula</th>
                        <th className="p-3 text-center">Status Atual</th>
                        <th className="p-3 text-center">Ações</th>
                      </tr>
                    </thead>
                    <tbody>
                      {students.map((student, index) => {
                        const currentStatus = getStudentFrequencyStatus(student.id)

                        return (
                          <tr
                            key={student.id || `student-${index}`}
                            className="border-b hover:bg-gray-50 transition-colors"
                          >
                            <td className="p-3 font-medium">
                              {student.name}
                            </td>
                            <td className="p-3 text-gray-600">
                              {student.registration || "-"}
                            </td>
                            <td className="p-3 text-center">
                              <div className={`inline-flex items-center gap-2 px-3 py-1 rounded-full text-sm font-medium border ${statusColor(currentStatus)}`}>
                                {statusIcon(currentStatus)}
                                {statusText(currentStatus)}
                              </div>
                            </td>
                            <td className="p-3">
                              <div className="flex justify-center gap-2">
                                <Button
                                  size="sm"
                                  variant={currentStatus === "PRESENT" ? "default" : "outline"}
                                  onClick={() => handleFrequency(student, "PRESENT")}
                                  disabled={saving === student.id}
                                  className="min-w-[100px]"
                                >
                                  {saving === student.id ? (
                                    <Loader2 className="h-4 w-4 animate-spin" />
                                  ) : (
                                    "Presente"
                                  )}
                                </Button>

                                <Button
                                  size="sm"
                                  variant={currentStatus === "ABSENT" ? "destructive" : "outline"}
                                  onClick={() => handleFrequency(student, "ABSENT")}
                                  disabled={saving === student.id}
                                  className="min-w-[100px]"
                                >
                                  {saving === student.id ? (
                                    <Loader2 className="h-4 w-4 animate-spin" />
                                  ) : (
                                    "Falta"
                                  )}
                                </Button>

                                <Button
                                  size="sm"
                                  variant={currentStatus === "JUSTIFIED" ? "secondary" : "outline"}
                                  onClick={() => handleFrequency(student, "JUSTIFIED")}
                                  disabled={saving === student.id}
                                  className="min-w-[100px]"
                                >
                                  {saving === student.id ? (
                                    <Loader2 className="h-4 w-4 animate-spin" />
                                  ) : (
                                    "Justificada"
                                  )}
                                </Button>
                              </div>
                            </td>
                          </tr>
                        )
                      })}
                    </tbody>
                  </table>
                </div>

                {/* Legenda */}
                <div className="mt-6 p-4 bg-gray-50 rounded-lg border">
                  <h4 className="font-medium text-gray-800 mb-2 flex items-center gap-2">
                    <AlertCircle size={16} />
                    Legenda
                  </h4>
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-3 text-sm">
                    <div className="flex items-center gap-2">
                      <div className="w-3 h-3 rounded-full bg-green-600"></div>
                      <span className="text-gray-700">
                        <strong>Presente:</strong> Aluno compareceu à aula
                      </span>
                    </div>
                    <div className="flex items-center gap-2">
                      <div className="w-3 h-3 rounded-full bg-red-600"></div>
                      <span className="text-gray-700">
                        <strong>Falta:</strong> Aluno não compareceu sem justificativa
                      </span>
                    </div>
                    <div className="flex items-center gap-2">
                      <div className="w-3 h-3 rounded-full bg-blue-600"></div>
                      <span className="text-gray-700">
                        <strong>Justificada:</strong> Falta com justificativa válida
                      </span>
                    </div>
                  </div>
                  <div className="mt-3 text-xs text-gray-500">
                    <p>Nota: A frequência é registrada por data. Para alterar, selecione o aluno e clique no status desejado.</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          )}
        </div>
      ) : (
        <Card>
          <CardContent className="py-12 text-center">
            <Calendar className="h-12 w-12 text-gray-300 mx-auto mb-3" />
            <p className="text-gray-600 mb-2">Selecione uma turma e disciplina</p>
            <p className="text-sm text-gray-500">
              1. Escolha uma turma onde você leciona<br />
              2. Selecione uma disciplina que você ensina nesta turma<br />
              3. Registre a frequência dos alunos
            </p>
            {classes.length === 0 && (
              <Alert className="mt-4 max-w-md mx-auto">
                <AlertDescription>
                  Você não está alocado em nenhuma turma como professor.
                </AlertDescription>
              </Alert>
            )}
          </CardContent>
        </Card>
      )}
    </div>
  )
}