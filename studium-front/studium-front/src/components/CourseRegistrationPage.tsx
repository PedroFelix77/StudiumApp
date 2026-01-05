"use client"

import { useEffect, useState } from "react"
import { Button } from "@/components/ui/button"
import { Plus } from "lucide-react"
import { api } from "@/services/api"
import RegistrationModal from "./course-registration-modal"

export default function CourseRegistrationsPage({ courseId }: { courseId: string }) {
    const [registrations, setRegistrations] = useState([])
    const [openModal, setOpenModal] = useState(false)

    const loadRegistrations = async () => {
        const res = await api.get(`/api/registrations/course/${courseId}`)
        setRegistrations(res.data)
    }

    useEffect(() => {
        loadRegistrations()
    }, [])

    return (
        <div className="space-y-6">
            <div className="flex justify-between">
                <h2 className="text-2xl font-bold">Matrículas</h2>
                <Button onClick={() => setOpenModal(true)}>
                    <Plus size={18} /> Matricular Aluno
                </Button>
            </div>

            <ul className="space-y-2">
                {registrations.map((r: any) => (
                    <li key={r.id} className="border p-3 rounded">
                        {r.student.name} — {r.registrationNumber}
                    </li>
                ))}
            </ul>

            <RegistrationModal
                open={openModal}
                onOpenChange={setOpenModal}
                courseId={courseId}
                onSuccess={loadRegistrations}
            />
        </div>
    )
}
