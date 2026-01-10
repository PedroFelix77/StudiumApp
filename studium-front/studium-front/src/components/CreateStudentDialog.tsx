import { useState } from "react";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { api } from "@/services/api";
import { useAuth } from "@/context/AuthContext";

interface CreateStudentDialogProps {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    onSuccess?: () => void;
}

export function CreateStudentDialog({
    open,
    onOpenChange,
    onSuccess,
}: CreateStudentDialogProps) {
    const { user } = useAuth();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    // Form fields
    const [fullName, setFullName] = useState("");
    const [cpf, setCpf] = useState("");
    const [email, setEmail] = useState("");
    const [birthDate, setBirthDate] = useState("");
    const [phone, setPhone] = useState("");

    // Address fields
    const [cep, setCep] = useState("");
    const [street, setStreet] = useState("");
    const [number, setNumber] = useState("");
    const [city, setCity] = useState("");
    const [state, setState] = useState("");
    const [complement, setComplement] = useState("");

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError("");
        setLoading(true);

        try {
            const payload = {
                user: {
                    name: fullName,
                    email,
                    cpf,
                    birthday: birthDate,
                    phone,
                },
                address: {
                    cep,
                    street,
                    number,
                    city,
                    state,
                    complement: complement || "",
                },
                responsibleName: fullName,
                responsiblePhone: phone,
            };

            await api.post("/api/director/students", payload);

            onOpenChange(false);
            onSuccess?.();
        } catch (err: any) {
            console.error("Erro ao criar aluno:", err);
            setError(
                err.response?.data?.message || "Erro ao criar aluno. Tente novamente."
            );
        } finally {
            setLoading(false);
        }
    };

    const handleClose = () => {
        if (!loading) {
            onOpenChange(false);
            setError("");
        }
    };

    return (
        <Dialog open={open} onOpenChange={handleClose}>
            <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
                <DialogHeader>
                    <DialogTitle>Criar Novo Aluno</DialogTitle>
                    <DialogDescription>
                        Preencha os dados abaixo para criar um novo aluno no sistema.
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit} className="space-y-4">
                    {error && (
                        <div className="p-3 text-sm text-red-600 bg-red-50 rounded-md">
                            {error}
                        </div>
                    )}

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div className="space-y-2">
                            <Label htmlFor="fullName">Nome Completo *</Label>
                            <Input
                                id="fullName"
                                value={fullName}
                                onChange={(e) => setFullName(e.target.value)}
                                required
                                placeholder="João Silva"
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="cpf">CPF *</Label>
                            <Input
                                id="cpf"
                                value={cpf}
                                onChange={(e) => setCpf(e.target.value)}
                                required
                                placeholder="000.000.000-00"
                                maxLength={14}
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="email">Email *</Label>
                            <Input
                                id="email"
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                                placeholder="joao@example.com"
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="birthDate">Data de Nascimento *</Label>
                            <Input
                                id="birthDate"
                                type="date"
                                value={birthDate}
                                onChange={(e) => setBirthDate(e.target.value)}
                                required
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="phone">Telefone *</Label>
                            <Input
                                id="phone"
                                value={phone}
                                onChange={(e) => setPhone(e.target.value)}
                                required
                                placeholder="(00) 00000-0000"
                            />
                        </div>
                    </div>

                    <div className="border-t pt-4">
                        <h3 className="text-lg font-semibold mb-4">Endereço</h3>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div className="space-y-2">
                                <Label htmlFor="cep">CEP *</Label>
                                <Input
                                    id="cep"
                                    value={cep}
                                    onChange={(e) => setCep(e.target.value)}
                                    required
                                    placeholder="00000-000"
                                    maxLength={9}
                                />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="street">Rua *</Label>
                                <Input
                                    id="street"
                                    value={street}
                                    onChange={(e) => setStreet(e.target.value)}
                                    required
                                    placeholder="Rua das Flores"
                                />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="number">Número *</Label>
                                <Input
                                    id="number"
                                    value={number}
                                    onChange={(e) => setNumber(e.target.value)}
                                    required
                                    placeholder="123"
                                />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="city">Cidade *</Label>
                                <Input
                                    id="city"
                                    value={city}
                                    onChange={(e) => setCity(e.target.value)}
                                    required
                                    placeholder="São Paulo"
                                />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="state">Estado *</Label>
                                <Input
                                    id="state"
                                    value={state}
                                    onChange={(e) => setState(e.target.value)}
                                    required
                                    placeholder="SP"
                                    maxLength={2}
                                />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="complement">Complemento</Label>
                                <Input
                                    id="complement"
                                    value={complement}
                                    onChange={(e) => setComplement(e.target.value)}
                                    placeholder="Apto 101"
                                />
                            </div>
                        </div>
                    </div>

                    <DialogFooter>
                        <Button
                            type="button"
                            variant="outline"
                            onClick={handleClose}
                            disabled={loading}
                        >
                            Cancelar
                        </Button>
                        <Button type="submit" disabled={loading}>
                            {loading ? "Criando..." : "Criar Aluno"}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
}
