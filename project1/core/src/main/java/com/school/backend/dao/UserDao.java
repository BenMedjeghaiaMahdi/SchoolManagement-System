package com.school.backend.dao;

import com.school.backend.model.User;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import java.util.Optional;

@RegisterBeanMapper(User.class)
public interface UserDao {
    @SqlQuery("SELECT * FROM users WHERE email = :email AND password = :password")
    Optional<User> login(@Bind("email") String email, @Bind("password") String password);
}