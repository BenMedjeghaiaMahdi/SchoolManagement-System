package com.school.backend.service;

import com.school.backend.DatabaseConfig;
import com.school.backend.dao.*;
import com.school.backend.model.*;
import org.jdbi.v3.core.Jdbi;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class SchoolService {

    private static SchoolService instance;
    private final Jdbi jdbi;
    private User currentUser;

    private SchoolService() {
        this.jdbi = DatabaseConfig.getJdbi();
    }

    public static SchoolService getInstance() {
        if (instance == null) instance = new SchoolService();
        return instance;
    }

    /* ================= AUTH ================= */

    public boolean login(String email, String password) {
        Optional<User> user = jdbi.onDemand(UserDao.class).login(email, password);
        user.ifPresent(u -> this.currentUser = u);
        return user.isPresent();
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    /* ================= TEACHERS ================= */

    public List<Teacher> getAllTeachers() {
        checkReadPermission("TEACHERS");
        return jdbi.onDemand(TeacherDao.class).listAll();
    }

    public List<Teacher> getTeachersBySubject(String subjectName) {
        checkReadPermission("TEACHERS");
        return jdbi.onDemand(TeacherDao.class).findBySubject(subjectName);
    }

    public void addTeacher(Teacher teacher) {
        checkWritePermission();
        jdbi.onDemand(TeacherDao.class).insert(teacher);
    }

    public void deleteTeacher(int id) {
        checkWritePermission();
        jdbi.onDemand(TeacherDao.class).deleteById(id);
    }

    /* ================= SUBJECTS ================= */

    public List<Subject> getAllSubjects() {
        checkReadPermission("TEACHERS");
        return jdbi.onDemand(SubjectDao.class).listAll();
    }

    public void addSubject(Subject subject) {
        checkWritePermission();
        jdbi.onDemand(SubjectDao.class).insert(subject);
    }

    public void assignSubjectToTeacher(int teacherId, int subjectId, int yearsTaught) {
        checkWritePermission();
        jdbi.onDemand(TeacherSubjectDao.class)
                .assignSubject(teacherId, subjectId, yearsTaught);
    }

    /* ================= STUDENTS ================= */

    public List<Student> getAllStudents() {
        checkReadPermission("STUDENTS");
        return jdbi.onDemand(StudentDao.class).listAll();
    }

    public void addStudent(Student student) {
        checkWritePermission();
        jdbi.onDemand(StudentDao.class).insert(student);
    }

    public Double getStudentAverage(int studentId, int year) {
        checkReadPermission("STUDENTS");
        return jdbi.onDemand(StudentDao.class).getGradeForYear(studentId, year);
    }

    /* ================= ROOMS ================= */

    public List<Room> getAllRooms() {
        checkReadPermission("ROOMS");
        return jdbi.onDemand(RoomDao.class).listAll();
    }

    public void addRoom(Room room) {
        checkWritePermission();
        jdbi.onDemand(RoomDao.class).insert(room);
    }

    public void deleteRoom(int id) {
        checkWritePermission();
        jdbi.onDemand(RoomDao.class).deleteById(id);
    }

    /* ================= MATERIALS ================= */

    public List<Material> getAllMaterials() {
        if (currentUser == null) throw new SecurityException("Not logged in");
        return jdbi.onDemand(MaterialDao.class).listAll();
    }

    public void addMaterial(Material material) {
        checkWritePermission();
        jdbi.onDemand(MaterialDao.class).insert(material);
    }

    public void updateMaterialQuantity(int materialId, int newQty) {
        checkWritePermission();

        MaterialDao materialDao = jdbi.onDemand(MaterialDao.class);
        MaterialLogDao logDao = jdbi.onDemand(MaterialLogDao.class);

        int before = materialDao.getQuantity(materialId);
        materialDao.updateQuantity(materialId, newQty);

        logDao.insert(new MaterialLog(
                materialId,
                "UPDATE",
                newQty - before,
                before,
                newQty,
                currentUser.getId()
        ));
    }

    /* ================= ABSENCES ================= */

    public void markAbsence(int personId, String personType, LocalDate date) {
        checkWritePermission();
        Absence absence = new Absence();
        absence.setPersonId(personId);
        absence.setDate(date);
        absence.setExplanation(false);
        jdbi.onDemand(AbsenceDao.class)
                .recordAbsence(absence);
    }

    public int getAbsenceCount(int personId, String personType) {
        checkReadPermission("ATTENDANCE");
        return jdbi.onDemand(AbsenceDao.class)
                .countAbsences(personId, personType);
    }

    /* ================= SECURITY ================= */

    private void checkWritePermission() {
        if (currentUser == null)
            throw new SecurityException("Not logged in");

        String role = currentUser.getRole();
        if ("MANAGER".equals(role) || "ECONOME".equals(role)) {
            throw new SecurityException("Access Denied: Write operation not allowed");
        }
    }

    private void checkReadPermission(String module) {
        if (currentUser == null)
            throw new SecurityException("Not logged in");

        if ("ECONOME".equals(currentUser.getRole())
                && !"MATERIALS".equals(module)) {
            throw new SecurityException("Access Denied: Econome can access materials only");
        }
    }
}
