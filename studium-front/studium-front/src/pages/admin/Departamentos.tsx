import { useState, useEffect } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Edit, Trash2, Plus } from "lucide-react";
import { api } from "@/services/api";
import { CreateDepartmentDialog } from "@/components/CreateDepartmentDialog";

interface Department {
  id: string;
  name: string;
  code?: string;
  teacherCount?: number;
  courseCount?: number;
}

export default function AdminDepartamentos() {
  const [q, setQ] = useState("");
  const [departments, setDepartments] = useState<Department[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [pageNumber, setPageNumber] = useState(0);

  useEffect(() => {
    fetchDepartments();
  }, [q, pageNumber]);

  async function fetchDepartments() {
    setLoading(true);
    try {
      const res = await api.get("/api/departments");
      const data = Array.isArray(res.data) ? res.data : [];

      setDepartments(data);
      setTotal(data.length);
    } catch (err) {
      console.error("Erro ao carregar departamentos", err);
    } finally {
      setLoading(false);
    }
  }

  async function handleDelete(id: string) {
    if (!confirm("Deseja desativar este departamento?")) return;
    try {
      await api.delete(`/api/departments/${id}`);
      fetchDepartments();
    } catch (err) {
      console.error(err);
      alert("Erro ao desativar departamento");
    }
  }

  const [showCreateDialog, setShowCreateDialog] = useState(false);

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-semibold">Gestão de Departamentos</h1>
          <p className="text-muted-foreground">Liste, pesquise e gerencie departamentos</p>
        </div>
        <Button onClick={() => setShowCreateDialog(true)} className="gap-2">
          <Plus size={18} />
          Adicionar Departamento
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Departamentos Registrados</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="mb-4 flex gap-3">
            <Input placeholder="Buscar por nome..." value={q} onChange={(e) => setQ(e.target.value)} />
            <Button onClick={() => { setPageNumber(0); fetchDepartments(); }}>Buscar</Button>
          </div>

          <table className="w-full text-left">
            <thead>
              <tr className="text-sm text-muted-foreground">
                <th className="p-2">Nome</th>
                <th className="p-2">Código</th>
                <th className="p-2">Professores</th>
                <th className="p-2">Cursos</th>
                <th className="p-2">Ações</th>
              </tr>
            </thead>
            <tbody>
              {loading && <tr><td colSpan={5}>Carregando...</td></tr>}
              {!loading && departments.length === 0 && <tr><td colSpan={5}>Nenhum departamento encontrado</td></tr>}
              {departments.map((dept) => (
                <tr key={dept.id} className="border-t">
                  <td className="p-2">{dept.name}</td>
                  <td className="p-2">{dept.code ?? "-"}</td>
                  <td className="p-2">{dept.teacherCount ?? 0}</td>
                  <td className="p-2">{dept.courseCount ?? 0}</td>
                  <td className="p-2">
                    <div className="flex gap-2">
                      <button title="Editar" className="p-1"><Edit size={16} /></button>
                      <button title="Desativar" className="p-1 text-red-600" onClick={() => handleDelete(dept.id)}><Trash2 size={16} /></button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          <div className="mt-4 flex justify-between items-center">
            <div>{total} resultados</div>
            <div className="space-x-2">
              <Button onClick={() => setPageNumber(Math.max(0, pageNumber - 1))}>Anterior</Button>
              <Button onClick={() => setPageNumber(pageNumber + 1)}>Próxima</Button>
            </div>
          </div>
        </CardContent>
      </Card>

      <CreateDepartmentDialog
        open={showCreateDialog}
        onOpenChange={setShowCreateDialog}
        onSuccess={fetchDepartments}
      />
    </div>
  );
}
