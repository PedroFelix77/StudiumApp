import { useState, useEffect } from "react";
import { api } from "@/services/api";
import { getDirectorDashboard } from "@/services/api/dashboard";
import type { DashboardResponseDTO } from "@/types/dashboard";

export interface AdminDashboardData {
  totalStudents: number;
  totalTeachers: number;
  totalCourses: number;
  totalDepartments: number;
  recentRegistrations: { studentName: string; courseName: string; createdAt: string }[];
}

export function useAdminDashboard() {
  const [data, setData] = useState<AdminDashboardData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setLoading(true);
    api.get("/dashboard/admin")
      .then(res => setData(res.data))
      .catch(e => {
        console.error(e);
        setError(e?.response?.data?.message || "Erro ao carregar dashboard");
      })
      .finally(() => setLoading(false));
  }, []);

  return { data, loading, error };
}

export function useDirectorDashboard() {
  const [data, setData] = useState<DashboardResponseDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setLoading(true);
    getDirectorDashboard()
      .then(res => setData(res))
      .catch(e => {
        console.error(e);
        setError(e?.response?.data?.message || "Erro ao carregar dashboard");
      })
      .finally(() => setLoading(false));
  }, []);

  return { data, loading, error };
}
