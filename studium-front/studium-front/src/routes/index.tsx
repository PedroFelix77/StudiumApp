import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { ProtectedRoute } from "./ProtectedRoute";
import { AuthProvider } from "../context/AuthContext";

// LAYOUTS
import { AppLayout } from "../layouts/AppLayout";

// ADMIN
import AdminDashboard from "../pages/admin/Dashboard";
import AdminAlunos from "../pages/admin/Alunos";
import AdminProfessores from "../pages/admin/Professores";
import AdminCursos from "../pages/admin/Cursos";
import AdminNotas from "../pages/admin/Notas";
import AdminRelatorios from "../pages/admin/Relatorios";
import AdminFrequencias from "../pages/admin/Frequencias";

// PROFESSOR
import ProfDashboard from "../pages/professor/Dashboard";
import ProfAlunos from "../pages/professor/Alunos";
import ProfCursos from "../pages/professor/Cursos";
import ProfNotas from "../pages/professor/Notas";
import ProfFrequencias from "../pages/professor/Frequencia";

// ALUNO
import AlunoDashboard from "../pages/aluno/Dashboard";
import AlunoCurso from "../pages/aluno/Curso";
import AlunoNotas from "../pages/aluno/Notas";
import AlunoFrequencia from "../pages/aluno/Frequencia";

// DIRETOR 
import DiretorDashboard from "@/pages/diretor/Dashboard";
import DiretorProfessores from "@/pages/diretor/Professores";
import DiretorAlunos from "@/pages/diretor/Alunos";
import DiretorCursos from "@/pages/diretor/Cursos";
import DiretorRelatorios from "@/pages/diretor/Relatorios";

import Login from "@/pages/Login";
import ResetPassword from "@/pages/ResetPassword";
import ActivateAccount from "@/pages/ActivateAccount";
import DepartamentoAdmin from "@/pages/admin/Departamentos";
import DiretorDepartamentos from "@/pages/diretor/Departamentos";
import DiretorFrequencias from "@/pages/diretor/Frequencias";
import DiretorNotas from "@/pages/diretor/Notas";

export function AppRoutes() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* ROTA PADRÃO - REDIRECIONA PARA LOGIN */}
          <Route path="/" element={<Navigate to="/login" replace />} />
          {/* LOGIN */}
          <Route path="/login" element={<Login />} />

          {/* ATIVAR CONTA */}
          <Route path="/activate" element={<ActivateAccount />} />
          <Route path="/reset-password" element={<ResetPassword />} />


          {/* ADMIN */}
          <Route
            path="/admin"
            element={
              <ProtectedRoute allowedRoles={["ADMIN"]}>
                <AppLayout />
              </ProtectedRoute>
            }
          >
            <Route path="dashboard" element={<AdminDashboard />} />
            <Route path="alunos" element={<AdminAlunos />} />
            <Route path="professores" element={<AdminProfessores />} />
            <Route path="cursos" element={<AdminCursos />} />
            <Route path="departamentos" element={<DepartamentoAdmin />} />
            <Route path="frequencias" element={<AdminFrequencias />} />
            <Route path="notas" element={<AdminNotas />} />
            <Route path="relatorios" element={<AdminRelatorios />} />
          </Route>

          {/* PROFESSOR */}
          <Route
            path="/professor"
            element={
              <ProtectedRoute allowedRoles={["TEACHER"]}>
                <AppLayout />
              </ProtectedRoute>
            }
          >
            <Route path="dashboard" element={<ProfDashboard />} />
            <Route path="alunos" element={<ProfAlunos />} />
            <Route path="cursos" element={<ProfCursos />} />
            <Route path="frequencias" element={<ProfFrequencias />} />
            <Route path="notas" element={<ProfNotas />} />
          </Route>

          {/* ALUNO */}
          <Route
            path="/aluno"
            element={
              <ProtectedRoute allowedRoles={["STUDENT"]}>
                <AppLayout />
              </ProtectedRoute>
            }
          >
            <Route path="dashboard" element={<AlunoDashboard />} />
            <Route path="curso" element={<AlunoCurso />} />
            <Route path="notas" element={<AlunoNotas />} />
            <Route path="frequencias" element={<AlunoFrequencia />} />
          </Route>

          {/* DIRETOR */}
          <Route
            path="/diretor"
            element={
              <ProtectedRoute allowedRoles={["DIRECTOR"]}>
                <AppLayout />
              </ProtectedRoute>
            }
          >
            <Route path="dashboard" element={<DiretorDashboard />} />
            <Route path="professores" element={<DiretorProfessores />} />
            <Route path="departamentos" element={<DiretorDepartamentos />} />
            <Route path="frequencia" element={<DiretorFrequencias />} />
            <Route path="notas" element={<DiretorNotas />} />
            <Route path="alunos" element={<DiretorAlunos />} />
            <Route path="cursos" element={<DiretorCursos />} />
            <Route path="relatorios" element={<DiretorRelatorios />} />
          </Route>

        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
