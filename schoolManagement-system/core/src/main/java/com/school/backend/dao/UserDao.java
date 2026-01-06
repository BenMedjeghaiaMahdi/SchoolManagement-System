package com.school.backend.dao;

import com.school.backend.model.User;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

@RegisterBeanMapper(User.class)
public interface UserDao {
    @SqlQuery("SELECT * FROM users WHERE email = :email AND password = :password")
    Optional<User> login(@Bind("email") String email, @Bind("password") String password);
    @SqlUpdate("INSERT INTO users (email, password, first_name, last_name, grade, phone, role) VALUES (:email, :password, :firstName, :lastName, :grade, :phone, :role)")
    void addUser(@BindBean User user);
    @SqlUpdate("DELETE FROM users WHERE id = :id")
    void deleteUser(@Bind("id") int id);
    @SqlUpdate("UPDATE users SET email = :email, password = :password, first_name = :firstName, last_name = :lastName, grade = :grade, phone = :phone, role = :role WHERE id = :id")
    void updateUser(@BindBean User user);
    @SqlQuery("SELECT * FROM users WHERE id = :id")
    Optional<User> findById(@Bind("id") int id);
    @SqlQuery("SELECT * FROM users")
    List<User> listAll();
    @SqlUpdate("UPDATE users SET photo_path = :path WHERE id = :id")
    void updatePhotoPath(@Bind("id") int id, @Bind("path") String path);


}