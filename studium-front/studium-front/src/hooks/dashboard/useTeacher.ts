import { useState, useEffect } from "react";
import { api } from "@/services/api";

export interface TeacherItem {
  id: string;
  name: string;
  departmentName?: string;
  email?: string;
  status?: string;
}

export function useTeachers(q = "", page = 0, size = 10) {
  const [items, setItems] = useState<TeacherItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [pageNumber, setPageNumber] = useState(page);

  useEffect(() => {
    fetch(q, pageNumber, size);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [q, pageNumber, size]);

  async function fetch(qParam = q, p = pageNumber, s = size) {
    setLoading(true);
    try {
      const res = await api.get("/api/teachers", { params: { q: qParam, page: p, size: s } });
      const teachers = res.data.content || [];
      // Mapear dados aninhados para estrutura plana
      const mappedItems: TeacherItem[] = teachers.map((teacher: any) => ({
        id: teacher.id,
        name: teacher.user?.name || "",
        departmentName: teacher.department?.name || "",
        email: teacher.user?.email || "",
        status: teacher.user?.status || "",
      }));
      setItems(mappedItems);
      setTotal(res.data.totalElements ?? 0);
    } catch (err) {
      console.error("Erro ao carregar professores", err);
    } finally {
      setLoading(false);
    }
  }

  return { items, loading, total, pageNumber, setPageNumber, refetch: fetch };
}
