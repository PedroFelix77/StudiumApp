import { useState, useEffect } from "react";
import { api } from "@/services/api";

export interface StudentItem {
  id: string;
  name: string;
  registration: string;
  courseName?: string;
  status?: string;
}

export function useStudents(q = "", page = 0, size = 10) {
  const [items, setItems] = useState<StudentItem[]>([]);
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
      const res = await api.get("/students", { params: { q: qParam, page: p, size: s } });
      setItems(res.data.content || []);
      setTotal(res.data.totalElements ?? 0);
    } catch (err) {
      console.error("Erro ao carregar alunos", err);
    } finally {
      setLoading(false);
    }
  }

  return { items, loading, total, pageNumber, setPageNumber, refetch: fetch };
}
