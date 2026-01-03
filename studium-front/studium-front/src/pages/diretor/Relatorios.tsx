import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Download, FileText, BarChart3, Users, GraduationCap } from "lucide-react";
import { api } from "@/services/api";

export default function DiretorRelatorios() {
  const [loading, setLoading] = useState<string | null>(null);

  async function generateReport(type: string) {
    setLoading(type);
    try {
      const res = await api.get(`/reports/${type}`, { responseType: "blob" });
      const url = window.URL.createObjectURL(new Blob([res.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", `relatorio-${type}-${new Date().toISOString().split("T")[0]}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (err) {
      console.error("Erro ao gerar relatório", err);
      alert("Erro ao gerar relatório");
    } finally {
      setLoading(null);
    }
  }

  const reports = [
    {
      id: "students",
      title: "Relatório de Alunos",
      description: "Lista completa de alunos cadastrados",
      icon: Users,
    },
    {
      id: "teachers",
      title: "Relatório de Professores",
      description: "Lista completa de professores cadastrados",
      icon: GraduationCap,
    },
    {
      id: "grades",
      title: "Relatório de Notas",
      description: "Médias e desempenho dos alunos",
      icon: BarChart3,
    },
    {
      id: "attendance",
      title: "Relatório de Frequência",
      description: "Estatísticas de frequência dos alunos",
      icon: FileText,
    },
  ];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-semibold">Relatórios</h1>
        <p className="text-muted-foreground">Gere e baixe relatórios da instituição</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {reports.map((report) => {
          const Icon = report.icon;
          const isLoading = loading === report.id;
          return (
            <Card key={report.id}>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Icon size={20} />
                  {report.title}
                </CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-sm text-muted-foreground mb-4">{report.description}</p>
                <Button
                  onClick={() => generateReport(report.id)}
                  disabled={isLoading}
                  className="w-full"
                >
                  {isLoading ? (
                    "Gerando..."
                  ) : (
                    <>
                      <Download size={16} className="mr-2" />
                      Gerar Relatório
                    </>
                  )}
                </Button>
              </CardContent>
            </Card>
          );
        })}
      </div>
    </div>
  );
}
