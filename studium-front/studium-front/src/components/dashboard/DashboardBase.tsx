import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { ResponsiveContainer, BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid } from "recharts";
import type { DashboardResponseDTO } from "@/types/dashboard";

type Props = {
    data: DashboardResponseDTO;
};

export function DashboardBase({ data }: Props) {
    return (
        <div className="space-y-6 animate-fade-in">
            <div>
                <h1 className="text-3xl font-semibold text-foreground mb-2">Dashboard</h1>
                <p className="text-muted-foreground">Visão geral</p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                {data.stats.map((s) => (
                    <Card key={s.label}>
                        <CardHeader>
                            <CardTitle className="text-sm">{s.label}</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <div className="text-2xl font-bold">{s.value}</div>
                            {s.extra && <div className="text-sm text-muted-foreground">{s.extra}</div>}
                        </CardContent>
                    </Card>
                ))}
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                <Card className="lg:col-span-2">
                    <CardHeader>
                        <CardTitle>Desempenho dos Cursos</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <ResponsiveContainer width="100%" height={300}>
                            <BarChart data={data.coursePerformance}>
                                <CartesianGrid strokeDasharray="3 3" />
                                <XAxis dataKey="courseName" tick={{ fontSize: 12 }} angle={-45} textAnchor="end" height={80} />
                                <YAxis />
                                <Tooltip />
                                <Bar dataKey="studentCount" name="Alunos" radius={[8, 8, 0, 0]} />
                                {/* se averageGrade estiver disponível, você pode adicionar outro Bar/Line */}
                            </BarChart>
                        </ResponsiveContainer>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader>
                        <CardTitle>Atividades Recentes</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-4">
                            {data.recentActivities.map((a) => (
                                <div key={a.id}>
                                    <div className="text-sm">{a.text}</div>
                                    <div className="text-xs text-muted-foreground">{new Date(a.createdAt).toLocaleString()}</div>
                                </div>
                            ))}
                        </div>
                    </CardContent>
                </Card>
            </div>
        </div>
    );
}
