package com.school.backend;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import java.io.File;

public class DatabaseConfig {

    private static Jdbi jdbi;

    public static Jdbi getJdbi() {
        if (jdbi == null) {
            init();
        }
        return jdbi;
    }

    private static void init() {
        try {
            File dbDir = new File("data");
            if(dbDir.exists()) {
                dbDir.delete();
            }
            if (!dbDir.exists()) {
                dbDir.mkdirs();
            }

            SQLiteConfig config = new SQLiteConfig();
            config.setJournalMode(SQLiteConfig.JournalMode.WAL);
            config.setSynchronous(SQLiteConfig.SynchronousMode.NORMAL);
            config.enforceForeignKeys(true);

            SQLiteDataSource dataSource = new SQLiteDataSource(config);
            dataSource.setUrl("jdbc:sqlite:data/school_project.db");

            jdbi = Jdbi.create(dataSource);
            jdbi.installPlugin(new SqlObjectPlugin());

            createSchema();

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    private static void createSchema() {
        jdbi.useHandle(handle -> {

            // USERS
            handle.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    email TEXT UNIQUE NOT NULL,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL CHECK (role IN ('ADMIN','MAGAZINER','EMPLOYEE')),
                    photo_path TEXT,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // SUBJECTS
            handle.execute("""
                CREATE TABLE IF NOT EXISTS subjects (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT UNIQUE NOT NULL
                )
            """);

            // TEACHERS
            handle.execute("""
                CREATE TABLE IF NOT EXISTS teachers (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    national_id TEXT UNIQUE NOT NULL,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL,
                    dob DATE,
                    pob TEXT,
                    phone TEXT,
                    family_situation TEXT,
                    work_start_date DATE,
                    photo_path TEXT
                )
            """);

            // TEACHER SUBJECTS
            handle.execute("""
                CREATE TABLE IF NOT EXISTS teacher_subjects (
                    teacher_id INTEGER NOT NULL,
                    subject_id INTEGER NOT NULL,
                    years_taught INTEGER DEFAULT 0,
                    PRIMARY KEY (teacher_id, subject_id),
                    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE,
                    FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
                )
            """);

            // STUDENTS
            handle.execute("""
                CREATE TABLE IF NOT EXISTS students (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL,
                    date_of_birth DATE,
                    place_of_birth TEXT,
                    photo_path TEXT
                )
            """);

            // GROUPS
            handle.execute("""
                CREATE TABLE IF NOT EXISTS groups (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT UNIQUE NOT NULL,
                    level INTEGER NOT NULL CHECK (level BETWEEN 1 AND 4)
                )
            """);

            // STUDENT GROUPS
            handle.execute("""
                CREATE TABLE IF NOT EXISTS student_groups (
                    student_id INTEGER NOT NULL,
                    group_id INTEGER NOT NULL,
                    PRIMARY KEY (student_id, group_id),
                    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
                    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE
                )
            """);

            // ROOMS
            handle.execute("""
                CREATE TABLE IF NOT EXISTS rooms (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    room_number TEXT UNIQUE NOT NULL,
                    type TEXT NOT NULL CHECK (type IN ('CLASS','LAB','SPORT','IT'))
                )
            """);

            // MATERIALS
            handle.execute("""
                CREATE TABLE IF NOT EXISTS materials (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT UNIQUE NOT NULL,
                    model TEXT,
                    status TEXT,
                    quantity INTEGER NOT NULL DEFAULT 0 CHECK (quantity >= 0),
                    photo_path TEXT,
                    category TEXT
                )
            """);

            // ABSENCES
            handle.execute("""
                CREATE TABLE IF NOT EXISTS absences (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    person_id INTEGER NOT NULL,
                    person_type TEXT NOT NULL
                        CHECK (person_type IN ('TEACHER','STUDENT')),
                    absence_date DATE NOT NULL,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    UNIQUE (person_id, person_type, absence_date)
                )
            """);

            // TIMETABLE
            handle.execute("""
                CREATE TABLE IF NOT EXISTS timetable (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    day INTEGER NOT NULL CHECK (day BETWEEN 1 AND 6),
                    hour INTEGER NOT NULL CHECK (hour BETWEEN 1 AND 6),
                    teacher_id INTEGER NOT NULL,
                    group_id INTEGER NOT NULL,
                    room_id INTEGER NOT NULL,
                    FOREIGN KEY (teacher_id) REFERENCES teachers(id),
                    FOREIGN KEY (group_id) REFERENCES groups(id),
                    FOREIGN KEY (room_id) REFERENCES rooms(id),
                    UNIQUE (day, hour, room_id),
                    UNIQUE (day, hour, teacher_id)
                )
            """);

            // MATERIAL REQUESTS
            handle.execute("""
                CREATE TABLE IF NOT EXISTS material_requests (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    teacher_id INTEGER NOT NULL,
                    material_id INTEGER NOT NULL,
                    quantity INTEGER NOT NULL CHECK (quantity > 0),
                    request_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                    status TEXT DEFAULT 'PENDING'
                        CHECK (status IN ('PENDING','APPROVED','REJECTED')),
                    FOREIGN KEY (teacher_id) REFERENCES teachers(id),
                    FOREIGN KEY (material_id) REFERENCES materials(id)
                )
            """);

            // MATERIAL LOGS
            handle.execute("""
                CREATE TABLE IF NOT EXISTS material_logs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    material_id INTEGER NOT NULL,
                    action TEXT NOT NULL CHECK (action IN ('ADD','REMOVE','UPDATE')),
                    quantity_change INTEGER NOT NULL,
                    quantity_before INTEGER NOT NULL,
                    quantity_after INTEGER NOT NULL,
                    user_id INTEGER NOT NULL,
                    log_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (material_id) REFERENCES materials(id) ON DELETE CASCADE,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);

            // DEFAULT ADMIN
            Integer count = handle.createQuery("SELECT COUNT(*) FROM users")
                    .mapTo(Integer.class)
                    .one();

            if (count == 0) {
                handle.execute("""
                  INSERT INTO users (email, first_name, last_name, password, role)
                          VALUES ('admin@school.com', 'Admin', 'System', 'admin123', 'ADMIN');       
                """);
                handle.execute("""
                  INSERT INTO users (email, first_name, last_name, password, role)
                              VALUES ('sameone@school.com', 'magazine', 'System', 'magazine', 'MAGAZINER');
                        """);

            }
        });
    }
}
