import { api } from "@/services/api"

export interface CourseResponseDTO {
  id: string
  name: string
  code_course: string
}

export interface CourseRequestDTO {
  name: string
  code_course: string
  departmentId?: string
  institutionId: string
}

export const courseService = {
  list: (params?: any) =>
    api.get("/api/courses", { params }),

  create: (data: CourseRequestDTO) =>
    api.post("/api/courses", data),

  delete: (id: string) =>
    api.delete(`/api/courses/${id}`)
}
