"use client"

import { useEffect, useState } from "react"
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogDescription,
    DialogFooter
} from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { api } from "@/services/api"

interface Props {
    open: boolean
    onOpenChange: (open: boolean) => void
    courseId: string
    classId: string
}

interface Discipline {
    id: string
    name: string
    code?: string
}

interface Teacher {
    id: string
    user: {
        name: string
    }
}

export default function ClassDisciplineModal({
    open,
    onOpenChange,
    courseId,
    classId
}: Props) {
    const [disciplines, setDisciplines] = useState<Discipline[]>([])
    const [teachers, setTeachers] = useState<Teacher[]>([])
    const [loadingTeachers, setLoadingTeachers] = useState(false)

    const [disciplineName, setDisciplineName] = useState("")
    const [disciplineCode, setDisciplineCode] = useState("")
    const [loadingDiscipline, setLoadingDiscipline] = useState(false)

    const [selectedDisciplineId, setSelectedDisciplineId] = useState("")
    const [selectedTeacherId, setSelectedTeacherId] = useState("")
    const [weeklyWorkLoad, setWeeklyWorkLoad] = useState<number | "">("")
    const [loadingClassroom, setLoadingClassroom] = useState(false)

    useEffect(() => {
        if (open) {
            loadDisciplines()
            loadTeachers()
        }
    }, [open, courseId])

    /* =========================
       LOADERS
    ========================= */

    async function loadDisciplines() {
        try {
            const res = await api.get(`/api/disciplines/course/${courseId}`)
            setDisciplines(res.data)
        } catch (err) {
            console.error("Erro ao carregar disciplinas", err)
            setDisciplines([])
        }
    }

    const loadTeachers = async () => {
        setLoadingTeachers(true)
        try {
            // CORREÇÃO: Endpoint correto é /api/teachers/course/{courseId}
            const res = await api.get(`/api/teachers/course/${courseId}`)
            setTeachers(res.data)
        } catch (err) {
            console.error("Erro ao carregar professores", err)
            setTeachers([])
        } finally {
            setLoadingTeachers(false)
        }
    }

    /* =========================
       ACTIONS
    ========================= */

    async function handleCreateDiscipline() {
        if (!disciplineName) return

        setLoadingDiscipline(true)
        try {
            await api.post("/api/disciplines", {
                name: disciplineName,
                code: disciplineCode,
                courseId
            })

            setDisciplineName("")
            setDisciplineCode("")
            loadDisciplines()
        } catch (err) {
            console.error("Erro ao criar disciplina", err)
        } finally {
            setLoadingDiscipline(false)
        }
    }

    async function handleLinkDiscipline() {
        if (!selectedDisciplineId || !selectedTeacherId || !weeklyWorkLoad) return

        setLoadingClassroom(true)
        try {
            await api.post("/api/classrooms", {
                classId,
                disciplineId: selectedDisciplineId,
                teacherId: selectedTeacherId,
                weeklyWorkLoad
            })

            onOpenChange(false)
        } catch (err) {
            console.error("Erro ao vincular disciplina à turma", err)
        } finally {
            setLoadingClassroom(false)
        }
    }

    /* =========================
       RENDER
    ========================= */

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="max-w-lg">
                <DialogHeader>
                    <DialogTitle>Disciplinas da Turma</DialogTitle>
                    <DialogDescription>
                        Crie disciplinas e vincule à turma para permitir lançamento de frequência
                    </DialogDescription>
                </DialogHeader>

                {/* DISCIPLINAS EXISTENTES */}
                <div className="space-y-2">
                    <Label className="text-sm">Disciplinas do Curso</Label>

                    {disciplines.map(d => (
                        <div
                            key={d.id}
                            className="flex justify-between items-center border rounded-md px-3 py-2 text-sm"
                        >
                            <span>{d.name}</span>
                            {d.code && (
                                <span className="text-muted-foreground">{d.code}</span>
                            )}
                        </div>
                    ))}

                    {disciplines.length === 0 && (
                        <p className="text-sm text-muted-foreground">
                            Nenhuma disciplina cadastrada
                        </p>
                    )}
                </div>

                <hr />

                {/* NOVA DISCIPLINA */}
                <div className="space-y-2">
                    <Label>Nova Disciplina</Label>

                    <Input
                        placeholder="Nome da disciplina"
                        value={disciplineName}
                        onChange={e => setDisciplineName(e.target.value)}
                    />

                    <Input
                        placeholder="Código (opcional)"
                        value={disciplineCode}
                        onChange={e => setDisciplineCode(e.target.value)}
                    />

                    <Button
                        onClick={handleCreateDiscipline}
                        disabled={loadingDiscipline || !disciplineName}
                    >
                        {loadingDiscipline ? "Criando..." : "Criar Disciplina"}
                    </Button>
                </div>

                <hr />

                {/* VINCULAR À TURMA */}
                <div className="space-y-2">
                    <Label>Vincular Disciplina à Turma</Label>

                    <select
                        className="w-full h-9 rounded-md border px-3 text-sm"
                        value={selectedDisciplineId}
                        onChange={e => setSelectedDisciplineId(e.target.value)}
                    >
                        <option value="">Selecione a disciplina</option>
                        {disciplines.map(d => (
                            <option key={d.id} value={d.id}>
                                {d.name}
                            </option>
                        ))}
                    </select>

                    <select
                        className="w-full h-9 rounded-md border px-3 text-sm"
                        value={selectedTeacherId}
                        onChange={e => setSelectedTeacherId(e.target.value)}
                    >
                        <option value="">
                            {loadingTeachers ? "Carregando professores..." : "Selecione o professor"}
                        </option>

                        {Array.isArray(teachers) && teachers.map(t => (
                            <option key={t.id} value={t.id}>
                                {t.user?.name}
                            </option>
                        ))}
                    </select>

                    <Input
                        type="number"
                        placeholder="Carga horária semanal (ex: 4)"
                        value={weeklyWorkLoad}
                        onChange={e => setWeeklyWorkLoad(e.target.value === "" ? "" : Number(e.target.value))}
                    />

                    <Button
                        onClick={handleLinkDiscipline}
                        disabled={
                            loadingClassroom ||
                            !selectedDisciplineId ||
                            !selectedTeacherId ||
                            !weeklyWorkLoad
                        }
                    >
                        {loadingClassroom ? "Vinculando..." : "Vincular Disciplina"}
                    </Button>
                </div>

                <DialogFooter>
                    <Button variant="outline" onClick={() => onOpenChange(false)}>
                        Fechar
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    )
}