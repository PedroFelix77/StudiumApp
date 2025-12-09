import { api } from "@/services/api";
import type { DashboardResponseDTO } from "../../types/dashboard";

export async function getAdminDashboard() {
  const res = await api.get("/dashboard/admin");
  return res.data as DashboardResponseDTO;
}

export async function getDirectorDashboard() {
  const res = await api.get("/dashboard/director");
  return res.data as DashboardResponseDTO;
}

export async function getTeacherDashboard(teacherId: string) {
  const res = await api.get(`/dashboard/teacher/${teacherId}`);
  return res.data as DashboardResponseDTO;
}

export async function getStudentDashboard(studentId: string) {
  const res = await api.get(`/dashboard/student/${studentId}`);
  return res.data as DashboardResponseDTO;
}
