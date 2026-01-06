package com.school.backend.dao;

import com.school.backend.model.Student;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import java.util.List;

@RegisterBeanMapper(Student.class)
public interface StudentDao {
    @SqlUpdate("INSERT INTO students (first_name, last_name, dob, pob, current_class, photo_path) VALUES (:firstName, :lastName, :dob, :pob, :currentClass, :photoPath)")
    void insert(@BindBean Student student);

    @SqlQuery("SELECT * FROM students ORDER BY last_name")
    List<Student> listAll();

    @SqlUpdate("UPDATE students SET current_class = :currentClass WHERE id = :id")
    void updateClass(@Bind("id") int id, @Bind("currentClass") String currentClass);

    @SqlUpdate("DELETE FROM students WHERE id = :id")
    void deleteById(@Bind("id") int id);

    @SqlQuery("SELECT average_score FROM student_grades WHERE student_id = :studentId AND year_level = :year")
    Double getGradeForYear(@Bind("studentId") int studentId, @Bind("year") int year);

    @SqlUpdate("INSERT INTO student_grades (student_id, year_level, average_score) VALUES (:studentId, :year, :score)")
    void addGrade(@Bind("studentId") int studentId, @Bind("year") int year, @Bind("score") double score);
    @SqlUpdate("UPDATE students SET photo_path = :path WHERE id = :id")
    void updatePhotoPath(@Bind("id") int id, @Bind("path") String path);
}