"use client"

import { useEffect, useState } from "react"
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogDescription
} from "@/components/ui/dialog"
import { api } from "@/services/api"
import { Calendar, CheckCircle2, XCircle, AlertCircle } from "lucide-react"

interface Props {
    open: boolean
    onOpenChange: (open: boolean) => void
    registrationId: string
}

type StatusFrequency = "PRESENT" | "ABSENT" | "JUSTIFIED"

interface FrequencyResponseDTO {
    id: string
    attendanceDate: string
    statusFrequency: StatusFrequency
    studentId: string
    studentName: string
    courseId: string
    courseName: string
    disciplineId: string
    disciplineName: string
}

export default function StudentFrequencyModal({
    open,
    onOpenChange,
    registrationId
}: Props) {
    const [frequencies, setFrequencies] = useState<FrequencyResponseDTO[]>([])
    const [loading, setLoading] = useState(false)
    const [studentId, setStudentId] = useState<string | null>(null)

    useEffect(() => {
        if (open && registrationId) {
            loadRegistrationData()
        }
    }, [open, registrationId])

    const loadRegistrationData = async () => {
        try {
            // Primeiro, buscar dados da matrícula para obter o studentId
            const registrationRes = await api.get(`/api/registrations/${registrationId}`)
            const registration = registrationRes.data
            setStudentId(registration.studentId || registration.student?.id)

            if (registration.studentId || registration.student?.id) {
                loadFrequencies(registration.studentId || registration.student?.id)
            }
        } catch (err) {
            console.error("Erro ao carregar dados da matrícula", err)
        }
    }

    const loadFrequencies = async (sid: string) => {
        setLoading(true)
        try {
            const res = await api.get("/api/frequencies/filter", {
                params: {
                    studentId: sid,
                    page: 0,
                    size: 100
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

    const getStatusIcon = (status: StatusFrequency) => {
        switch (status) {
            case "PRESENT":
                return <CheckCircle2 className="h-4 w-4 text-green-600" />
            case "ABSENT":
                return <XCircle className="h-4 w-4 text-red-600" />
            case "JUSTIFIED":
                return <AlertCircle className="h-4 w-4 text-yellow-600" />
            default:
                return null
        }
    }

    const getStatusLabel = (status: StatusFrequency) => {
        switch (status) {
            case "PRESENT":
                return "Presente"
            case "ABSENT":
                return "Ausente"
            case "JUSTIFIED":
                return "Justificado"
            default:
                return status
        }
    }

    const presentCount = frequencies.filter(f => f.statusFrequency === "PRESENT").length
    const absentCount = frequencies.filter(f => f.statusFrequency === "ABSENT").length
    const justifiedCount = frequencies.filter(f => f.statusFrequency === "JUSTIFIED").length
    const total = frequencies.length
    const attendanceRate = total > 0 ? ((presentCount + justifiedCount) / total) * 100 : 0

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="max-w-3xl max-h-[80vh] overflow-y-auto">
                <DialogHeader>
                    <DialogTitle>Frequência do Aluno</DialogTitle>
                    <DialogDescription>
                        Visualize o histórico de frequência deste aluno
                    </DialogDescription>
                </DialogHeader>

                {loading ? (
                    <p className="text-sm text-muted-foreground">Carregando...</p>
                ) : (
                    <>
                        {/* RESUMO */}
                        {total > 0 && (
                            <div className="grid grid-cols-4 gap-4 p-4 bg-muted rounded-lg">
                                <div>
                                    <p className="text-sm text-muted-foreground">Total</p>
                                    <p className="text-2xl font-bold">{total}</p>
                                </div>
                                <div>
                                    <p className="text-sm text-muted-foreground">Presente</p>
                                    <p className="text-2xl font-bold text-green-600">{presentCount}</p>
                                </div>
                                <div>
                                    <p className="text-sm text-muted-foreground">Ausente</p>
                                    <p className="text-2xl font-bold text-red-600">{absentCount}</p>
                                </div>
                                <div>
                                    <p className="text-sm text-muted-foreground">Frequência</p>
                                    <p className="text-2xl font-bold">{attendanceRate.toFixed(1)}%</p>
                                </div>
                            </div>
                        )}

                        {/* LISTA DE FREQUÊNCIAS */}
                        {frequencies.length === 0 ? (
                            <p className="text-sm text-muted-foreground text-center py-8">
                                Nenhuma frequência registrada
                            </p>
                        ) : (
                            <div className="space-y-2">
                                {frequencies.map((freq) => (
                                    <div
                                        key={freq.id}
                                        className="flex items-center justify-between border rounded-md p-3"
                                    >
                                        <div className="flex items-center gap-3">
                                            <Calendar className="h-4 w-4 text-muted-foreground" />
                                            <div>
                                                <p className="font-medium">
                                                    {new Date(freq.attendanceDate).toLocaleDateString("pt-BR")}
                                                </p>
                                                <p className="text-sm text-muted-foreground">
                                                    {freq.disciplineName} - {freq.courseName}
                                                </p>
                                            </div>
                                        </div>
                                        <div className="flex items-center gap-2">
                                            {getStatusIcon(freq.statusFrequency)}
                                            <span className="text-sm font-medium">
                                                {getStatusLabel(freq.statusFrequency)}
                                            </span>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </>
                )}
            </DialogContent>
        </Dialog>
    )
}

