"use client"

import { useEffect, useState } from "react"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
    DialogFooter
} from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Label } from "@/components/ui/label"
import { api } from "@/services/api"

interface Props {
    open: boolean
    onOpenChange: (open: boolean) => void
    courseId: string
    classId: string
}

export default function TeacherAllocationModal({
    open,
    onOpenChange,
    courseId,
    classId
}: Props) {
    const [teachers, setTeachers] = useState<any[]>([])
    const [disciplines, setDisciplines] = useState<any[]>([])
    const [allocations, setAllocations] = useState<any[]>([])

    const [teacherId, setTeacherId] = useState("")
    const [disciplineId, setDisciplineId] = useState("")
    const [weeklyHours, setWeeklyHours] = useState(2)

    useEffect(() => {
        if (open) {
            loadData()
        }
    }, [open])

    async function loadData() {
        const [tRes, dRes, aRes] = await Promise.all([
            api.get("/api/teachers"),
            api.get(`/api/disciplines/course/${courseId}`),
            api.get(`/api/teacher-classes/class/${classId}`)
        ])

        setTeachers(tRes.data.content ?? [])
        setDisciplines(dRes.data)
        setAllocations(aRes.data)
    }

    async function handleAllocate() {
        await api.post("/api/teacher-classes", {
            teacherId,
            disciplineId,
            classId,
            courseId,
            weeklyHours,
            isMainTeacher: true
        })

        setTeacherId("")
        setDisciplineId("")
        loadData()
    }

    async function handleRemove(id: string) {
        await api.delete(`/api/teacher-classes/${id}`)
        loadData()
    }

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="max-w-xl">
                <DialogHeader>
                    <DialogTitle>Alocação de Professor</DialogTitle>
                    <DialogDescription>
                        Vincule um professor a uma turma e disciplina
                    </DialogDescription>
                </DialogHeader>

                {/* LISTAGEM */}
                <div className="space-y-2">
                    {allocations.map(a => (
                        <div
                            key={a.id}
                            className="flex justify-between items-center border rounded-md p-2 text-sm"
                        >
                            <div>
                                <strong>{a.teacherName}</strong> — {a.disciplineName}
                            </div>
                            <Button
                                size="sm"
                                variant="destructive"
                                onClick={() => handleRemove(a.id)}
                            >
                                Remover
                            </Button>
                        </div>
                    ))}

                    {allocations.length === 0 && (
                        <p className="text-sm text-muted-foreground">
                            Nenhum professor alocado
                        </p>
                    )}
                </div>

                <hr />

                {/* FORM */}
                <div className="space-y-3">
                    <Label>Professor</Label>
                    <select
                        className="w-full h-9 border rounded-md"
                        value={teacherId}
                        onChange={e => setTeacherId(e.target.value)}
                    >
                        <option value="">Selecione</option>
                        console.log("teachers:", teachers)

                        {Array.isArray(teachers) && teachers.map(t => (
                            <option key={t.id} value={t.id}>
                                {t.user?.name}
                            </option>
                        ))}
                    </select>

                    <Label>Disciplina</Label>
                    <select
                        className="w-full h-9 border rounded-md"
                        value={disciplineId}
                        onChange={e => setDisciplineId(e.target.value)}
                    >
                        <option value="">Selecione</option>
                        {disciplines.map(d => (
                            <option key={d.id} value={d.id}>
                                {d.name}
                            </option>
                        ))}
                    </select>

                    <Label>Carga horária semanal</Label>
                    <input
                        type="number"
                        min={1}
                        className="w-full h-9 border rounded-md px-2"
                        value={weeklyHours}
                        onChange={e => setWeeklyHours(Number(e.target.value))}
                    />
                </div>

                <DialogFooter>
                    <Button
                        onClick={handleAllocate}
                        disabled={!teacherId || !disciplineId}
                    >
                        Alocar Professor
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    )
}
