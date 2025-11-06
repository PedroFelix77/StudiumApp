CREATE TABLE addresses (
    id UUID PRIMARY KEY,
    street VARCHAR(255),
    number VARCHAR(6),
    complement VARCHAR(255),
    city VARCHAR(255),
    cep VARCHAR(8),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE institutions (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    cnpj CHAR(14) NOT NULL UNIQUE,
    telephone VARCHAR(9) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    logo VARCHAR(255),
    address_id UUID REFERENCES addresses(id) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE departments (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    institution_id UUID REFERENCES institutions(id) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE courses (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code_course VARCHAR(10) NOT NULL,
    department_id UUID REFERENCES departments(id) NOT NULL,
    institution_id UUID REFERENCES institutions(id) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE disciplines (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    course_id UUID REFERENCES courses(id) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE classes (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code_class VARCHAR(255) NOT NULL,
    academic_year VARCHAR(100) NOT NULL,
    course_id UUID REFERENCES courses(id) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE classrooms (
    id UUID PRIMARY KEY,
    class_date TIMESTAMP NOT NULL,
    content VARCHAR(255),
    class_id UUID REFERENCES classes(id) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    cpf CHAR(11) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    birth_day DATE NOT NULL,
    phone VARCHAR(10) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('STUDENT', 'TEACHER', 'ADMIN')),
    institution_id UUID REFERENCES institutions(id),
    address_id UUID REFERENCES addresses(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE students (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE teachers (
    id UUID PRIMARY KEY,
    specialty VARCHAR(100) NOT NULL,
    date_of_hire DATE NOT NULL,
    department_id UUID REFERENCES departments(id),
    user_id UUID REFERENCES users(id) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE registrations (
    id UUID PRIMARY KEY,
    registration_number VARCHAR(10) UNIQUE NOT NULL,
    date_registration DATE NOT NULL,
    student_id UUID REFERENCES students(id) NOT NULL,
    course_id UUID REFERENCES courses(id) NOT NULL,
    class_id UUID REFERENCES classes(id) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE frequencies (
    id UUID PRIMARY KEY,
    registration_id UUID REFERENCES registrations(id) NOT NULL,
    classroom_id UUID REFERENCES classrooms(id) NOT NULL,
    registered_by_teacher_id UUID REFERENCES teachers(id) NOT NULL,
    attendance_date TIMESTAMP NOT NULL,
    status VARCHAR(20),
    justification VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE grades (
    id UUID PRIMARY KEY,
    grade NUMERIC(5,2) NOT NULL,
    type_grade VARCHAR(50) NOT NULL,
    registration_id UUID REFERENCES registrations(id) NOT NULL,
    recorded_by_teacher_id UUID REFERENCES teachers(id) NOT NULL,
    discipline_id UUID REFERENCES disciplines(id) NOT NULL,
    class_id UUID REFERENCES classes(id) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE teacher_class (
    id UUID PRIMARY KEY,
    teacher_id UUID REFERENCES teachers(id) NOT NULL,
    class_id UUID REFERENCES classes(id) NOT NULL,
    course_id UUID REFERENCES courses(id) NOT NULL,
    observations VARCHAR(255),
    is_main_teacher BOOLEAN,
    weekly_hours INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE admins (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
