import { clsx, type ClassValue } from "clsx"
import { twMerge } from "tailwind-merge"

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

/**
 * Mapeia o role do usuário para o path da rota correspondente
 * Os roles vêm em inglês do backend, mas as rotas estão em português
 */
export function getRolePath(role: string): string {
  const roleUpper = role.toUpperCase();
  const roleToPathMap: Record<string, string> = {
    ADMIN: "admin",
    DIRECTOR: "diretor",
    TEACHER: "professor",
    STUDENT: "aluno",
  };
  
  return roleToPathMap[roleUpper] || "admin";
}
