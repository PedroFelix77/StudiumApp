"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Calendar, CheckCircle, XCircle, Clock, AlertCircle } from "lucide-react"
import { api } from "@/services/api"
import { useAuth } from "@/context/AuthContext"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Badge } from "@/components/ui/badge"
import { Progress } from "@/components/ui/progress"
import { Skeleton } from "@/components/ui/skeleton"

interface AttendanceRecord {
  date: string
  disciplineName: string
  className: string
  status: "PRESENT" | "ABSENT" | "JUSTIFIED"
}

interface DisciplineAttendance {
  disciplineId: string
  disciplineName: string
  totalClasses: number
  presentCount: number
  absentCount: number
  justifiedCount: number
  attendanceRate: number
}

export default function AlunoFrequencia() {
  const { user } = useAuth()
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [attendanceRecords, setAttendanceRecords] = useState<AttendanceRecord[]>([])
  const [disciplineStats, setDisciplineStats] = useState<DisciplineAttendance[]>([])
  const [selectedDiscipline, setSelectedDiscipline] = useState<string>("all")
  const [selectedMonth, setSelectedMonth] = useState<string>("all")

  useEffect(() => {
    if (user?.id) {
      loadAttendance()
    }
  }, [user])

  const loadAttendance = async () => {
    try {
      setLoading(true)
      setError("")

      // Carregar frequência do aluno
      const attendanceRes = await api.get(`/api/students/${user?.id}/attendance`)

      if (attendanceRes.data) {
        setAttendanceRecords(attendanceRes.data.records || [])
        setDisciplineStats(attendanceRes.data.stats || [])
      }
    } catch (err: any) {
      console.error("Erro ao carregar frequência:", err)
      setError("Não foi possível carregar sua frequência")
      // Dados mockados para demonstração
      setAttendanceRecords([
        { date: "2024-03-10", disciplineName: "Cálculo 2", className: "CC001", status: "PRESENT" },
        { date: "2024-03-08", disciplineName: "Física 1", className: "FIS001", status: "PRESENT" },
        { date: "2024-03-07", disciplineName: "Programação", className: "PROG001", status: "ABSENT" },
        { date: "2024-03-05", disciplineName: "Banco de Dados", className: "BD001", status: "JUSTIFIED" },
        { date: "2024-03-03", disciplineName: "Cálculo 2", className: "CC001", status: "PRESENT" },
      ])
      setDisciplineStats([
        { disciplineId: "1", disciplineName: "Cálculo 2", totalClasses: 20, presentCount: 18, absentCount: 1, justifiedCount: 1, attendanceRate: 90 },
        { disciplineId: "2", disciplineName: "Física 1", totalClasses: 18, presentCount: 15, absentCount: 2, justifiedCount: 1, attendanceRate: 83.3 },
        { disciplineId: "3", disciplineName: "Programação", totalClasses: 22, presentCount: 20, absentCount: 2, justifiedCount: 0, attendanceRate: 90.9 },
        { disciplineId: "4", disciplineName: "Banco de Dados", totalClasses: 15, presentCount: 12, absentCount: 1, justifiedCount: 2, attendanceRate: 80 },
      ])
    } finally {
      setLoading(false)
    }
  }

  const getStatusIcon = (status: string) => {
    switch (status) {
      case "PRESENT": return <CheckCircle className="h-5 w-5 text-green-600" />
      case "ABSENT": return <XCircle className="h-5 w-5 text-red-600" />
      case "JUSTIFIED": return <Clock className="h-5 w-5 text-blue-600" />
      default: return null
    }
  }

  const getStatusText = (status: string) => {
    switch (status) {
      case "PRESENT": return "Presente"
      case "ABSENT": return "Falta"
      case "JUSTIFIED": return "Justificada"
      default: return "Desconhecido"
    }
  }

  const getStatusColor = (status: string) => {
    switch (status) {
      case "PRESENT": return "bg-green-100 text-green-800 border-green-200"
      case "ABSENT": return "bg-red-100 text-red-800 border-red-200"
      case "JUSTIFIED": return "bg-blue-100 text-blue-800 border-blue-200"
      default: return "bg-gray-100 text-gray-800"
    }
  }

  const getAttendanceColor = (rate: number) => {
    if (rate >= 75) return "text-green-600"
    if (rate >= 50) return "text-yellow-600"
    return "text-red-600"
  }

  const filteredRecords = selectedDiscipline === "all"
    ? attendanceRecords
    : attendanceRecords.filter(record =>
      record.disciplineName === disciplineStats.find(d => d.disciplineId === selectedDiscipline)?.disciplineName
    )

  const filteredStats = selectedDiscipline === "all"
    ? disciplineStats
    : disciplineStats.filter(stat => stat.disciplineId === selectedDiscipline)

  const totalStats = filteredStats.reduce((acc, stat) => ({
    totalClasses: acc.totalClasses + stat.totalClasses,
    presentCount: acc.presentCount + stat.presentCount,
    absentCount: acc.absentCount + stat.absentCount,
    justifiedCount: acc.justifiedCount + stat.justifiedCount,
  }), { totalClasses: 0, presentCount: 0, absentCount: 0, justifiedCount: 0 })

  const overallAttendanceRate = totalStats.totalClasses > 0
    ? ((totalStats.presentCount + totalStats.justifiedCount) / totalStats.totalClasses) * 100
    : 0

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
        <h1 className="text-3xl font-semibold">Minha Frequência</h1>
        <p className="text-muted-foreground">
          Controle de presença nas aulas
        </p>
      </div>

      {error && (
        <Alert variant="destructive">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      {/* Estatísticas Gerais */}
      <Card>
        <CardContent className="pt-6">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <div className="text-center p-4 bg-green-50 rounded-lg">
              <div className="text-2xl font-bold text-green-600">{totalStats.presentCount}</div>
              <div className="text-sm text-green-700">Presentes</div>
            </div>
            <div className="text-center p-4 bg-red-50 rounded-lg">
              <div className="text-2xl font-bold text-red-600">{totalStats.absentCount}</div>
              <div className="text-sm text-red-700">Faltas</div>
            </div>
            <div className="text-center p-4 bg-blue-50 rounded-lg">
              <div className="text-2xl font-bold text-blue-600">{totalStats.justifiedCount}</div>
              <div className="text-sm text-blue-700">Justificadas</div>
            </div>
            <div className="text-center p-4 bg-purple-50 rounded-lg">
              <div className="text-2xl font-bold text-purple-600">{totalStats.totalClasses}</div>
              <div className="text-sm text-purple-700">Total de Aulas</div>
            </div>
          </div>

          <div className="mt-6">
            <div className="flex justify-between items-center mb-2">
              <span className="text-sm font-medium">Frequência Geral</span>
              <span className={`font-bold ${getAttendanceColor(overallAttendanceRate)}`}>
                {overallAttendanceRate.toFixed(1)}%
              </span>
            </div>
            <Progress value={overallAttendanceRate} className="h-2" />
          </div>
        </CardContent>
      </Card>

      {/* Filtros */}
      <Card>
        <CardContent className="pt-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="text-sm font-medium mb-2 block">Disciplina</label>
              <Select value={selectedDiscipline} onValueChange={setSelectedDiscipline}>
                <SelectTrigger>
                  <SelectValue placeholder="Todas as disciplinas" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">Todas as disciplinas</SelectItem>
                  {disciplineStats.map(stat => (
                    <SelectItem key={stat.disciplineId} value={stat.disciplineId}>
                      {stat.disciplineName}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div>
              <label className="text-sm font-medium mb-2 block">Mês</label>
              <Select value={selectedMonth} onValueChange={setSelectedMonth}>
                <SelectTrigger>
                  <SelectValue placeholder="Todos os meses" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">Todos os meses</SelectItem>
                  <SelectItem value="3">Março 2024</SelectItem>
                  <SelectItem value="2">Fevereiro 2024</SelectItem>
                  <SelectItem value="1">Janeiro 2024</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Estatísticas por Disciplina */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {filteredStats.map((stat) => (
          <Card key={stat.disciplineId}>
            <CardHeader>
              <CardTitle className="text-lg">{stat.disciplineName}</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <div>
                  <div className="flex justify-between items-center mb-2">
                    <span className="text-sm text-gray-600">Frequência</span>
                    <span className={`font-bold ${getAttendanceColor(stat.attendanceRate)}`}>
                      {stat.attendanceRate.toFixed(1)}%
                    </span>
                  </div>
                  <Progress value={stat.attendanceRate} className="h-2" />
                </div>

                <div className="grid grid-cols-3 gap-2 text-sm">
                  <div className="text-center">
                    <div className="font-semibold text-green-600">{stat.presentCount}</div>
                    <div className="text-gray-500">Presentes</div>
                  </div>
                  <div className="text-center">
                    <div className="font-semibold text-red-600">{stat.absentCount}</div>
                    <div className="text-gray-500">Faltas</div>
                  </div>
                  <div className="text-center">
                    <div className="font-semibold text-blue-600">{stat.justifiedCount}</div>
                    <div className="text-gray-500">Justificadas</div>
                  </div>
                </div>

                <div className="text-sm text-gray-500 text-center">
                  Total de {stat.totalClasses} aulas
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* Registros de Frequência */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Calendar size={20} />
            Registros de Frequência
          </CardTitle>
        </CardHeader>
        <CardContent>
          {loading ? (
            <div className="space-y-4">
              {[1, 2, 3, 4, 5].map((i) => (
                <Skeleton key={i} className="h-16 w-full" />
              ))}
            </div>
          ) : filteredRecords.length === 0 ? (
            <div className="text-center py-8 text-muted-foreground">
              Nenhum registro de frequência encontrado
            </div>
          ) : (
            <div className="space-y-4">
              {filteredRecords.map((record, index) => (
                <div
                  key={index}
                  className="flex items-center justify-between p-4 border rounded-lg hover:bg-gray-50"
                >
                  <div className="flex items-center gap-4">
                    <div className={`p-2 rounded-full ${getStatusColor(record.status)}`}>
                      {getStatusIcon(record.status)}
                    </div>
                    <div>
                      <div className="font-medium">{record.disciplineName}</div>
                      <div className="text-sm text-gray-500">
                        {record.className} • {new Date(record.date).toLocaleDateString('pt-BR', {
                          weekday: 'long',
                          day: 'numeric',
                          month: 'long',
                          year: 'numeric'
                        })}
                      </div>
                    </div>
                  </div>
                  <Badge className={`${getStatusColor(record.status)}`}>
                    {getStatusText(record.status)}
                  </Badge>
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}