package com.school.backend.dao;

import com.school.backend.model.Teacher;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

@RegisterBeanMapper(Teacher.class)
public interface TeacherDao {

    /* ========== INSERT TEACHER ==========
       Adds a new teacher to the database */
    @SqlUpdate("""
        INSERT INTO teachers 
        (national_id, first_name, last_name, phone, dob, pob, family_situation, work_start_date, photo_path) 
        VALUES 
        (:nationalId, :firstName, :lastName, :phone, :dob, :pob, :familySituation, :workStartDate, :photoPath)
    """)
    void insert(@BindBean Teacher teacher);

    /* ========== LIST ALL TEACHERS ==========
       Returns a list of all teachers sorted by last name */
    @SqlQuery("SELECT * FROM teachers ORDER BY last_name")
    List<Teacher> listAll();

    /* ========== FIND TEACHERS BY SUBJECT ==========
       Returns teachers who teach a specific subject */
    @SqlQuery("""
        SELECT t.* 
        FROM teachers t
        JOIN teacher_subjects ts ON t.id = ts.teacher_id
        JOIN subjects s ON ts.subject_id = s.id
        WHERE s.name = :subjectName
    """)
    List<Teacher> findBySubject(@Bind("subjectName") String subjectName);

    /* ========== DELETE TEACHER ==========
       Deletes a teacher by ID */
    @SqlUpdate("DELETE FROM teachers WHERE id = :id")
    void deleteById(@Bind("id") int id);

    /* ========== ADD SUBJECT TO TEACHER ==========
       Links a teacher to a subject in teacher_subjects table */
    @SqlUpdate("""
        INSERT INTO teacher_subjects (teacher_id, subject_id, years_taught) 
        VALUES (:teacherId, :subjectId, :yearsTaught)
        ON CONFLICT(teacher_id, subject_id) DO NOTHING
    """)
    void addSubject(@Bind("teacherId") int teacherId, @Bind("subjectId") int subjectId, @Bind("yearsTaught") int yearsTaught);

    /* ========== REMOVE SUBJECT FROM TEACHER ==========
       Unlink a teacher from a subject */
    @SqlUpdate("DELETE FROM teacher_subjects WHERE teacher_id = :teacherId AND subject_id = :subjectId")
    void removeSubject(@Bind("teacherId") int teacherId, @Bind("subjectId") int subjectId);
    @SqlUpdate("UPDATE teachers SET photo_path = :path WHERE id = :id")
    void updatePhotoPath(@Bind("id") int id, @Bind("path") String path);
}
