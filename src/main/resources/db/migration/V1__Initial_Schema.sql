-- Departments table
CREATE TABLE departments (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(10) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    hod_id BIGINT,
    established_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Users table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(120) NOT NULL,
    department_id BIGINT REFERENCES departments(id),
    roles TEXT NOT NULL,
    selected_role VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Add foreign key constraint to departments for HOD
ALTER TABLE departments ADD CONSTRAINT fk_departments_hod FOREIGN KEY (hod_id) REFERENCES users(id);

-- Students table
CREATE TABLE students (
    id SERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    department_id BIGINT REFERENCES departments(id),
    first_name VARCHAR(50),
    middle_name VARCHAR(50),
    last_name VARCHAR(50),
    full_name VARCHAR(150),
    enrollment_no VARCHAR(50) NOT NULL UNIQUE,
    personal_email VARCHAR(100),
    institutional_email VARCHAR(100) NOT NULL UNIQUE,
    batch VARCHAR(20),
    semester INTEGER NOT NULL DEFAULT 1,
    -- Semester status
    sem1 VARCHAR(20) DEFAULT 'NOT_ATTEMPTED',
    sem2 VARCHAR(20) DEFAULT 'NOT_ATTEMPTED',
    sem3 VARCHAR(20) DEFAULT 'NOT_ATTEMPTED',
    sem4 VARCHAR(20) DEFAULT 'NOT_ATTEMPTED',
    sem5 VARCHAR(20) DEFAULT 'NOT_ATTEMPTED',
    sem6 VARCHAR(20) DEFAULT 'NOT_ATTEMPTED',
    sem7 VARCHAR(20) DEFAULT 'NOT_ATTEMPTED',
    sem8 VARCHAR(20) DEFAULT 'NOT_ATTEMPTED',
    status VARCHAR(20) DEFAULT 'active',
    -- Guardian details
    guardian_name VARCHAR(100),
    guardian_relation VARCHAR(50),
    guardian_contact VARCHAR(20),
    guardian_occupation VARCHAR(100),
    -- Contact details
    mobile VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    city VARCHAR(50),
    state VARCHAR(50),
    pincode VARCHAR(10),
    -- Other details
    gender VARCHAR(2),
    category VARCHAR(20),
    aadhar_no VARCHAR(20),
    admission_year INTEGER,
    convo_year INTEGER,
    is_complete BOOLEAN DEFAULT FALSE,
    term_close BOOLEAN DEFAULT FALSE,
    is_cancel BOOLEAN DEFAULT FALSE,
    is_pass_all BOOLEAN DEFAULT FALSE,
    shift INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Student Education table
CREATE TABLE student_education (
    id SERIAL PRIMARY KEY,
    student_id BIGINT REFERENCES students(id),
    degree VARCHAR(100) NOT NULL,
    institution VARCHAR(100) NOT NULL,
    board VARCHAR(100) NOT NULL,
    percentage DOUBLE PRECISION NOT NULL,
    year_of_passing INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Faculty table
CREATE TABLE faculty (
    id SERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) UNIQUE,
    department_id BIGINT REFERENCES departments(id),
    employee_id VARCHAR(50) NOT NULL UNIQUE,
    designation VARCHAR(100) NOT NULL,
    specializations TEXT,
    joining_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'active',
    experience_years INTEGER DEFAULT 0,
    experience_details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Faculty Qualifications table
CREATE TABLE faculty_qualifications (
    id SERIAL PRIMARY KEY,
    faculty_id BIGINT REFERENCES faculty(id),
    degree VARCHAR(100) NOT NULL,
    field VARCHAR(100) NOT NULL,
    institution VARCHAR(100) NOT NULL,
    year INTEGER NOT NULL
);

-- Results table
CREATE TABLE results (
    id SERIAL PRIMARY KEY,
    st_id VARCHAR(50) NOT NULL,
    enrollment_no VARCHAR(50) NOT NULL,
    extype VARCHAR(50),
    exam_id INTEGER,
    exam VARCHAR(100),
    declaration_date DATE,
    academic_year VARCHAR(20),
    semester INTEGER NOT NULL,
    unit_no DOUBLE PRECISION,
    exam_number DOUBLE PRECISION,
    name VARCHAR(100) NOT NULL,
    inst_code INTEGER,
    inst_name VARCHAR(100),
    course_name VARCHAR(100),
    branch_code INTEGER,
    branch_name VARCHAR(100) NOT NULL,
    total_credits DOUBLE PRECISION,
    earned_credits DOUBLE PRECISION,
    spi DOUBLE PRECISION,
    cpi DOUBLE PRECISION,
    cgpa DOUBLE PRECISION,
    result VARCHAR(20),
    trials INTEGER DEFAULT 1,
    remark TEXT,
    current_backlog INTEGER DEFAULT 0,
    total_backlog INTEGER DEFAULT 0,
    upload_batch VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(enrollment_no, exam_id)
);

-- Result Subjects table
CREATE TABLE result_subjects (
    id SERIAL PRIMARY KEY,
    result_id BIGINT REFERENCES results(id),
    code VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    credits DOUBLE PRECISION NOT NULL,
    grade VARCHAR(5),
    is_backlog BOOLEAN DEFAULT FALSE,
    theory_ese_grade VARCHAR(5),
    theory_pa_grade VARCHAR(5),
    theory_total_grade VARCHAR(5),
    practical_pa_grade VARCHAR(5),
    practical_viva_grade VARCHAR(5),
    practical_total_grade VARCHAR(5)
);

-- Project Events table
CREATE TABLE project_events (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    event_date DATE NOT NULL,
    registration_start_date DATE NOT NULL,
    registration_end_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    status VARCHAR(20) DEFAULT 'UPCOMING',
    publish_results BOOLEAN DEFAULT FALSE,
    created_by BIGINT REFERENCES users(id),
    updated_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Event Departments table (many-to-many relationship)
CREATE TABLE event_departments (
    event_id BIGINT REFERENCES project_events(id),
    department_id BIGINT REFERENCES departments(id),
    PRIMARY KEY (event_id, department_id)
);

-- Event Schedules table
CREATE TABLE event_schedules (
    id SERIAL PRIMARY KEY,
    event_id BIGINT REFERENCES project_events(id),
    time VARCHAR(50) NOT NULL,
    activity VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL,
    coordinator_id BIGINT REFERENCES users(id),
    coordinator_name VARCHAR(100) NOT NULL,
    notes TEXT
);

-- Project Teams table
CREATE TABLE project_teams (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department_id BIGINT REFERENCES departments(id),
    event_id BIGINT REFERENCES project_events(id),
    created_by BIGINT REFERENCES users(id),
    updated_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Team Members table
CREATE TABLE team_members (
    id SERIAL PRIMARY KEY,
    team_id BIGINT REFERENCES project_teams(id),
    user_id BIGINT REFERENCES users(id),
    name VARCHAR(100) NOT NULL,
    enrollment_no VARCHAR(50),
    role VARCHAR(50) DEFAULT 'Member',
    is_leader BOOLEAN DEFAULT FALSE
);

-- Project Locations table
CREATE TABLE project_locations (
    id SERIAL PRIMARY KEY,
    location_id VARCHAR(20) NOT NULL UNIQUE,
    section VARCHAR(10) NOT NULL,
    position INTEGER NOT NULL,
    department_id BIGINT REFERENCES departments(id),
    event_id BIGINT REFERENCES project_events(id),
    is_assigned BOOLEAN DEFAULT FALSE,
    created_by BIGINT REFERENCES users(id),
    updated_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Projects table
CREATE TABLE projects (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    category VARCHAR(100) NOT NULL,
    abstract TEXT NOT NULL,
    department_id BIGINT REFERENCES departments(id),
    status VARCHAR(20) DEFAULT 'DRAFT',
    -- Requirements
    power BOOLEAN DEFAULT FALSE,
    internet BOOLEAN DEFAULT FALSE,
    special_space BOOLEAN DEFAULT FALSE,
    other_requirements TEXT,
    -- Guide
    guide_user_id BIGINT REFERENCES users(id),
    guide_name VARCHAR(100) NOT NULL,
    guide_department_id BIGINT REFERENCES departments(id),
    guide_contact_number VARCHAR(20) NOT NULL,
    -- Foreign keys
    team_id BIGINT REFERENCES project_teams(id),
    event_id BIGINT REFERENCES project_events(id),
    location_id BIGINT REFERENCES project_locations(id),
    -- Department evaluation
    dept_eval_completed BOOLEAN DEFAULT FALSE,
    dept_eval_score DOUBLE PRECISION,
    dept_eval_feedback TEXT,
    dept_eval_jury_id BIGINT REFERENCES users(id),
    dept_eval_at TIMESTAMP,
    -- Central evaluation
    central_eval_completed BOOLEAN DEFAULT FALSE,
    central_eval_score DOUBLE PRECISION,
    central_eval_feedback TEXT,
    central_eval_jury_id BIGINT REFERENCES users(id),
    central_eval_at TIMESTAMP,
    -- Audit
    created_by BIGINT REFERENCES users(id),
    updated_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);