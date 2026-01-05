import { useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { useTeachers } from "@/hooks/dashboard/useTeacher";
import { Trash2, Edit, Plus } from "lucide-react";
import { api } from "@/services/api";
import { CreateTeacherDialog } from "@/components/CreateTeacherDialog";

export default function AdminTeachersPage() {
  const [q, setQ] = useState("");
  const { items, loading, total, pageNumber, setPageNumber, refetch } = useTeachers(q, 0, 10);
  const [showCreateDialog, setShowCreateDialog] = useState(false);

  async function handleDelete(id: string) {
    if (!confirm("Deseja desativar este professor?")) return;
    try {
      await api.delete(`/api/teachers/${id}`);
      window.location.reload();
    } catch (err) {
      console.error(err);
      alert("Erro ao desativar professor");
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-semibold">Gestão de Professores</h1>
          <p className="text-muted-foreground">Liste, pesquise e gerencie professores</p>
        </div>
        <Button onClick={() => setShowCreateDialog(true)} className="gap-2">
          <Plus size={18} />
          Adicionar Professor
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Professores Registrados</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="mb-4 flex gap-3">
            <Input placeholder="Buscar por nome ou email..." value={q} onChange={(e) => setQ(e.target.value)} />
            <Button onClick={() => setPageNumber(0)}>Buscar</Button>
          </div>

          <table className="w-full text-left">
            <thead>
              <tr className="text-sm text-muted-foreground">
                <th className="p-2">Nome</th>
                <th className="p-2">Departamento</th>
                <th className="p-2">Email</th>
                <th className="p-2">Ações</th>
              </tr>
            </thead>
            <tbody>
              {loading && <tr><td colSpan={4}>Carregando...</td></tr>}
              {!loading && items.length === 0 && <tr><td colSpan={4}>Nenhum professor encontrado</td></tr>}
              {items.map((t) => (
                <tr key={t.id} className="border-t">
                  <td className="p-2">{t.name}</td>
                  <td className="p-2">{t.departmentName ?? "-"}</td>
                  <td className="p-2">{t.email ?? "-"}</td>
                  <td className="p-2">
                    <div className="flex gap-2">
                      <button title="Editar" className="p-1"><Edit size={16} /></button>
                      <button title="Desativar" className="p-1 text-red-600" onClick={() => handleDelete(t.id)}><Trash2 size={16} /></button>
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

      <CreateTeacherDialog
        open={showCreateDialog}
        onOpenChange={setShowCreateDialog}
        onSuccess={() => {
          refetch(q, pageNumber, 10);
        }}
      />
    </div>
  );
}
