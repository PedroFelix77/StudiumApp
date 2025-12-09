import { useState } from "react";
import { useAuth } from "@/context/AuthContext";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { Loader2 } from "lucide-react";
import { ForgotPassword } from "@/components/ForgotPassword";
import { useNavigate } from "react-router-dom";
import { getRolePath } from "@/lib/utils";

export default function Login() {
  const { login } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState(""); // <-- ok
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    setError("");

    try {
      const loggedUser = await login({ email, password });

      const path = getRolePath(loggedUser.role);
      navigate(`/${path}/dashboard`, { replace: true });

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
          <form onSubmit={handleSubmit} className="space-y-5">
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
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="focus-visible:ring-[rgb(16,70,132)]"
              />
            </div>

            {/* SENHA */}
            <div>
              <Label
                htmlFor="password"
                className="block mb-1 text-sm font-medium"
                style={{ color: "rgb(8, 36, 66)" }}
              >
                Senha
              </Label>
              <Input
                id="password"
                type="password"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="focus-visible:ring-[rgb(16,70,132)]"
              />
            </div>

            {/* ERRO */}
            {error && (
              <p
                className="text-sm text-center font-medium"
                style={{ color: "rgb(217, 38, 38)" }}
              >
                {error}
              </p>
            )}

            {/* BOTÃO */}
            <Button
              type="submit"
              disabled={isSubmitting}
              className="w-full font-semibold text-white transition-colors"
              style={{ backgroundColor: "rgb(16, 70, 132)" }}
            >
              {isSubmitting && (
                <Loader2 className="animate-spin mr-2 h-4 w-4" />
              )}
              {isSubmitting ? "Entrando..." : "Entrar"}
            </Button>
          </form>

          <ForgotPassword />
        </CardContent>
      </Card>
    </div>
  );
}
