"use client"

import { useState, useEffect } from "react"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { courseService } from "@/services/api/courseService"
import type { CourseRequestDTO } from "@/services/api/courseService"
import { useAuth } from "@/context/AuthContext"

interface CourseModalProps {
    open: boolean
    onOpenChange: (open: boolean) => void
    onSuccess?: () => void
    courseId?: string
}

export default function CourseModal({
    open,
    onOpenChange,
    onSuccess,
    courseId,
}: CourseModalProps) {
    const { user } = useAuth()
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState("")
    const [name, setName] = useState("")
    const [code_course, setCodeCourse] = useState("")

    useEffect(() => {
        if (open) {
            setName("")
            setCodeCourse("")
            setError("")
        }
    }, [open])

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setError("")
        setLoading(true)

        try {
            if (!user?.institution?.id) {
                setError("Instituição não encontrada")
                return
            }

            const data: CourseRequestDTO = {
                name,
                code_course,
                institutionId: user.institution.id,
            }

            if (courseId) {
                // TODO: Implementar update quando disponível
                await courseService.create(data)
            } else {
                await courseService.create(data)
            }

            onOpenChange(false)
            onSuccess?.()
        } catch (err: any) {
            console.error("Erro ao salvar curso:", err)
            setError(
                err.response?.data?.message || "Erro ao salvar curso. Tente novamente."
            )
        } finally {
            setLoading(false)
        }
    }

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>
                        {courseId ? "Editar Curso" : "Adicionar Novo Curso"}
                    </DialogTitle>
                    <DialogDescription>
                        Preencha os dados abaixo para {courseId ? "editar" : "criar"} o curso.
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit} className="space-y-4">
                    {error && (
                        <div className="p-3 text-sm text-red-600 bg-red-50 rounded-md">
                            {error}
                        </div>
                    )}

                    <div className="space-y-2">
                        <Label htmlFor="name">Nome do Curso *</Label>
                        <Input
                            id="name"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            required
                            placeholder="Ex: Engenharia de Software"
                        />
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="code_course">Código do Curso *</Label>
                        <Input
                            id="code_course"
                            value={code_course}
                            onChange={(e) => setCodeCourse(e.target.value)}
                            required
                            placeholder="Ex: ES001"
                        />
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
                        <Button type="submit" disabled={loading}>
                            {loading ? "Salvando..." : courseId ? "Atualizar" : "Criar"}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    )
}

