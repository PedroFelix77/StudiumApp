export interface Student {
    id: string
    name: string
  }
  
  export interface ClassEntity {
    id: string
    name: string
  }
  
  export interface Registration {
    id: string
    registrationNumber: string
    student: Student
    classEntity: ClassEntity
    dateRegistration: string
  }
      