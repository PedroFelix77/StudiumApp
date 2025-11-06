import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useAuth } from "@/context/AuthContext";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { Loader2 } from "lucide-react";
import { ForgotPassword } from "@/components/ForgotPassword";
import { useState } from "react";

const loginSchema = z.object({
  email: z
    .string()
    .min(1, "O e-mail é obrigatório")
    .email("Digite um e-mail válido"),
  senha: z
    .string()
    .min(6, "A senha deve ter pelo menos 6 caracteres")
});

type LoginFormData = z.infer<typeof loginSchema>;

export default function Login() {
  const { login } = useAuth(); //pega do context
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");

  const form = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
    mode: "onChange",
  });

  const onSubmit = async (data: LoginFormData) => {
    setIsSubmitting(true);
    setError("");

    try {
      await login({ email: data.email, senha: data.senha });
    } catch (err) {
      setError("Credenciais inválidas. Verifique e tente novamente.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div
      className="flex items-center justify-center min-h-screen"
      style={{ backgroundColor: "rgb(250, 250, 250)" }}
    >
      <Card
        className="w-full max-w-md shadow-lg border"
        style={{
          backgroundColor: "white",
          borderColor: "rgb(209, 218, 235)",
        }}
      >
        <CardHeader>
          <CardTitle
            className="text-center text-2xl font-semibold"
            style={{ color: "rgb(8, 36, 66)" }}
          >
            Studium — Login
          </CardTitle>
        </CardHeader>

        <CardContent>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-5">
            {/* E-MAIL */}
            <div>
              <Label
                htmlFor="email"
                className="block mb-1 text-sm font-medium"
                style={{ color: "rgb(8, 36, 66)" }}
              >
                E-mail Institucional
              </Label>
              <Input
                id="email"
                type="email"
                placeholder="exemplo@studium.edu.br"
                {...form.register("email")}
                className="focus-visible:ring-[rgb(16,70,132)]"
              />
              {form.formState.errors.email && (
                <p className="text-sm text-red-600 mt-1">
                  {form.formState.errors.email.message}
                </p>
              )}
            </div>

            {/* SENHA */}
            <div>
              <Label
                htmlFor="senha"
                className="block mb-1 text-sm font-medium"
                style={{ color: "rgb(8, 36, 66)" }}
              >
                Senha
              </Label>
              <Input
                id="senha"
                type="password"
                placeholder="••••••••"
                {...form.register("senha")}
                className="focus-visible:ring-[rgb(16,70,132)]"
              />
              {form.formState.errors.senha && (
                <p className="text-sm text-red-600 mt-1">
                  {form.formState.errors.senha.message}
                </p>
              )}
            </div>

            {/* ERRO GERAL */}
            {error && (
              <p
                className="text-sm text-center font-medium"
                style={{ color: "rgb(217, 38, 38)" }}
              >
                {error}
              </p>
            )}

            {/* BOTÃO LOGIN */}
            <Button
              type="submit"
              disabled={isSubmitting || !form.formState.isValid}
              className="w-full font-semibold text-white transition-colors"
              style={{
                backgroundColor: "rgb(16, 70, 132)",
              }}
            >
              {isSubmitting ? (
                <Loader2 className="animate-spin mr-2 h-4 w-4" />
              ) : null}
              {isSubmitting ? "Entrando..." : "Entrar"}
            </Button>
          </form>

          {/* 👇 Forgot Password */}
          <ForgotPassword />
        </CardContent>
      </Card>
    </div>
  );
}
