import { useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";

import {
  Dialog,
  DialogTrigger,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
  DialogClose,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

const forgotPasswordSchema = z.object({
  email: z
    .string()
    .min(1, "O e-mail é obrigatório")
    .email("Digite um e-mail válido"),
});

type ForgotPasswordFormData = z.infer<typeof forgotPasswordSchema>;

export function ForgotPassword() {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [emailSent, setEmailSent] = useState(false);

  const form = useForm<ForgotPasswordFormData>({
    resolver: zodResolver(forgotPasswordSchema),
    mode: "onChange",
  });

  const onSubmit = async (data: ForgotPasswordFormData) => {
    setIsSubmitting(true);
    setEmailSent(false);

    try {
      // Aqui no futuro: integração com o endpoint /api/auth/recover
      console.log("Simulando envio de e-mail para:", data.email);

      // Simula atraso de envio
      await new Promise((resolve) => setTimeout(resolve, 1500));

      setEmailSent(true);
    } catch (error) {
      console.error("Erro ao enviar e-mail:", error);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Dialog>
      <DialogTrigger asChild>
        <div className="mt-4 text-center text-sm text-muted-foreground">
          <a
            href="#"
            className="hover:underline"
            style={{ color: "rgb(16, 70, 132)" }}
          >
            Esqueceu sua senha?
          </a>
        </div>
      </DialogTrigger>

      <DialogContent>
        <DialogHeader>
          <DialogTitle>Redefinir senha</DialogTitle>
          <DialogDescription>
            Para redefinir sua senha, informe seu e-mail institucional.
            Enviaremos um link com instruções.
          </DialogDescription>
        </DialogHeader>

        {/* FORMULÁRIO */}
        {!emailSent ? (
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
            <div className="grid grid-cols-4 items-center text-right gap-3">
              <Label htmlFor="email">E-mail</Label>
              <Input
                id="email"
                type="email"
                placeholder="exemplo@studium.edu.br"
                {...form.register("email")}
                className="col-span-3 focus-visible:ring-[rgb(16,70,132)]"
              />
            </div>

            {form.formState.errors.email && (
              <p className="text-sm text-red-600 text-center">
                {form.formState.errors.email.message}
              </p>
            )}

            <DialogFooter className="mt-4 flex justify-end space-x-3">
              <DialogClose asChild>
                <Button type="button" variant="destructive">
                  Voltar
                </Button>
              </DialogClose>
              <Button
                type="submit"
                variant="default"
                disabled={isSubmitting || !form.formState.isValid}
                style={{ backgroundColor: "rgb(16, 70, 132)" }}
              >
                {isSubmitting ? "Enviando..." : "Recuperar senha"}
              </Button>
            </DialogFooter>
          </form>
        ) : (
          //MENSAGEM DE SUCESSO APÓS ENVIO
          <div className="text-center space-y-4 py-4">
            <p className="text-green-600 font-medium">
              Um link de redefinição foi enviado para o seu e-mail!
            </p>
            <DialogFooter>
              <DialogClose asChild>
                <Button variant="default" style={{ backgroundColor: "rgb(16,70,132)" }}>
                  Voltar para o login
                </Button>
              </DialogClose>
            </DialogFooter>
          </div>
        )}
      </DialogContent>
    </Dialog>
  );
}
