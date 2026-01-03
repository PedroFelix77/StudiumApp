"use client"

import { useState } from "react"
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
import { api } from "@/services/api"

interface CreateDepartmentDialogProps {
    open: boolean
    onOpenChange: (open: boolean) => void
    onSuccess?: () => void
}

export function CreateDepartmentDialog({
    open,
    onOpenChange,
    onSuccess,
}: CreateDepartmentDialogProps) {
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState("")

    // Form fields
    const [name, setName] = useState("")
    const [code, setCode] = useState("")

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setError("")
        setLoading(true)

        try {
            const payload = {
                name,
                code: code || undefined,
            }

            await api.post("/api/departments", payload)

            // Reset form
            setName("")
            setCode("")

            onOpenChange(false)
            onSuccess?.()
        } catch (err: any) {
            console.error("Erro ao criar departamento:", err)
            setError(
                err.response?.data?.message || "Erro ao criar departamento. Tente novamente."
            )
        } finally {
            setLoading(false)
        }
    }

    const handleClose = () => {
        if (!loading) {
            onOpenChange(false)
            setError("")
            setName("")
            setCode("")
        }
    }

    return (
        <Dialog open={open} onOpenChange={handleClose}>
            <DialogContent className="max-w-md">
                <DialogHeader>
                    <DialogTitle>Criar Novo Departamento</DialogTitle>
                    <DialogDescription>
                        Preencha os dados abaixo para criar um novo departamento no sistema.
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit} className="space-y-4">
                    {error && (
                        <div className="p-3 text-sm text-red-600 bg-red-50 rounded-md">
                            {error}
                        </div>
                    )}

                    <div className="space-y-2">
                        <Label htmlFor="name">Nome do Departamento *</Label>
                        <Input
                            id="name"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            required
                            placeholder="Ex: Tecnologia da Informação"
                        />
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="code">Código</Label>
                        <Input
                            id="code"
                            value={code}
                            onChange={(e) => setCode(e.target.value)}
                            placeholder="Ex: TI"
                        />
                    </div>

                    <DialogFooter>
                        <Button
                            type="button"
                            variant="outline"
                            onClick={handleClose}
                            disabled={loading}
                        >
                            Cancelar
                        </Button>
                        <Button type="submit" disabled={loading}>
                            {loading ? "Criando..." : "Criar Departamento"}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    )
}

