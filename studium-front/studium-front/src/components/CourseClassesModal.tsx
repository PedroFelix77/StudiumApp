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
import { Users } from "lucide-react"
import { api } from "@/services/api"
import { useAuth } from "@/context/AuthContext"
import ClassStudentsModal from "@/components/ClassStudentsModal"

interface Props {
    open: boolean
    onOpenChange: (open: boolean) => void
    courseId: string
}

interface ClassItem {
    id: string
    name: string
    codeClass: string
    academicYear: string
}

export default function CourseClassesModal({
    open,
    onOpenChange,
    courseId
}: Props) {
    const { user } = useAuth()
    const [classes, setClasses] = useState<ClassItem[]>([])
    const [loading, setLoading] = useState(false)
    const [selectedClassId, setSelectedClassId] = useState<string | null>(null)
    const [openStudentsModal, setOpenStudentsModal] = useState(false)

    const [name, setName] = useState("")
    const [codeClass, setCodeClass] = useState("")
    const [academicYear, setAcademicYear] = useState("")

    useEffect(() => {
        if (open) {
            loadClasses()
        }
    }, [open, courseId, user])

    const loadClasses = async () => {
        try {
            let endpoint = `/api/classes/course/${courseId}`

            // Se for TEACHER, filtrar turmas do professor
            if (user?.role === "TEACHER" && user?.id) {
                endpoint = `/api/classes/teacher/${user.id}/course/${courseId}`
            }

            const res = await api.get(endpoint)
            setClasses(res.data)
        } catch (err) {
            console.error("Erro ao carregar turmas", err)
            setClasses([])
        }
    }

    const handleCreate = async () => {
        setLoading(true)
        try {
            await api.post("/api/classes", {
                name,
                codeClass,
                academicYear,
                courseId
            })
            setName("")
            setCodeClass("")
            setAcademicYear("")
            loadClasses()
        } catch (err) {
            console.error("Erro ao criar turma", err)
        } finally {
            setLoading(false)
        }
    }

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="max-w-2xl">
                <DialogHeader>
                    <DialogTitle>Turmas do Curso</DialogTitle>
                    <DialogDescription>
                        Crie e visualize as turmas vinculadas a este curso
                    </DialogDescription>
                </DialogHeader>

                {/* LISTAGEM */}
                <div className="space-y-2">
                    {classes.map(c => (
                        <div
                            key={c.id}
                            className="flex justify-between items-center border rounded-md p-2 text-sm gap-2"
                        >
                            <div className="flex-1">
                                <span className="font-medium">{c.name}</span>
                                <span className="text-muted-foreground ml-2">{c.codeClass}</span>
                                <span className="text-muted-foreground ml-2">{c.academicYear}</span>
                            </div>
                            <Button
                                variant="outline"
                                size="sm"
                                onClick={() => {
                                    setSelectedClassId(c.id)
                                    setOpenStudentsModal(true)
                                }}
                                className="gap-1"
                            >
                                <Users size={14} />
                                Alunos
                            </Button>
                        </div>
                    ))}

                    {classes.length === 0 && (
                        <p className="text-sm text-muted-foreground">
                            Nenhuma turma cadastrada
                        </p>
                    )}
                </div>

                <hr />

                {/* FORM */}
                <div className="space-y-3">
                    <Label>Nova Turma</Label>

                    <Input
                        placeholder="Nome da turma"
                        value={name}
                        onChange={e => setName(e.target.value)}
                    />
                    <Input
                        placeholder="Código"
                        value={codeClass}
                        onChange={e => setCodeClass(e.target.value)}
                    />
                    <Input
                        placeholder="Ano letivo"
                        value={academicYear}
                        onChange={e => setAcademicYear(e.target.value)}
                    />
                </div>

                <DialogFooter>
                    {user?.role !== "TEACHER" && (
                        <Button
                            onClick={handleCreate}
                            disabled={loading || !name || !codeClass || !academicYear}
                        >
                            {loading ? "Criando..." : "Criar Turma"}
                        </Button>
                    )}
                </DialogFooter>
            </DialogContent>

            {selectedClassId && (
                <ClassStudentsModal
                    open={openStudentsModal}
                    onOpenChange={setOpenStudentsModal}
                    classId={selectedClassId}
                />
            )}
        </Dialog>
    )
}
