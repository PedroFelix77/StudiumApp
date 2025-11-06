import { BrowserRouter, Routes, Route } from "react-router-dom";
import { ProtectedRoute } from "./ProtectedRoute";
import { AuthProvider } from "../context/AuthContext"; // ✅ importante

import { AdminLayout } from "../layouts/AdminLayout";
import { ProfessorLayout } from "../layouts/ProfessorLayout";
import { AlunoLayout } from "../layouts/AlunoLayout";

import AdminDashboard from "../pages/admin/Dashboard";
import AdminAlunos from "../pages/admin/Alunos";
import AdminProfessores from "../pages/admin/Professores";
import AdminCursos from "../pages/admin/Cursos";
import AdminNotas from "../pages/admin/Notas";
import AdminRelatorios from "../pages/admin/Relatorios";
import AdminFrequencias from "../pages/admin/Frequencias";

import ProfDashboard from "../pages/professor/Dashboard";
import ProfAlunos from "../pages/professor/Alunos";
import ProfCursos from "../pages/professor/Cursos";
import ProfNotas from "../pages/professor/Notas";
import ProfFrequencias from "../pages/professor/Frequencia";

import AlunoDashboard from "../pages/aluno/Dashboard";
import AlunoCurso from "../pages/aluno/Curso";
import AlunoNotas from "../pages/aluno/Notas";
import AlunoFrequencia from "../pages/aluno/Frequencia";

import Login from "@/pages/Login"; 

export function AppRoutes() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>

          {/* LOGIN */}
          <Route path="/login" element={<Login />} />

          {/* ADMIN */}
          <Route
            path="/admin"
            element={
              <ProtectedRoute allowedRoles={["admin"]}>
                <AdminLayout />
              </ProtectedRoute>
            }
          >
            <Route index element={<AdminDashboard />} />
            <Route path="alunos" element={<AdminAlunos />} />
            <Route path="professores" element={<AdminProfessores />} />
            <Route path="cursos" element={<AdminCursos />} />
            <Route path="frequencias" element={<AdminFrequencias />} />
            <Route path="notas" element={<AdminNotas />} />
            <Route path="relatorios" element={<AdminRelatorios />} />
          </Route>

          {/* PROFESSOR */}
          <Route
            path="/professor"
            element={
              <ProtectedRoute allowedRoles={["professor"]}>
                <ProfessorLayout />
              </ProtectedRoute>
            }
          >
            <Route index element={<ProfDashboard />} />
            <Route path="alunos" element={<ProfAlunos />} />
            <Route path="cursos" element={<ProfCursos />} />
            <Route path="frequencias" element={<ProfFrequencias />} />
            <Route path="notas" element={<ProfNotas />} />
          </Route>

          {/* ALUNO */}
          <Route
            path="/aluno"
            element={
              <ProtectedRoute allowedRoles={["aluno"]}>
                <AlunoLayout />
              </ProtectedRoute>
            }
          >
            <Route index element={<AlunoDashboard />} />
            <Route path="curso" element={<AlunoCurso />} />
            <Route path="notas" element={<AlunoNotas />} />
            <Route path="frequencias" element={<AlunoFrequencia />} />
          </Route>

          {/* ROTA PADRÃO */}
          <Route path="*" element={<Login />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
