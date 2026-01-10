"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Search, Save, Calculator, AlertCircle, Loader2, CheckCircle, RefreshCw } from "lucide-react"
import { api } from "@/services/api"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { useAuth } from "@/context/AuthContext"

interface Class {
  id: string
  name: string
}

interface Discipline {
  id: string
  name: string
  code?: string
}

interface StudentRow {
  registrationId: string
  studentId: string
  studentName: string
  enrollment: string
  p1?: number
  p2?: number
  final?: number
  initialAverage: number
  finalAverage?: number
  status: "APROVADO" | "REPROVADO" | "EM ANDAMENTO"
  needsFinal: boolean
  p1Saved?: boolean
  p2Saved?: boolean
  finalSaved?: boolean
}

export default function NotasProfessorPage() {
  const { user } = useAuth()
  const [classes, setClasses] = useState<Class[]>([])
  const [disciplines, setDisciplines] = useState<Discipline[]>([])
  const [rows, setRows] = useState<StudentRow[]>([])
  const [search, setSearch] = useState("")

  const [selectedClass, setSelectedClass] = useState("")
  const [selectedDiscipline, setSelectedDiscipline] = useState("")
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)
  const [lastSaveTime, setLastSaveTime] = useState<string>("")
  const [error, setError] = useState<string>("")

  // Carrega turmas do professor ao iniciar
  useEffect(() => {
    loadClasses()
  }, [])

  // Carrega disciplinas quando turma é selecionada
  useEffect(() => {
    if (selectedClass) {
      loadDisciplines()
      setSelectedDiscipline("")
      setRows([])
      setError("")
    } else {
      setDisciplines([])
      setRows([])
    }
  }, [selectedClass])

  // Carrega alunos e notas quando disciplina é selecionada
  useEffect(() => {
    if (selectedClass && selectedDiscipline) {
      loadStudentsAndGrades()
    } else {
      setRows([])
    }
  }, [selectedClass, selectedDiscipline])

  async function loadClasses() {
    try {
      if (!user?.id) {
        setError("Usuário não autenticado")
        return
      }

      // Buscar turmas do professor logado
      const res = await api.get(`/api/teacher-classes/my/classes`)
      const classesData = res.data.content || res.data || []
      setClasses(Array.isArray(classesData) ? classesData : [])

      if (classesData.length === 0) {
        setError("Você não está alocado em nenhuma turma")
      }
    } catch (err) {
      console.error("Erro ao carregar turmas:", err)
      setClasses([])
      setError("Não foi possível carregar suas turmas")
    }
  }

  async function loadDisciplines() {
    if (!user?.id || !selectedClass) return

    try {
      // Buscar disciplinas do professor na turma selecionada
      const res = await api.get(`/api/teacher-classes/my/disciplines`, {
        params: { classId: selectedClass }
      })

      setDisciplines(res.data || [])

      if (res.data.length === 0) {
        setError("Você não leciona disciplinas nesta turma")
      }
    } catch (err) {
      console.error("Erro ao carregar disciplinas:", err)
      setDisciplines([])
      setError("Não foi possível carregar as disciplinas")
    }
  }

  async function loadStudentsAndGrades() {
    if (!selectedClass || !selectedDiscipline) {
      setRows([])
      return
    }

    setLoading(true)
    setError("")

    try {
      // 1️⃣ Buscar alunos da turma
      const studentsRes = await api.get(
        `/api/registrations/class/${selectedClass}/students`
      )

      const students = Array.isArray(studentsRes.data)
        ? studentsRes.data
        : (studentsRes.data?.content || studentsRes.data || [])

      if (students.length === 0) {
        setRows([])
        setError("Nenhum aluno encontrado nesta turma")
        setLoading(false)
        return
      }

      // 2️⃣ Buscar notas existentes - usando endpoint otimizado se disponível
      let grades: any[] = []
      try {
        // Primeiro tenta endpoint otimizado
        const gradesRes = await api.get("/api/grades/by-class-discipline-with-students", {
          params: {
            classId: selectedClass,
            disciplineId: selectedDiscipline
          }
        })
        grades = gradesRes.data || []
        console.log("Notas carregadas com alunos:", grades)
      } catch (err) {
        console.warn("Endpoint otimizado não disponível, tentando endpoint padrão:", err)

        try {
          // Fallback para endpoint padrão
          const gradesRes = await api.get("/api/grades/by-class-discipline", {
            params: {
              classId: selectedClass,
              disciplineId: selectedDiscipline
            }
          })
          grades = gradesRes.data || []
        } catch (fallbackErr) {
          console.warn("Não foi possível carregar notas existentes:", fallbackErr)
          // Continua sem notas, apenas com alunos
        }
      }

      // 3️⃣ Processar cada aluno - usando registrationId para fazer match
      const processedRows: StudentRow[] = students.map((student: any) => {
        // Buscar notas deste aluno usando registrationId
        const studentGrades = grades.filter((g: any) => {
          // Se o endpoint otimizado retornar studentId ou registrationNumber, usamos isso também
          if (g.registrationId && student.registrationId) {
            return g.registrationId === student.registrationId
          }
          // Fallback: usar studentId se disponível
          if (g.studentId && student.studentId) {
            return g.studentId === student.studentId
          }
          // Fallback: usar registrationNumber
          if (g.registrationNumber && student.registrationNumber) {
            return g.registrationNumber === student.registrationNumber
          }
          return false
        })

        const p1Grade = studentGrades.find((g: any) =>
          g.typeGrade === "PROVA1"
        )
        const p2Grade = studentGrades.find((g: any) =>
          g.typeGrade === "PROVA2"
        )
        const finalGrade = studentGrades.find((g: any) =>
          g.typeGrade === "FINAL"
        )

        const p1 = p1Grade?.grade !== undefined ? Number(p1Grade.grade) : undefined
        const p2 = p2Grade?.grade !== undefined ? Number(p2Grade.grade) : undefined
        const final = finalGrade?.grade !== undefined ? Number(finalGrade.grade) : undefined

        // Calcular média inicial (P1 + P2) / 2
        let initialAverage = 0
        if (p1 !== undefined && p2 !== undefined) {
          initialAverage = (p1 + p2) / 2
        } else if (p1 !== undefined) {
          initialAverage = p1
        } else if (p2 !== undefined) {
          initialAverage = p2
        }

        // Verificar se precisa de final (média < 7)
        const needsFinal = initialAverage < 7

        // Calcular média final se tiver nota final
        let finalAverage = undefined
        let status: "APROVADO" | "REPROVADO" | "EM ANDAMENTO" = "EM ANDAMENTO"

        if (final !== undefined) {
          finalAverage = (initialAverage + final) / 2
          status = finalAverage >= 5 ? "APROVADO" : "REPROVADO"
        } else if (p1 !== undefined || p2 !== undefined) {
          // Só define status se pelo menos uma nota foi lançada
          status = initialAverage >= 7 ? "APROVADO" : "REPROVADO"
        } else {
          status = "EM ANDAMENTO"
        }

        return {
          registrationId: student.registrationId,
          studentId: student.studentId,
          studentName: student.studentName,
          enrollment: student.registrationNumber || student.enrollment || "",
          p1,
          p2,
          final,
          initialAverage: Number(initialAverage.toFixed(1)),
          finalAverage: finalAverage !== undefined ? Number(finalAverage.toFixed(1)) : undefined,
          status,
          needsFinal,
          p1Saved: !!p1Grade?.id,
          p2Saved: !!p2Grade?.id,
          finalSaved: !!finalGrade?.id
        }
      })

      setRows(processedRows)

      // Se não conseguiu carregar notas, mas tem alunos, mostra mensagem
      if (processedRows.length > 0 && processedRows.every(r => r.p1 === undefined && r.p2 === undefined)) {
        setError("Notas não foram encontradas para esta turma/disciplina. Você pode lançá-las agora.")
      }
    } catch (err: any) {
      console.error("Erro ao carregar alunos:", err)
      setRows([])
      setError(err.response?.data?.message || "Erro ao carregar alunos e notas")
    } finally {
      setLoading(false)
    }
  }

  const updateValue = (
    id: string,
    field: "p1" | "p2" | "final",
    value: string
  ) => {
    const num = parseFloat(value)
    if (isNaN(num) || num < 0 || num > 10) return

    setRows((prev) =>
      prev.map((r) => {
        if (r.registrationId === id) {
          const updatedRow = { ...r, [field]: num }

          // Marca como não salvo quando altera o valor
          if (field === "p1") updatedRow.p1Saved = false
          if (field === "p2") updatedRow.p2Saved = false
          if (field === "final") updatedRow.finalSaved = false

          // Recalcular média inicial
          let initialAverage = 0
          const p1 = updatedRow.p1 !== undefined ? updatedRow.p1 : 0
          const p2 = updatedRow.p2 !== undefined ? updatedRow.p2 : 0

          if (p1 > 0 && p2 > 0) {
            initialAverage = (p1 + p2) / 2
          } else if (p1 > 0) {
            initialAverage = p1
          } else if (p2 > 0) {
            initialAverage = p2
          }

          const needsFinal = initialAverage < 7

          // Recalcular média final se tiver nota final
          let finalAverage = undefined
          let status: "APROVADO" | "REPROVADO" | "EM ANDAMENTO" = "EM ANDAMENTO"

          if (updatedRow.final !== undefined) {
            finalAverage = (initialAverage + updatedRow.final) / 2
            status = finalAverage >= 5 ? "APROVADO" : "REPROVADO"
          } else {
            status = initialAverage >= 7 ? "APROVADO" : "REPROVADO"
          }

          return {
            ...updatedRow,
            initialAverage: Number(initialAverage.toFixed(1)),
            finalAverage: finalAverage !== undefined ? Number(finalAverage.toFixed(1)) : undefined,
            status,
            needsFinal
          }
        }
        return r
      })
    )
  }

  const saveGrades = async () => {
    if (!user?.id || !selectedClass || !selectedDiscipline) {
      setError("É necessário estar autenticado e selecionar turma e disciplina.")
      return
    }

    setSaving(true)
    setError("")

    try {
      let successCount = 0
      let errorMessages: string[] = []

      for (const row of rows) {
        const studentName = row.studentName

        // Salvar P1 se tiver valor e não estiver salvo
        if (row.p1 !== undefined && row.p1 !== null && !row.p1Saved) {
          try {
            await api.post("/api/grades", {
              registrationId: row.registrationId,
              disciplineId: selectedDiscipline,
              classId: selectedClass,
              teacherId: user.id,
              typeGrade: "PROVA1",
              grade: row.p1
            })

            // Atualiza estado local para marcar como salvo
            setRows(prev => prev.map(r =>
              r.registrationId === row.registrationId
                ? { ...r, p1Saved: true }
                : r
            ))

            successCount++
          } catch (err: any) {
            errorMessages.push(`P1 de ${studentName}: ${err.response?.data?.message || "Erro desconhecido"}`)
          }
        }

        // Salvar P2 se tiver valor e não estiver salvo
        if (row.p2 !== undefined && row.p2 !== null && !row.p2Saved) {
          try {
            await api.post("/api/grades", {
              registrationId: row.registrationId,
              disciplineId: selectedDiscipline,
              classId: selectedClass,
              teacherId: user.id,
              typeGrade: "PROVA2",
              grade: row.p2
            })

            // Atualiza estado local para marcar como salvo
            setRows(prev => prev.map(r =>
              r.registrationId === row.registrationId
                ? { ...r, p2Saved: true }
                : r
            ))

            successCount++
          } catch (err: any) {
            errorMessages.push(`P2 de ${studentName}: ${err.response?.data?.message || "Erro desconhecido"}`)
          }
        }

        // Salvar final se necessário, tiver valor e não estiver salvo
        if (row.needsFinal && row.final !== undefined && row.final !== null && !row.finalSaved) {
          try {
            await api.post("/api/grades", {
              registrationId: row.registrationId,
              disciplineId: selectedDiscipline,
              classId: selectedClass,
              teacherId: user.id,
              typeGrade: "FINAL",
              grade: row.final
            })

            // Atualiza estado local para marcar como salvo
            setRows(prev => prev.map(r =>
              r.registrationId === row.registrationId
                ? { ...r, finalSaved: true }
                : r
            ))

            successCount++
          } catch (err: any) {
            errorMessages.push(`Final de ${studentName}: ${err.response?.data?.message || "Erro desconhecido"}`)
          }
        }
      }

      // Atualizar horário do último salvamento
      setLastSaveTime(new Date().toLocaleTimeString())

      // Mostrar resultado
      if (errorMessages.length > 0) {
        setError(`Salvo com alguns erros: ${errorMessages.join("; ")}`)
      } else if (successCount > 0) {
        setError("") // Limpa erro se salvou com sucesso
      } else {
        setError("Nenhuma nota nova para salvar.")
      }

    } catch (err: any) {
      console.error("Erro ao salvar notas:", err)
      setError(err.response?.data?.message || "Erro ao salvar notas")
    } finally {
      setSaving(false)
    }
  }

  const handleRefresh = async () => {
    if (selectedClass && selectedDiscipline) {
      await loadStudentsAndGrades()
    }
  }

  const filtered = rows.filter(
    (r) =>
      r.studentName.toLowerCase().includes(search.toLowerCase()) ||
      r.enrollment.toLowerCase().includes(search.toLowerCase())
  )

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

  const getSavedIndicator = (saved: boolean | undefined) => {
    return saved ? (
      <span className="absolute -right-2 -top-2">
        <CheckCircle className="h-4 w-4 text-green-600" />
      </span>
    ) : null
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
        <h1 className="text-3xl font-semibold">Gestão de Notas</h1>
        <p className="text-muted-foreground">Lançamento e consulta de notas por disciplina</p>
        <p className="text-sm text-gray-500 mt-1">
          Professor: <span className="font-medium">{user.name}</span>
        </p>
      </div>

      {/* Card Principal */}
      <Card>
        <CardHeader className="flex flex-row justify-between items-center">
          <CardTitle className="flex items-center gap-2">
            <Calculator size={24} />
            Lançamento de Notas
          </CardTitle>
          <div className="flex items-center gap-4">
            {lastSaveTime && (
              <div className="text-sm text-green-600 flex items-center gap-1">
                <CheckCircle size={16} />
                <span>Salvo às {lastSaveTime}</span>
              </div>
            )}
            <div className="flex gap-2">
              <Button
                onClick={handleRefresh}
                variant="outline"
                size="sm"
                disabled={loading || saving || !selectedClass || !selectedDiscipline}
                className="gap-2"
              >
                <RefreshCw size={16} />
                Atualizar
              </Button>
              <Button
                onClick={saveGrades}
                disabled={loading || saving || !selectedClass || !selectedDiscipline}
                className="gap-2"
              >
                {saving ? (
                  <>
                    <Loader2 className="h-4 w-4 animate-spin" />
                    Salvando...
                  </>
                ) : (
                  <>
                    <Save size={18} />
                    Salvar Notas
                  </>
                )}
              </Button>
            </div>
          </div>
        </CardHeader>

        <CardContent className="space-y-4">
          {/* Mensagem de erro/sucesso */}
          {error && (
            <Alert variant={error.includes("Salvo com alguns erros") || error.includes("Notas não foram encontradas") ? "default" : "destructive"}>
              <AlertCircle className="h-4 w-4" />
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}

          {/* Filtros */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* Turma */}
            <div className="space-y-2">
              <Label htmlFor="class">Turma</Label>
              <select
                id="class"
                className="w-full h-9 border rounded-md px-3"
                value={selectedClass}
                onChange={(e) => setSelectedClass(e.target.value)}
                disabled={loading || saving}
              >
                <option value="">Selecione a turma</option>
                {classes.map((t) => (
                  <option key={t.id} value={t.id}>
                    {t.name}
                  </option>
                ))}
              </select>
            </div>

            {/* Disciplina - Só aparece depois de selecionar turma */}
            {selectedClass && (
              <div className="space-y-2">
                <Label htmlFor="discipline">Disciplina</Label>
                <select
                  id="discipline"
                  className="w-full h-9 border rounded-md px-3"
                  value={selectedDiscipline}
                  onChange={(e) => setSelectedDiscipline(e.target.value)}
                  disabled={loading || saving || !selectedClass}
                >
                  <option value="">Selecione a disciplina</option>
                  {disciplines.map((d) => (
                    <option key={d.id} value={d.id}>
                      {d.name} {d.code ? `(${d.code})` : ''}
                    </option>
                  ))}
                </select>
              </div>
            )}
          </div>

          {/* Busca */}
          {rows.length > 0 && (
            <div className="relative">
              <Search className="absolute left-3 top-2.5 h-5 w-5 text-gray-400" />
              <Input
                placeholder="Buscar aluno por nome ou matrícula..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="pl-10"
                disabled={loading}
              />
            </div>
          )}

          {/* Tabela */}
          {loading ? (
            <div className="flex justify-center items-center py-12">
              <Loader2 className="h-8 w-8 animate-spin text-gray-400" />
              <span className="ml-2 text-gray-500">Carregando dados...</span>
            </div>
          ) : selectedClass && selectedDiscipline ? (
            rows.length > 0 ? (
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="border-b bg-gray-50">
                      <th className="p-3 text-left font-medium">Aluno</th>
                      <th className="p-3 text-left font-medium">Matrícula</th>
                      <th className="p-3 text-center font-medium">P1</th>
                      <th className="p-3 text-center font-medium">P2</th>
                      <th className="p-3 text-center font-medium">Média</th>
                      <th className="p-3 text-center font-medium">Final</th>
                      <th className="p-3 text-center font-medium">Média Final</th>
                      <th className="p-3 text-center font-medium">Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filtered.map((row) => (
                      <tr key={row.registrationId} className="border-b hover:bg-gray-50 transition-colors">
                        <td className="p-3 font-medium">{row.studentName}</td>
                        <td className="p-3 text-gray-600">{row.enrollment}</td>

                        {/* P1 */}
                        <td className="p-3">
                          <div className="relative">
                            <Input
                              type="number"
                              min="0"
                              max="10"
                              step="0.1"
                              value={row.p1 ?? ""}
                              onChange={(e) =>
                                updateValue(row.registrationId, "p1", e.target.value)
                              }
                              className={`w-16 text-center ${row.p1Saved ? 'bg-green-50 border-green-200' : ''}`}
                              disabled={saving}
                              placeholder="0-10"
                            />
                            {getSavedIndicator(row.p1Saved)}
                          </div>
                        </td>

                        {/* P2 */}
                        <td className="p-3">
                          <div className="relative">
                            <Input
                              type="number"
                              min="0"
                              max="10"
                              step="0.1"
                              value={row.p2 ?? ""}
                              onChange={(e) =>
                                updateValue(row.registrationId, "p2", e.target.value)
                              }
                              className={`w-16 text-center ${row.p2Saved ? 'bg-green-50 border-green-200' : ''}`}
                              disabled={saving}
                              placeholder="0-10"
                            />
                            {getSavedIndicator(row.p2Saved)}
                          </div>
                        </td>

                        {/* Média Inicial */}
                        <td className="p-3 text-center">
                          <span className={`font-bold px-2 py-1 rounded ${row.initialAverage >= 7
                            ? "text-green-700 bg-green-50"
                            : row.initialAverage >= 5
                              ? "text-yellow-700 bg-yellow-50"
                              : "text-red-700 bg-red-50"
                            }`}>
                            {row.initialAverage.toFixed(1)}
                          </span>
                        </td>

                        {/* Final */}
                        <td className="p-3">
                          <div className="relative">
                            {row.needsFinal ? (
                              <>
                                <Input
                                  type="number"
                                  min="0"
                                  max="10"
                                  step="0.1"
                                  value={row.final ?? ""}
                                  onChange={(e) =>
                                    updateValue(row.registrationId, "final", e.target.value)
                                  }
                                  className={`w-16 text-center ${row.finalSaved ? 'bg-green-50 border-green-200' : ''}`}
                                  disabled={saving}
                                  placeholder="0-10"
                                />
                                {getSavedIndicator(row.finalSaved)}
                              </>
                            ) : (
                              <span className="text-gray-400 italic">—</span>
                            )}
                          </div>
                        </td>

                        {/* Média Final */}
                        <td className="p-3 text-center">
                          {row.finalAverage !== undefined ? (
                            <span className={`font-bold px-2 py-1 rounded ${row.finalAverage >= 5
                              ? "text-green-700 bg-green-50"
                              : "text-red-700 bg-red-50"
                              }`}>
                              {row.finalAverage.toFixed(1)}
                            </span>
                          ) : (
                            <span className="text-gray-400">—</span>
                          )}
                        </td>

                        {/* Status */}
                        <td className="p-3 text-center">
                          <span className={`inline-block px-3 py-1 rounded-full text-xs font-semibold border ${getStatusColor(row.status)}`}>
                            {getStatusIcon(row.status)} {row.status}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>

                {/* Resumo */}
                <div className="mt-4 p-3 bg-blue-50 rounded-lg border border-blue-100">
                  <div className="flex items-center justify-between">
                    <div>
                      <h4 className="font-medium text-blue-800 flex items-center gap-2">
                        <AlertCircle size={16} />
                        Resumo da Turma
                      </h4>
                      <p className="text-sm text-blue-700">
                        {rows.length} aluno(s) • {rows.filter(r => r.status === "APROVADO").length} aprovado(s) • {rows.filter(r => r.status === "REPROVADO").length} reprovado(s) • {rows.filter(r => r.status === "EM ANDAMENTO").length} em andamento
                      </p>
                    </div>
                    <div className="text-sm text-gray-600">
                      Notas salvas aparecem com ícone verde ✓
                    </div>
                  </div>
                </div>

                {/* Legenda */}
                <div className="mt-6 p-4 bg-gray-50 rounded-lg border">
                  <h4 className="font-medium text-gray-800 mb-2">Legenda e Regras</h4>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-3 text-sm">
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <div className="w-3 h-3 rounded-full bg-green-600"></div>
                        <span className="text-gray-700">
                          <strong>Aprovado direto:</strong> Média ≥ 7.0
                        </span>
                      </div>
                      <div className="flex items-center gap-2">
                        <div className="w-3 h-3 rounded-full bg-red-600"></div>
                        <span className="text-gray-700">
                          <strong>Reprovado:</strong> Média &lt; 7.0 sem final ou Final &lt; 5.0
                        </span>
                      </div>
                      <div className="flex items-center gap-2">
                        <div className="w-3 h-3 rounded-full bg-yellow-600"></div>
                        <span className="text-gray-700">
                          <strong>Em andamento:</strong> Sem notas lançadas
                        </span>
                      </div>
                    </div>
                    <div className="space-y-1">
                      <div className="text-gray-700">
                        <strong>Cálculo da Média:</strong> (P1 + P2) ÷ 2
                      </div>
                      <div className="text-gray-700">
                        <strong>Cálculo da Final:</strong> (Média + Final) ÷ 2
                      </div>
                      <div className="text-gray-700 text-xs mt-2">
                        <strong>Obs:</strong> Notas devem estar entre 0 e 10
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            ) : (
              <div className="text-center py-12 border rounded-lg bg-gray-50">
                <Calculator className="h-12 w-12 text-gray-400 mx-auto mb-3" />
                <p className="text-gray-600 mb-2">Nenhum aluno encontrado nesta turma.</p>
                <p className="text-sm text-gray-500">
                  Verifique se há alunos matriculados na turma selecionada.
                </p>
              </div>
            )
          ) : (
            <div className="text-center py-12 border rounded-lg bg-gray-50">
              <Calculator className="h-12 w-12 text-gray-400 mx-auto mb-3" />
              <p className="text-gray-600 mb-2">Selecione uma turma e disciplina</p>
              <p className="text-sm text-gray-500">
                1. Escolha uma turma onde você leciona<br />
                2. Selecione uma disciplina que você ensina nesta turma<br />
                para visualizar e lançar as notas
              </p>
              {classes.length === 0 && (
                <Alert className="mt-4 max-w-md mx-auto">
                  <AlertDescription>
                    Você não está alocado em nenhuma turma como professor.
                  </AlertDescription>
                </Alert>
              )}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}