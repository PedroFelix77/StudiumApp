import { useState, useEffect } from "react";
import { api } from "@/services/api";
import type { DashboardResponseDTO } from "@/types/dashboard";

export function useAdminDashboard() {
  const [data, setData] = useState<DashboardResponseDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setLoading(true);
    api.get("/api/dashboard/admin")
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
    api.get("/api/dashboard/director")
      .then(res => setData(res.data))
      .catch(e => {
        console.error(e);
        setError(e?.response?.data?.message || "Erro ao carregar dashboard");
      })
      .finally(() => setLoading(false));
  }, []);

  return { data, loading, error };
}
