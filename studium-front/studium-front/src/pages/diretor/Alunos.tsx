import { useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { useStudents } from "@/hooks/dashboard/useStudent";
import { Trash2, Edit, Plus } from "lucide-react";
import { api } from "@/services/api";
import { CreateStudentDialog } from "@/components/CreateStudentDialog";

export default function DiretorAlunos() {
  const [q, setQ] = useState("");
  const [dialogOpen, setDialogOpen] = useState(false);
  const { items, loading, total, pageNumber, setPageNumber, refetch } = useStudents(q, 0, 10);

  async function handleDelete(id: string) {
    if (!confirm("Deseja desativar este aluno?")) return;
    try {
      await api.delete(`/api/students/${id}`);
      window.location.reload();
    } catch (err) {
      console.error(err);
      alert("Erro ao desativar aluno");
    }
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-semibold">Gestão de Alunos</h1>
        <p className="text-muted-foreground">Liste, pesquise e gerencie alunos</p>
      </div>

      <Card>
        <CardHeader>
          <div className="flex justify-between items-center">
            <CardTitle>Alunos Registrados</CardTitle>
            <Button onClick={() => setDialogOpen(true)}>
              <Plus size={16} className="mr-2" />
              Criar Aluno
            </Button>
          </div>
        </CardHeader>
        <CardContent>
          <div className="mb-4 flex gap-3">
            <Input placeholder="Buscar por nome ou matrícula..." value={q} onChange={(e) => setQ(e.target.value)} />
            <Button onClick={() => setPageNumber(0)}>Buscar</Button>
          </div>

          <table className="w-full text-left">
            <thead>
              <tr className="text-sm text-muted-foreground">
                <th className="p-2">Nome</th>
                <th className="p-2">Matrícula</th>
                <th className="p-2">Curso</th>
                <th className="p-2">Status</th>
                <th className="p-2">Ações</th>
              </tr>
            </thead>
            <tbody>
              {loading && <tr><td colSpan={5}>Carregando...</td></tr>}
              {!loading && items.length === 0 && <tr><td colSpan={5}>Nenhum aluno encontrado</td></tr>}
              {items.map((s) => (
                <tr key={s.id} className="border-t">
                  <td className="p-2">{s.name}</td>
                  <td className="p-2">{s.registration}</td>
                  <td className="p-2">{s.courseName ?? "-"}</td>
                  <td className="p-2">{s.status ?? "INATIVO"}</td>
                  <td className="p-2">
                    <div className="flex gap-2">
                      <button title="Editar" className="p-1"><Edit size={16} /></button>
                      <button title="Desativar" className="p-1 text-red-600" onClick={() => handleDelete(s.id)}><Trash2 size={16} /></button>
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

      <CreateStudentDialog
        open={dialogOpen}
        onOpenChange={setDialogOpen}
        onSuccess={() => {
          refetch();
        }}
      />
    </div>
  );
}