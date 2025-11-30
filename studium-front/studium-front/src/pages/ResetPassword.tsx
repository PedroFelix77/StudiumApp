import { useState } from "react";
import { useSearchParams, Link } from "react-router-dom";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";

import { Card, CardContent, CardHeader, CardTitle, CardFooter } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

import { api } from "@/services/api";

const schema = z
    .object({
        password: z.string().min(6, "A senha deve ter no mínimo 6 caracteres"),
        confirmPassword: z.string(),
    })
    .refine((data) => data.password === data.confirmPassword, {
        message: "As senhas não coincidem",
        path: ["confirmPassword"],
    });

export default function ResetPassword() {
    const [searchParams] = useSearchParams();
    const token = searchParams.get("token");

    const [success, setSuccess] = useState(false);
    const [error, setError] = useState("");
    const [isExpiredToken, setIsExpiredToken] = useState(false);
    const form = useForm({
        resolver: zodResolver(schema),
    });

    const onSubmit = async (data: any) => {
        try {
            await api.post("/auth/reset-password", {
                token,
                newPassword: data.password,
            });

            setSuccess(true);
            setError("");
            setIsExpiredToken(false);

        } catch (err: any) {
            const msg =
                err.response?.data?.message ||
                err.response?.data ||
                "Erro ao redefinir a senha.";

            setError(msg);

            // token expirado ou inválido → mostra botão
            if (
                err.response?.status === 403 ||
                msg.toLowerCase().includes("token") ||
                msg.toLowerCase().includes("expir")
            ) {
                setIsExpiredToken(true);
            }
        }
    };


    // --------------------------------------------
    // TOKEN AUSENTE → NÃO TEM COMO REENVIAR
    // --------------------------------------------
    if (!token) {
        return (
            <div className="flex flex-col items-center justify-center h-screen px-4">
                <div className="max-w-md w-full p-6 bg-white shadow rounded-lg text-center">
                    <h2 className="text-2xl font-semibold text-red-600 mb-2">
                        Token inválido!
                    </h2>
                    <p className="text-gray-600 mb-6">
                        O link está incorreto ou não existe.
                    </p>

                    <Link to="/forgot-password">
                        <Button className="bg-primary text-white">
                            Solicitar novo link
                        </Button>
                    </Link>
                </div>
            </div>
        );
    }


    return (
        <div className="flex items-center justify-center min-h-screen bg-muted/20 px-4">
            <Card className="w-full max-w-md shadow-lg">

                {!success ? (
                    <>
                        <CardHeader>
                            <CardTitle className="text-center text-2xl font-bold">
                                Redefinir senha
                            </CardTitle>
                        </CardHeader>

                        <CardContent>
                            {/* TOKEN EXPIRADO → MOSTRA BOTÃO */}
                            {isExpiredToken ? (
                                <div className="text-center space-y-4 py-4">

                                    <p className="text-red-600 font-medium">
                                        Este link expirou.
                                    </p>



                                    {error && (
                                        <p className="text-sm text-red-600 font-medium mt-3">
                                            {error}
                                        </p>
                                    )}
                                </div>
                            ) : (
                                // FORM NORMAL
                                <form
                                    onSubmit={form.handleSubmit(onSubmit)}
                                    className="space-y-6"
                                >
                                    <div className="space-y-2">
                                        <Label>Nova senha</Label>
                                        <Input
                                            type="password"
                                            placeholder="Digite a nova senha"
                                            {...form.register("password")}
                                        />
                                        {form.formState.errors.password && (
                                            <p className="text-sm text-red-600">
                                                {form.formState.errors.password.message}
                                            </p>
                                        )}
                                    </div>

                                    <div className="space-y-2">
                                        <Label>Confirmar nova senha</Label>
                                        <Input
                                            type="password"
                                            placeholder="Repita a nova senha"
                                            {...form.register("confirmPassword")}
                                        />
                                        {form.formState.errors.confirmPassword && (
                                            <p className="text-sm text-red-600">
                                                {form.formState.errors.confirmPassword.message}
                                            </p>
                                        )}
                                    </div>

                                    {error && (
                                        <p className="text-sm text-red-600 text-center font-medium">
                                            {error}
                                        </p>
                                    )}

                                    <Button
                                        type="submit"
                                        className="w-full bg-primary text-white"
                                    >
                                        Redefinir senha
                                    </Button>
                                </form>
                            )}
                        </CardContent>
                    </>
                ) : (
                    // SUCESSO
                    <>
                        <CardHeader>
                            <CardTitle className="text-center text-green-600 text-2xl">
                                Senha redefinida!
                            </CardTitle>
                        </CardHeader>

                        <CardContent className="text-center text-muted-foreground">
                            Agora você já pode fazer login com sua nova senha.
                        </CardContent>

                        <CardFooter className="flex justify-center">
                            <Link to="/login">
                                <Button className="bg-primary text-white">
                                    Ir para o login
                                </Button>
                            </Link>
                        </CardFooter>
                    </>
                )}
            </Card>
        </div>
    );
}
