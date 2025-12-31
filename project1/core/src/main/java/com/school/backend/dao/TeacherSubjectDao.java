package com.school.backend.dao;

import com.school.backend.model.Subject;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface TeacherSubjectDao {

    @SqlUpdate("INSERT INTO teacher_subjects (teacher_id, subject_id, years_taught) VALUES (:teacherId, :subjectId, :yearsTaught)")
    void assignSubject(
            @Bind("teacherId") int teacherId,
            @Bind("subjectId") int subjectId,
            @Bind("yearsTaught") int yearsTaught
    );

    @SqlUpdate("""
        DELETE FROM teacher_subjects
        WHERE teacher_id = :teacherId
          AND subject_id = :subjectId
    """)
    void removeSubject(
            @Bind("teacherId") int teacherId,
            @Bind("subjectId") int subjectId
    );

    @SqlQuery("""
        SELECT s.*
        FROM subjects s
        JOIN teacher_subjects ts ON ts.subject_id = s.id
        WHERE ts.teacher_id = :teacherId
    """)
    @RegisterBeanMapper(Subject.class)
    List<Subject> findSubjectsByTeacher(@Bind("teacherId") int teacherId);

    @SqlQuery("""
        SELECT t.id
        FROM teachers t
        JOIN teacher_subjects ts ON ts.teacher_id = t.id
        WHERE ts.subject_id = :subjectId
    """)
    List<Integer> findTeachersBySubject(@Bind("subjectId") int subjectId);
}
