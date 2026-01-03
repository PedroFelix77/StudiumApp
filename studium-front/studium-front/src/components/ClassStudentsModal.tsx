"use client"

import { useEffect, useState } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Calendar, BookOpen } from "lucide-react"
import { registrationService, type StudentInClass } from "@/services/api/registrationService"
import StudentFrequencyModal from "@/components/StudentFrequencyModal"
import StudentGradesModal from "@/components/StudentGradesModal"

interface Props {
    open: boolean
    onOpenChange: (open: boolean) => void
    classId: string
}

export default function ClassStudentsModal({ open, onOpenChange, classId }: Props) {
    const [students, setStudents] = useState<StudentInClass[]>([])
    const [loading, setLoading] = useState(false)
    const [selectedRegistrationId, setSelectedRegistrationId] = useState<string | null>(null)
    const [openFrequencyModal, setOpenFrequencyModal] = useState(false)
    const [openGradesModal, setOpenGradesModal] = useState(false)

    useEffect(() => {
        if (open) {
            loadStudents()
        }
    }, [open])

    const loadStudents = async () => {
        setLoading(true)
        try {
            const data = await registrationService.getStudentsByClass(classId)
            setStudents(data)
        } finally {
            setLoading(false)
        }
    }

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="max-w-lg">
                <DialogHeader>
                    <DialogTitle>Alunos da Turma</DialogTitle>
                </DialogHeader>

                {loading ? (
                    <p>Carregando...</p>
                ) : students.length === 0 ? (
                    <p className="text-sm text-muted-foreground">
                        Nenhum aluno matriculado nesta turma.
                    </p>
                ) : (
                    <ul className="space-y-2">
                        {students.map((s) => (
                            <li
                                key={s.registrationId}
                                className="flex justify-between items-center border p-2 rounded gap-2"
                            >
                                <div className="flex-1">
                                    <span className="font-medium">{s.studentName}</span>
                                    <span className="text-sm text-muted-foreground ml-2">
                                        {s.registrationNumber}
                                    </span>
                                </div>
                                <div className="flex gap-1">
                                    <Button
                                        variant="outline"
                                        size="sm"
                                        onClick={() => {
                                            setSelectedRegistrationId(s.registrationId)
                                            setOpenFrequencyModal(true)
                                        }}
                                        className="gap-1"
                                    >
                                        <Calendar size={14} />
                                        Frequência
                                    </Button>
                                    <Button
                                        variant="outline"
                                        size="sm"
                                        onClick={() => {
                                            setSelectedRegistrationId(s.registrationId)
                                            setOpenGradesModal(true)
                                        }}
                                        className="gap-1"
                                    >
                                        <BookOpen size={14} />
                                        Notas
                                    </Button>
                                </div>
                            </li>
                        ))}
                    </ul>
                )}
            </DialogContent>

            {selectedRegistrationId && (
                <>
                    <StudentFrequencyModal
                        open={openFrequencyModal}
                        onOpenChange={setOpenFrequencyModal}
                        registrationId={selectedRegistrationId}
                    />
                    <StudentGradesModal
                        open={openGradesModal}
                        onOpenChange={setOpenGradesModal}
                        registrationId={selectedRegistrationId}
                    />
                </>
            )}
        </Dialog>
    )
}
