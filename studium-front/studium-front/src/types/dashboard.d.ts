export interface DashboardStatDTO {
    label: string;
    value: number;
    extra?: string | null;
  }
  
  export interface DashboardCoursePerformanceDTO {
    courseId: string;
    courseName: string;
    studentCount: number;
    averageGrade?: number | null;
  }
  
  export interface DashboardActivityDTO {
    id: string;
    text: string;
    createdAt: string;
  }
  
  export interface DashboardResponseDTO {
    stats: DashboardStatDTO[];
    coursePerformance: DashboardCoursePerformanceDTO[];
    recentActivities: DashboardActivityDTO[];
  }
  