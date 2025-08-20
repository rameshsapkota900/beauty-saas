-- Create parlours table
CREATE TABLE IF NOT EXISTS parlours (
    parlour_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    logo_url VARCHAR(500),
    contact_info TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create admins table
CREATE TABLE IF NOT EXISTS admins (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    parlour_id UUID NOT NULL REFERENCES parlours(parlour_id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create categories table
CREATE TABLE IF NOT EXISTS categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    parlour_id UUID NOT NULL REFERENCES parlours(parlour_id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create services table
CREATE TABLE IF NOT EXISTS services (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    parlour_id UUID NOT NULL REFERENCES parlours(parlour_id) ON DELETE CASCADE,
    category_id UUID NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    image_url VARCHAR(500),
    price DECIMAL(10,2) NOT NULL CHECK (price > 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create courses table
CREATE TABLE IF NOT EXISTS courses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    parlour_id UUID NOT NULL REFERENCES parlours(parlour_id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    image_url VARCHAR(500),
    description TEXT,
    price DECIMAL(10,2) NOT NULL CHECK (price > 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create staffs table
CREATE TABLE IF NOT EXISTS staffs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    parlour_id UUID NOT NULL REFERENCES parlours(parlour_id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    photo VARCHAR(500),
    designation VARCHAR(255) NOT NULL,
    base_salary DECIMAL(10,2) NOT NULL CHECK (base_salary > 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create service_bookings table
CREATE TABLE IF NOT EXISTS service_bookings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    parlour_id UUID NOT NULL REFERENCES parlours(parlour_id) ON DELETE CASCADE,
    service_id UUID NOT NULL REFERENCES services(id) ON DELETE CASCADE,
    client_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'CANCELLED', 'COMPLETED')),
    cancel_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create course_bookings table
CREATE TABLE IF NOT EXISTS course_bookings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    parlour_id UUID NOT NULL REFERENCES parlours(parlour_id) ON DELETE CASCADE,
    course_id UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    client_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'CANCELLED', 'COMPLETED')),
    cancel_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create staff_salary_log table
CREATE TABLE IF NOT EXISTS staff_salary_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    staff_id UUID NOT NULL REFERENCES staffs(id) ON DELETE CASCADE,
    net_salary DECIMAL(10,2) NOT NULL,
    paid_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create staff_advance_pay table
CREATE TABLE IF NOT EXISTS staff_advance_pay (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    staff_id UUID NOT NULL REFERENCES staffs(id) ON DELETE CASCADE,
    amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create successful_students table
CREATE TABLE IF NOT EXISTS successful_students (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    parlour_id UUID NOT NULL REFERENCES parlours(parlour_id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create certificates table
CREATE TABLE IF NOT EXISTS certificates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    parlour_id UUID NOT NULL REFERENCES parlours(parlour_id) ON DELETE CASCADE,
    student_name VARCHAR(255) NOT NULL,
    image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_admins_parlour_id ON admins(parlour_id);
CREATE INDEX IF NOT EXISTS idx_admins_email ON admins(email);
CREATE INDEX IF NOT EXISTS idx_categories_parlour_id ON categories(parlour_id);
CREATE INDEX IF NOT EXISTS idx_services_parlour_id ON services(parlour_id);
CREATE INDEX IF NOT EXISTS idx_services_category_id ON services(category_id);
CREATE INDEX IF NOT EXISTS idx_courses_parlour_id ON courses(parlour_id);
CREATE INDEX IF NOT EXISTS idx_staffs_parlour_id ON staffs(parlour_id);
CREATE INDEX IF NOT EXISTS idx_service_bookings_parlour_id ON service_bookings(parlour_id);
CREATE INDEX IF NOT EXISTS idx_service_bookings_phone ON service_bookings(phone);
CREATE INDEX IF NOT EXISTS idx_course_bookings_parlour_id ON course_bookings(parlour_id);
CREATE INDEX IF NOT EXISTS idx_course_bookings_phone ON course_bookings(phone);
CREATE INDEX IF NOT EXISTS idx_staff_salary_log_staff_id ON staff_salary_log(staff_id);
CREATE INDEX IF NOT EXISTS idx_staff_advance_pay_staff_id ON staff_advance_pay(staff_id);
CREATE INDEX IF NOT EXISTS idx_successful_students_parlour_id ON successful_students(parlour_id);
CREATE INDEX IF NOT EXISTS idx_certificates_parlour_id ON certificates(parlour_id);
