import { useState, useEffect } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { api } from "@/services/api";
import { BookOpen, User, TrendingUp } from "lucide-react";

interface Grade {
  id: string;
  studentId: string;
  studentName: string;
  studentRegistration?: string;
  courseId: string;
  courseName: string;
  courseCode?: string;
  examId?: string;
  examName?: string;
  examType?: string;
  grade: number;
  maxGrade?: number;
  date: string;
  createdAt?: string;
}

interface CourseGrades {
  courseId: string;
  courseName: string;
  courseCode?: string;
  grades: Grade[];
  average?: number;
}

interface StudentGrades {
  studentId: string;
  studentName: string;
  studentRegistration?: string;
  courses: CourseGrades[];
  overallAverage?: number;
}

export default function DiretorNotas() {
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedCourse, setSelectedCourse] = useState<string>("");
  const [selectedStudent, setSelectedStudent] = useState<string>("");
  const [viewMode, setViewMode] = useState<"byCourse" | "byStudent">("byCourse");
  const [grades, setGrades] = useState<Grade[]>([]);
  const [courses, setCourses] = useState<{ id: string; name: string; code?: string }[]>([]);
  const [students, setStudents] = useState<{ id: string; name: string; registration?: string }[]>([]);
  const [loading, setLoading] = useState(false);
  const [pageNumber, setPageNumber] = useState(0);
  const [total, setTotal] = useState(0);

  useEffect(() => {
    fetchCourses();
    fetchStudents();
  }, []);

  useEffect(() => {
    fetchGrades();
  }, [searchTerm, selectedCourse, selectedStudent, pageNumber]);

  async function fetchCourses() {
    try {
      const res = await api.get("/courses", { params: { page: 0, size: 100 } });
      setCourses(res.data.content || []);
    } catch (err) {
      console.error("Erro ao carregar cursos", err);
    }
  }

  async function fetchStudents() {
    try {
      const res = await api.get("/students", { params: { page: 0, size: 100 } });
      setStudents(res.data.content || []);
    } catch (err) {
      console.error("Erro ao carregar alunos", err);
    }
  }

  async function fetchGrades() {
    setLoading(true);
    try {
      const params: any = {
        q: searchTerm,
        page: pageNumber,
        size: 20,
      };
      if (selectedCourse) params.courseId = selectedCourse;
      if (selectedStudent) params.studentId = selectedStudent;

      const res = await api.get("/grades", { params });
      setGrades(res.data.content || []);
      setTotal(res.data.totalElements || 0);
    } catch (err) {
      console.error("Erro ao carregar notas", err);
    } finally {
      setLoading(false);
    }
  }

  // Agrupar notas por curso
  const gradesByCourse = grades.reduce((acc, grade) => {
    if (!acc[grade.courseId]) {
      acc[grade.courseId] = {
        courseId: grade.courseId,
        courseName: grade.courseName,
        courseCode: grade.courseCode,
        grades: [],
      };
    }
    acc[grade.courseId].grades.push(grade);
    return acc;
  }, {} as Record<string, CourseGrades>);

  // Calcular médias por curso
  Object.values(gradesByCourse).forEach((course) => {
    if (course.grades.length > 0) {
      const sum = course.grades.reduce((acc, g) => acc + g.grade, 0);
      course.average = sum / course.grades.length;
    }
  });

  // Agrupar notas por aluno
  const gradesByStudent = grades.reduce((acc, grade) => {
    if (!acc[grade.studentId]) {
      acc[grade.studentId] = {
        studentId: grade.studentId,
        studentName: grade.studentName,
        studentRegistration: grade.studentRegistration,
        courses: [],
      };
    }

    let courseGroup = acc[grade.studentId].courses.find(c => c.courseId === grade.courseId);
    if (!courseGroup) {
      courseGroup = {
        courseId: grade.courseId,
        courseName: grade.courseName,
        courseCode: grade.courseCode,
        grades: [],
      };
      acc[grade.studentId].courses.push(courseGroup);
    }
    courseGroup.grades.push(grade);
    return acc;
  }, {} as Record<string, StudentGrades>);

  // Calcular médias por aluno e curso
  Object.values(gradesByStudent).forEach((student) => {
    let totalSum = 0;
    let totalCount = 0;

    student.courses.forEach((course) => {
      if (course.grades.length > 0) {
        const sum = course.grades.reduce((acc, g) => acc + g.grade, 0);
        course.average = sum / course.grades.length;
        totalSum += course.average;
        totalCount++;
      }
    });

    if (totalCount > 0) {
      student.overallAverage = totalSum / totalCount;
    }
  });

  const getGradeColor = (grade: number) => {
    if (grade >= 7) return "text-green-600";
    if (grade >= 5) return "text-yellow-600";
    return "text-red-600";
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-semibold">Gestão de Notas</h1>
        <p className="text-muted-foreground">Visualize e gerencie notas dos alunos por curso ou por aluno</p>
      </div>

      {/* Filtros */}
      <Card>
        <CardHeader>
          <CardTitle>Filtros</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <div>
              <Input
                placeholder="Buscar por aluno, curso ou avaliação..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
            <div>
              <select
                className="w-full h-9 rounded-md border border-input bg-transparent px-3 py-1 text-sm"
                value={selectedCourse}
                onChange={(e) => setSelectedCourse(e.target.value)}
              >
                <option value="">Todos os cursos</option>
                {courses.map((course) => (
                  <option key={course.id} value={course.id}>
                    {course.code ? `${course.code} - ` : ""}{course.name}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <select
                className="w-full h-9 rounded-md border border-input bg-transparent px-3 py-1 text-sm"
                value={selectedStudent}
                onChange={(e) => setSelectedStudent(e.target.value)}
              >
                <option value="">Todos os alunos</option>
                {students.map((student) => (
                  <option key={student.id} value={student.id}>
                    {student.registration ? `${student.registration} - ` : ""}{student.name}
                  </option>
                ))}
              </select>
            </div>
            <div className="flex gap-2">
              <Button
                variant={viewMode === "byCourse" ? "default" : "outline"}
                onClick={() => setViewMode("byCourse")}
                className="flex-1"
              >
                <BookOpen size={16} className="mr-2" />
                Por Curso
              </Button>
              <Button
                variant={viewMode === "byStudent" ? "default" : "outline"}
                onClick={() => setViewMode("byStudent")}
                className="flex-1"
              >
                <User size={16} className="mr-2" />
                Por Aluno
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>

      {loading && <div className="text-center py-8">Carregando...</div>}

      {!loading && viewMode === "byCourse" && (
        <div className="space-y-4">
          {Object.values(gradesByCourse).map((course) => (
            <Card key={course.courseId}>
              <CardHeader>
                <div className="flex justify-between items-center">
                  <div>
                    <CardTitle className="flex items-center gap-2">
                      <BookOpen size={20} />
                      {course.courseCode && <span className="text-muted-foreground">{course.courseCode} - </span>}
                      {course.courseName}
                    </CardTitle>
                    {course.average !== undefined && (
                      <p className="text-sm text-muted-foreground mt-1">
                        Média do curso: <span className={`font-semibold ${getGradeColor(course.average)}`}>{course.average.toFixed(2)}</span>
                      </p>
                    )}
                  </div>
                  <div className="text-sm text-muted-foreground">
                    {course.grades.length} {course.grades.length === 1 ? "nota" : "notas"}
                  </div>
                </div>
              </CardHeader>
              <CardContent>
                <table className="w-full text-left">
                  <thead>
                    <tr className="text-sm text-muted-foreground border-b">
                      <th className="p-2">Aluno</th>
                      <th className="p-2">Matrícula</th>
                      <th className="p-2">Avaliação</th>
                      <th className="p-2">Tipo</th>
                      <th className="p-2">Nota</th>
                      <th className="p-2">Data</th>
                    </tr>
                  </thead>
                  <tbody>
                    {course.grades.map((grade) => (
                      <tr key={grade.id} className="border-b">
                        <td className="p-2">{grade.studentName}</td>
                        <td className="p-2 text-muted-foreground">{grade.studentRegistration ?? "-"}</td>
                        <td className="p-2">{grade.examName ?? "-"}</td>
                        <td className="p-2 text-muted-foreground text-sm">{grade.examType ?? "-"}</td>
                        <td className="p-2">
                          <span className={`font-semibold ${getGradeColor(grade.grade)}`}>
                            {grade.grade.toFixed(1)}
                            {grade.maxGrade && ` / ${grade.maxGrade}`}
                          </span>
                        </td>
                        <td className="p-2 text-muted-foreground text-sm">
                          {new Date(grade.date).toLocaleDateString()}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </CardContent>
            </Card>
          ))}
          {Object.keys(gradesByCourse).length === 0 && (
            <Card>
              <CardContent className="py-8 text-center text-muted-foreground">
                Nenhuma nota encontrada
              </CardContent>
            </Card>
          )}
        </div>
      )}

      {!loading && viewMode === "byStudent" && (
        <div className="space-y-4">
          {Object.values(gradesByStudent).map((student) => (
            <Card key={student.studentId}>
              <CardHeader>
                <div className="flex justify-between items-center">
                  <div>
                    <CardTitle className="flex items-center gap-2">
                      <User size={20} />
                      {student.studentName}
                      {student.studentRegistration && (
                        <span className="text-muted-foreground font-normal">({student.studentRegistration})</span>
                      )}
                    </CardTitle>
                    {student.overallAverage !== undefined && (
                      <p className="text-sm text-muted-foreground mt-1 flex items-center gap-1">
                        <TrendingUp size={16} />
                        Média geral: <span className={`font-semibold ${getGradeColor(student.overallAverage)}`}>{student.overallAverage.toFixed(2)}</span>
                      </p>
                    )}
                  </div>
                </div>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {student.courses.map((course) => (
                    <div key={course.courseId} className="border rounded-lg p-4">
                      <div className="flex justify-between items-center mb-3">
                        <h4 className="font-semibold">
                          {course.courseCode && <span className="text-muted-foreground">{course.courseCode} - </span>}
                          {course.courseName}
                        </h4>
                        {course.average !== undefined && (
                          <span className={`font-semibold ${getGradeColor(course.average)}`}>
                            Média: {course.average.toFixed(2)}
                          </span>
                        )}
                      </div>
                      <div className="grid grid-cols-2 md:grid-cols-4 gap-2">
                        {course.grades.map((grade) => (
                          <div key={grade.id} className="border rounded p-2">
                            <div className="text-xs text-muted-foreground">{grade.examName ?? "Avaliação"}</div>
                            <div className={`font-semibold ${getGradeColor(grade.grade)}`}>
                              {grade.grade.toFixed(1)}
                              {grade.maxGrade && ` / ${grade.maxGrade}`}
                            </div>
                            <div className="text-xs text-muted-foreground">
                              {new Date(grade.date).toLocaleDateString()}
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          ))}
          {Object.keys(gradesByStudent).length === 0 && (
            <Card>
              <CardContent className="py-8 text-center text-muted-foreground">
                Nenhuma nota encontrada
              </CardContent>
            </Card>
          )}
        </div>
      )}

      {/* Paginação */}
      {total > 0 && (
        <div className="flex justify-between items-center">
          <div className="text-sm text-muted-foreground">{total} resultados encontrados</div>
          <div className="space-x-2">
            <Button onClick={() => setPageNumber(Math.max(0, pageNumber - 1))} disabled={pageNumber === 0}>
              Anterior
            </Button>
            <Button onClick={() => setPageNumber(pageNumber + 1)} disabled={grades.length < 20}>
              Próxima
            </Button>
          </div>
        </div>
      )}
    </div>
  );
}
