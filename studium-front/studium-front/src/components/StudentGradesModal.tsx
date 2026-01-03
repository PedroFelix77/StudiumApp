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
import { BookOpen, TrendingUp } from "lucide-react"

interface Props {
    open: boolean
    onOpenChange: (open: boolean) => void
    registrationId: string
}

interface Grade {
    id: string
    studentId: string
    studentName: string
    courseId: string
    courseName: string
    courseCode?: string
    examId?: string
    examName?: string
    examType?: string
    grade: number
    maxGrade?: number
    date: string
    createdAt?: string
}

export default function StudentGradesModal({
    open,
    onOpenChange,
    registrationId
}: Props) {
    const [grades, setGrades] = useState<Grade[]>([])
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
            const registrationRes = await api.get(`/registrations/${registrationId}`)
            const registration = registrationRes.data
            setStudentId(registration.studentId || registration.student?.id)

            if (registration.studentId || registration.student?.id) {
                loadGrades(registration.studentId || registration.student?.id)
            }
        } catch (err) {
            console.error("Erro ao carregar dados da matrícula", err)
        }
    }

    const loadGrades = async (sid: string) => {
        setLoading(true)
        try {
            const res = await api.get("/grades", {
                params: {
                    studentId: sid,
                    page: 0,
                    size: 100
                }
            })
            setGrades(res.data.content || [])
        } catch (err) {
            console.error("Erro ao carregar notas", err)
            setGrades([])
        } finally {
            setLoading(false)
        }
    }

    const getGradeColor = (grade: number) => {
        if (grade >= 7) return "text-green-600"
        if (grade >= 5) return "text-yellow-600"
        return "text-red-600"
    }

    // Agrupar notas por curso
    const gradesByCourse = grades.reduce((acc, grade) => {
        if (!acc[grade.courseId]) {
            acc[grade.courseId] = {
                courseId: grade.courseId,
                courseName: grade.courseName,
                courseCode: grade.courseCode,
                grades: []
            }
        }
        acc[grade.courseId].grades.push(grade)
        return acc
    }, {} as Record<string, { courseId: string; courseName: string; courseCode?: string; grades: Grade[]; average?: number }>)

    // Calcular médias por curso
    Object.values(gradesByCourse).forEach((course) => {
        if (course.grades.length > 0) {
            const sum = course.grades.reduce((acc, g) => acc + g.grade, 0)
            course.average = sum / course.grades.length
        }
    })

    const overallAverage =
        Object.values(gradesByCourse).length > 0
            ? Object.values(gradesByCourse).reduce((acc, course) => acc + (course.average || 0), 0) /
              Object.values(gradesByCourse).length
            : 0

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="max-w-3xl max-h-[80vh] overflow-y-auto">
                <DialogHeader>
                    <DialogTitle>Notas do Aluno</DialogTitle>
                    <DialogDescription>
                        Visualize o histórico de notas deste aluno
                    </DialogDescription>
                </DialogHeader>

                {loading ? (
                    <p className="text-sm text-muted-foreground">Carregando...</p>
                ) : (
                    <>
                        {/* MÉDIA GERAL */}
                        {grades.length > 0 && (
                            <div className="p-4 bg-muted rounded-lg mb-4">
                                <div className="flex items-center gap-2">
                                    <TrendingUp className="h-5 w-5 text-muted-foreground" />
                                    <div>
                                        <p className="text-sm text-muted-foreground">Média Geral</p>
                                        <p className={`text-2xl font-bold ${getGradeColor(overallAverage)}`}>
                                            {overallAverage.toFixed(2)}
                                        </p>
                                    </div>
                                </div>
                            </div>
                        )}

                        {/* NOTAS POR CURSO */}
                        {Object.keys(gradesByCourse).length === 0 ? (
                            <p className="text-sm text-muted-foreground text-center py-8">
                                Nenhuma nota registrada
                            </p>
                        ) : (
                            <div className="space-y-4">
                                {Object.values(gradesByCourse).map((course) => (
                                    <div key={course.courseId} className="border rounded-md p-4">
                                        <div className="flex items-center justify-between mb-3">
                                            <div>
                                                <h3 className="font-semibold">{course.courseName}</h3>
                                                {course.courseCode && (
                                                    <p className="text-sm text-muted-foreground">
                                                        {course.courseCode}
                                                    </p>
                                                )}
                                            </div>
                                            {course.average !== undefined && (
                                                <div className="text-right">
                                                    <p className="text-sm text-muted-foreground">Média</p>
                                                    <p className={`text-xl font-bold ${getGradeColor(course.average)}`}>
                                                        {course.average.toFixed(2)}
                                                    </p>
                                                </div>
                                            )}
                                        </div>
                                        <div className="space-y-2">
                                            {course.grades.map((grade) => (
                                                <div
                                                    key={grade.id}
                                                    className="flex items-center justify-between p-2 bg-muted rounded"
                                                >
                                                    <div>
                                                        <p className="font-medium">
                                                            {grade.examName || "Avaliação"}
                                                        </p>
                                                        <p className="text-sm text-muted-foreground">
                                                            {grade.examType && `${grade.examType} • `}
                                                            {new Date(grade.date).toLocaleDateString("pt-BR")}
                                                        </p>
                                                    </div>
                                                    <div className="text-right">
                                                        <p className={`text-lg font-bold ${getGradeColor(grade.grade)}`}>
                                                            {grade.grade.toFixed(2)}
                                                            {grade.maxGrade && ` / ${grade.maxGrade}`}
                                                        </p>
                                                    </div>
                                                </div>
                                            ))}
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

