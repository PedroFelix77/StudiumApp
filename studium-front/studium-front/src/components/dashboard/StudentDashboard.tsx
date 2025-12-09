import { useEffect, useState } from "react";
import { DashboardBase } from "@/components/dashboard/DashboardBase";
import { getStudentDashboard } from "@/services/api/dashboard";
import type { DashboardResponseDTO } from "@/types/dashboard";
import { Card } from "@/components/ui/card";
import { useAuth } from "@/context/AuthContext";

export default function StudentDashboard() {
    const [data, setData] = useState<DashboardResponseDTO | null>(null);
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState("");
    const { user } = useAuth();
    const studentId = user?.id;

    useEffect(() => {
        if (!studentId) return;
        setLoading(true);
        getStudentDashboard(studentId)
            .then((d) => setData(d))
            .catch((e) => setErr(e?.response?.data?.message || e.message || "Erro"))
            .finally(() => setLoading(false));
    }, []);

    if (loading) return <Card><div>Carregando...</div></Card>;
    if (err) return <Card><div className="text-red-600">{err}</div></Card>;
    if (!data) return null;

    return <DashboardBase data={data} />;
}
