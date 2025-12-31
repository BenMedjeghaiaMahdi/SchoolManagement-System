package com.school.backend.dao;

import com.school.backend.model.Subject;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

@RegisterBeanMapper(Subject.class)
public interface SubjectDao {

    @SqlUpdate("INSERT INTO subjects (name) VALUES (:name)")
    void insert(@BindBean Subject subject);

    @SqlUpdate(" UPDATE subjects SET name = :name WHERE id = :id ")
    void update(@BindBean Subject subject);

    @SqlUpdate("DELETE FROM subjects WHERE id = :id")
    void deleteById(@Bind int id);

    @SqlQuery(" SELECT * FROM subjects ORDER BY name")
    List<Subject> listAll();

    @SqlQuery(" SELECT * FROM subjects WHERE id = :id")
    Optional<Subject> findById(@Bind int id);

    @SqlQuery(" SELECT * FROM subjects WHERE name = :name")
    Optional<Subject> findByName(@Bind String name);
}
