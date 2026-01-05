import { useState, useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { Loader2 } from "lucide-react";
import { api } from "@/services/api";

export default function ActivateAccount() {
  const [searchParams] = useSearchParams();
  const activationToken = searchParams.get("token");

  const navigate = useNavigate();

  const [password, setPassword] = useState("");
  const [confirm, setConfirm] = useState("");
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const [resending, setResending] = useState(false);
  const [canResend, setCanResend] = useState(false);

  useEffect(() => {
    if (!activationToken) {
      setError("Token de ativação inválido.");
    }
  }, [activationToken]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    if (password !== confirm) {
      setError("As senhas não coincidem.");
      return;
    }

    if (!activationToken) {
      setError("Token de ativação inválido.");
      return;
    }

    setLoading(true);

    try {
      await api.post("/api/auth/activate", {
        token: activationToken,
        newPassword: password,
      });

      setSuccessMessage("Conta ativada com sucesso! Redirecionando...");
      setTimeout(() => navigate("/login"), 2000);

    } catch (err: any) {
      const msg = err.response?.data?.message || "Não foi possível ativar a conta.";

      setError(msg);

      // se o token expirou → habilitar botão de reenvio
      if (msg.toLowerCase().includes("expir")) {
        setCanResend(true);
      }

    } finally {
      setLoading(false);
    }
  };

  const handleResend = async () => {
    if (!activationToken) return;

    setResending(true);
    setError("");

    try {
      await api.post("/api/auth/resend", { token: activationToken });

      setSuccessMessage("Um novo link foi enviado! Verifique seu email.");
      setCanResend(false);
    } catch (err: any) {
      setError("Falha ao reenviar o link. Tente novamente.");
    } finally {
      setResending(false);
    }
  };

  return (
    <div
      className="flex items-center justify-center min-h-screen"
      style={{ backgroundColor: "rgb(250, 250, 250)" }}
    >
      <Card
        className="w-full max-w-md shadow-lg border"
        style={{ backgroundColor: "white", borderColor: "rgb(209, 218, 235)" }}
      >
        <CardHeader>
          <CardTitle
            className="text-center text-2xl font-semibold"
            style={{ color: "rgb(8, 36, 66)" }}
          >
            Ativar Conta
          </CardTitle>
        </CardHeader>

        <CardContent>
          {!activationToken ? (
            <p className="text-center text-red-600 font-medium">
              Token inválido ou ausente.
            </p>
          ) : (
            <form onSubmit={handleSubmit} className="space-y-5">

              {/* SENHAS */}
              <div>
                <Label
                  htmlFor="password"
                  style={{ color: "rgb(8, 36, 66)" }}
                  className="block mb-1 text-sm font-medium"
                >
                  Defina sua senha
                </Label>
                <Input
                  id="password"
                  type="password"
                  placeholder="Digite sua nova senha"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="focus-visible:ring-[rgb(16,70,132)]"
                />
              </div>

              <div>
                <Label
                  htmlFor="confirm"
                  style={{ color: "rgb(8, 36, 66)" }}
                  className="block mb-1 text-sm font-medium"
                >
                  Confirmar senha
                </Label>
                <Input
                  id="confirm"
                  type="password"
                  placeholder="Repita sua senha"
                  value={confirm}
                  onChange={(e) => setConfirm(e.target.value)}
                  className="focus-visible:ring-[rgb(16,70,132)]"
                />
              </div>

              {/* ERRO */}
              {error && (
                <p className="text-sm text-center font-medium text-red-600">
                  {error}
                </p>
              )}

              {/* SUCESSO */}
              {successMessage && (
                <p className="text-sm text-center font-medium text-green-600">
                  {successMessage}
                </p>
              )}

              {/* BOTÃO ATIVAR */}
              {!successMessage && (
                <Button
                  type="submit"
                  disabled={loading}
                  className="w-full font-semibold text-white transition-colors"
                  style={{ backgroundColor: "rgb(16, 70, 132)" }}
                >
                  {loading ? (
                    <>
                      <Loader2 className="animate-spin mr-2 h-4 w-4" /> Ativando...
                    </>
                  ) : (
                    "Ativar Conta"
                  )}
                </Button>
              )}

              {/* BOTÃO DE REENVIO */}
              {canResend && (
                <Button
                  type="button"
                  onClick={handleResend}
                  disabled={resending}
                  className="w-full font-semibold mt-3 text-white"
                  style={{ backgroundColor: "rgb(132, 16, 16)" }}
                >
                  {resending ? (
                    <>
                      <Loader2 className="animate-spin mr-2 h-4 w-4" />
                      Reenviando...
                    </>
                  ) : (
                    "Reenviar link de ativação"
                  )}
                </Button>
              )}

            </form>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
