"use client"

import { useEffect, useState } from "react"
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Label } from "@/components/ui/label"
import { api } from "@/services/api"

interface RegistrationModalProps {
    open: boolean
    onOpenChange: (open: boolean) => void
    courseId: string
    onSuccess?: () => void
}

export default function RegistrationModal({ open, onOpenChange, courseId, onSuccess }: RegistrationModalProps) {
    const [students, setStudents] = useState<{ id: string; name: string }[]>([])
    const [classes, setClasses] = useState<{ id: string; name: string }[]>([])
    const [studentId, setStudentId] = useState("")
    const [classId, setClassId] = useState("")
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState("")

    useEffect(() => {
        if (open && courseId) {
            loadData()
        }
    }, [open, courseId])

    const loadData = async () => {
        setError("")
        try {
            // Carregar alunos usando o mesmo padrão do hook useStudents
            const studentsRes = await api.get("/api/students", { params: { page: 0, size: 100 } })

            // Processar alunos - mesmo padrão do useStudents
            const studentsData = studentsRes.data.content || []
            const mappedStudents = studentsData.map((s: any) => ({
                id: s.id,
                name: s.name || s.user?.name || "Sem nome"
            }))

            setStudents(mappedStudents)

            // Tentar carregar turmas (pode dar 403 se não existir ainda)
            try {
                const classesRes = await api.get(`/api/classes/course/${courseId}`)
                const classesData = Array.isArray(classesRes.data)
                    ? classesRes.data
                    : (classesRes.data?.content || [])

                const mappedClasses = classesData.map((c: any) => ({
                    id: c.id,
                    name: c.name || c.className || "Sem nome"
                }))

                setClasses(mappedClasses)
            } catch (classesErr: any) {
                // Se der erro 403 ou outro erro nas turmas, apenas loga mas não bloqueia
                console.warn("Erro ao carregar turmas (endpoint pode não existir ainda):", classesErr)
                setClasses([])
            }
        } catch (err: any) {
            console.error("Erro ao carregar alunos:", err)
            if (err.response?.status === 403) {
                setError("Acesso negado. Verifique suas permissões.")
            } else {
                setError("Erro ao carregar alunos")
            }
            setStudents([])
        }
    }

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setError("")
        setLoading(true)

        try {
            await api.post("/api/registrations", {
                studentId,
                courseId,
                classId
            })
            onSuccess?.()
            onOpenChange(false)
            // Reset form
            setStudentId("")
            setClassId("")
        } catch (err: any) {
            console.error("Erro ao matricular aluno:", err)
            setError(err.response?.data?.message || "Erro ao matricular aluno. Tente novamente.")
        } finally {
            setLoading(false)
        }
    }

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>Matricular Aluno</DialogTitle>
                    <DialogDescription>
                        Selecione o aluno e a turma para realizar a matrícula.
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit} className="space-y-4">
                    {error && (
                        <div className="p-3 text-sm text-red-600 bg-red-50 rounded-md">
                            {error}
                        </div>
                    )}

                    <div className="space-y-2">
                        <Label htmlFor="student">Aluno *</Label>
                        <select
                            id="student"
                            className="w-full h-9 rounded-md border border-input bg-transparent px-3 py-1 text-sm"
                            value={studentId}
                            onChange={e => setStudentId(e.target.value)}
                            required
                        >
                            <option value="">Selecione o aluno</option>
                            {students.map((s) => (
                                <option key={s.id} value={s.id}>{s.name}</option>
                            ))}
                        </select>
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="class">Turma *</Label>
                        <select
                            id="class"
                            className="w-full h-9 rounded-md border border-input bg-transparent px-3 py-1 text-sm"
                            value={classId}
                            onChange={e => setClassId(e.target.value)}
                            required
                        >
                            <option value="">Selecione a turma</option>
                            {classes.map((c) => (
                                <option key={c.id} value={c.id}>{c.name}</option>
                            ))}
                        </select>
                    </div>

                    <DialogFooter>
                        <Button
                            type="button"
                            variant="outline"
                            onClick={() => onOpenChange(false)}
                            disabled={loading}
                        >
                            Cancelar
                        </Button>
                        <Button type="submit" disabled={!studentId || !classId || loading}>
                            {loading ? "Matriculando..." : "Matricular"}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    )
}
