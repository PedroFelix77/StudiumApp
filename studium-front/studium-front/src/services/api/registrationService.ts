import { api } from "../api"
export interface StudentInClass {
    registrationId: string
    studentId: string
    studentName: string
    registrationNumber: string
  }
  
  export const registrationService = {
    getStudentsByClass: async (classId: string): Promise<StudentInClass[]> => {
      const res = await api.get(`/api/registrations/class/${classId}/students`)
      return res.data
    }
  }
  