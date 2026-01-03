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
import { api } from "@/services/api"

interface CreateTeacherDialogProps {
    open: boolean
    onOpenChange: (open: boolean) => void
    onSuccess?: () => void
}

interface Department {
    id: string
    name: string
    code?: string
}

interface Course {
    id: string
    name: string
    code_course?: string
}

export function CreateTeacherDialog({
    open,
    onOpenChange,
    onSuccess,
}: CreateTeacherDialogProps) {
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState("")
    const [departments, setDepartments] = useState<Department[]>([])
    const [courses, setCourses] = useState<Course[]>([])

    // Form fields
    const [fullName, setFullName] = useState("")
    const [cpf, setCpf] = useState("")
    const [email, setEmail] = useState("")
    const [birthDate, setBirthDate] = useState("")
    const [phone, setPhone] = useState("")
    const [departmentId, setDepartmentId] = useState("")
    const [courseIds, setCourseIds] = useState<string[]>([])
    const [hireDate, setHireDate] = useState("")
    const [specialty, setSpecialty] = useState("")

    // Address fields
    const [cep, setCep] = useState("")
    const [street, setStreet] = useState("")
    const [number, setNumber] = useState("")
    const [city, setCity] = useState("")
    const [state, setState] = useState("")
    const [complement, setComplement] = useState("")

    useEffect(() => {
        if (open) {
            loadDepartments()
            loadCourses()
        }
    }, [open])

    const loadDepartments = async () => {
        try {
            const res = await api.get("/api/departments")
            const data = Array.isArray(res.data) ? res.data : []
            setDepartments(data)
        } catch (err) {
            console.error("Erro ao carregar departamentos", err)
        }
    }

    const loadCourses = async () => {
        try {
            const res = await api.get("/api/courses", { params: { page: 0, size: 100 } })
            const coursesData = res.data?.content ?? res.data ?? []
            setCourses(Array.isArray(coursesData) ? coursesData : [])
        } catch (err) {
            console.error("Erro ao carregar cursos", err)
        }
    }

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setError("")
        setLoading(true)

        try {
            const payload = {
                user: {
                    name: fullName,
                    email,
                    cpf,
                    phone,
                    birthday: birthDate,
                },
                address: {
                    cep,
                    street,
                    number,
                    city,
                    state,
                    complement: complement || "",
                },
                departmentId: departmentId,
                courseIds: courseIds.length > 0 ? courseIds : undefined,
                hireDate: hireDate || undefined,
                specialty: specialty || undefined,
            }

            await api.post("/api/director/teachers", payload)

            // Reset form
            setFullName("")
            setCpf("")
            setEmail("")
            setBirthDate("")
            setPhone("")
            setDepartmentId("")
            setCourseIds([])
            setHireDate("")
            setSpecialty("")
            setCep("")
            setStreet("")
            setNumber("")
            setCity("")
            setState("")
            setComplement("")

            onOpenChange(false)
            onSuccess?.()
        } catch (err: any) {
            console.error("Erro ao criar professor:", err)
            setError(
                err.response?.data?.message || "Erro ao criar professor. Tente novamente."
            )
        } finally {
            setLoading(false)
        }
    }

    const handleClose = () => {
        if (!loading) {
            onOpenChange(false)
            setError("")
            // Reset form
            setFullName("")
            setCpf("")
            setEmail("")
            setBirthDate("")
            setPhone("")
            setDepartmentId("")
            setCourseIds([])
            setHireDate("")
            setSpecialty("")
            setCep("")
            setStreet("")
            setNumber("")
            setCity("")
            setState("")
            setComplement("")
        }
    }

    const handleCourseToggle = (courseId: string) => {
        setCourseIds(prev =>
            prev.includes(courseId)
                ? prev.filter(id => id !== courseId)
                : [...prev, courseId]
        )
    }

    return (
        <Dialog open={open} onOpenChange={handleClose}>
            <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
                <DialogHeader>
                    <DialogTitle>Criar Novo Professor</DialogTitle>
                    <DialogDescription>
                        Preencha os dados abaixo para criar um novo professor no sistema.
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit} className="space-y-4">
                    {error && (
                        <div className="p-3 text-sm text-red-600 bg-red-50 rounded-md">
                            {error}
                        </div>
                    )}

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div className="space-y-2">
                            <Label htmlFor="fullName">Nome Completo *</Label>
                            <Input
                                id="fullName"
                                value={fullName}
                                onChange={(e) => setFullName(e.target.value)}
                                required
                                placeholder="João Silva"
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="cpf">CPF *</Label>
                            <Input
                                id="cpf"
                                value={cpf}
                                onChange={(e) => setCpf(e.target.value)}
                                required
                                placeholder="000.000.000-00"
                                maxLength={14}
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="email">Email *</Label>
                            <Input
                                id="email"
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                                placeholder="joao@example.com"
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="birthDate">Data de Nascimento *</Label>
                            <Input
                                id="birthDate"
                                type="date"
                                value={birthDate}
                                onChange={(e) => setBirthDate(e.target.value)}
                                required
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="phone">Telefone *</Label>
                            <Input
                                id="phone"
                                value={phone}
                                onChange={(e) => setPhone(e.target.value)}
                                required
                                placeholder="(00) 00000-0000"
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="departmentId">Departamento</Label>
                            <select
                                id="departmentId"
                                value={departmentId}
                                onChange={(e) => setDepartmentId(e.target.value)}
                                className="w-full h-10 rounded-md border border-input bg-background px-3 py-2 text-sm"
                            >
                                <option value="">Selecione um departamento</option>
                                {departments.map((dept) => (
                                    <option key={dept.id} value={dept.id}>
                                        {dept.name}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="hireDate">Data de Contratação</Label>
                            <Input
                                id="hireDate"
                                type="date"
                                value={hireDate}
                                onChange={(e) => setHireDate(e.target.value)}
                                placeholder="Data de contratação"
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="specialty">Especialidade</Label>
                            <Input
                                id="specialty"
                                value={specialty}
                                onChange={(e) => setSpecialty(e.target.value)}
                                placeholder="Ex: Matemática, Física, etc."
                            />
                        </div>
                    </div>

                    <div className="border-t pt-4">
                        <h3 className="text-lg font-semibold mb-4">Cursos</h3>
                        <div className="space-y-2 max-h-40 overflow-y-auto border rounded-md p-3">
                            {courses.length === 0 ? (
                                <p className="text-sm text-muted-foreground">Nenhum curso disponível</p>
                            ) : (
                                courses.map((course) => (
                                    <label
                                        key={course.id}
                                        className="flex items-center space-x-2 cursor-pointer hover:bg-muted p-2 rounded"
                                    >
                                        <input
                                            type="checkbox"
                                            checked={courseIds.includes(course.id)}
                                            onChange={() => handleCourseToggle(course.id)}
                                            className="rounded border-gray-300"
                                        />
                                        <span className="text-sm">
                                            {course.name} {course.code_course && `(${course.code_course})`}
                                        </span>
                                    </label>
                                ))
                            )}
                        </div>
                    </div>

                    <div className="border-t pt-4">
                        <h3 className="text-lg font-semibold mb-4">Endereço</h3>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div className="space-y-2">
                                <Label htmlFor="cep">CEP *</Label>
                                <Input
                                    id="cep"
                                    value={cep}
                                    onChange={(e) => setCep(e.target.value)}
                                    required
                                    placeholder="00000-000"
                                    maxLength={9}
                                />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="street">Rua *</Label>
                                <Input
                                    id="street"
                                    value={street}
                                    onChange={(e) => setStreet(e.target.value)}
                                    required
                                    placeholder="Rua das Flores"
                                />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="number">Número *</Label>
                                <Input
                                    id="number"
                                    value={number}
                                    onChange={(e) => setNumber(e.target.value)}
                                    required
                                    placeholder="123"
                                />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="city">Cidade *</Label>
                                <Input
                                    id="city"
                                    value={city}
                                    onChange={(e) => setCity(e.target.value)}
                                    required
                                    placeholder="São Paulo"
                                />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="state">Estado *</Label>
                                <Input
                                    id="state"
                                    value={state}
                                    onChange={(e) => setState(e.target.value)}
                                    required
                                    placeholder="SP"
                                    maxLength={2}
                                />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="complement">Complemento</Label>
                                <Input
                                    id="complement"
                                    value={complement}
                                    onChange={(e) => setComplement(e.target.value)}
                                    placeholder="Apto 101"
                                />
                            </div>
                        </div>
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
                            {loading ? "Criando..." : "Criar Professor"}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    )
}

