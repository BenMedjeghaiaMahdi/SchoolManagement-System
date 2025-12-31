package com.school.backend.dao;

import com.school.backend.model.Material;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import java.util.List;

@RegisterBeanMapper(Material.class)
public interface MaterialDao {
    @SqlUpdate("INSERT INTO materials (name, quantity) VALUES (:name, :quantity)")
    void insert(@BindBean Material material);

    @SqlQuery("SELECT * FROM materials")
    List<Material> listAll();

    @SqlUpdate("UPDATE materials SET quantity = :quantity WHERE id = :id")
    void updateQuantity(@Bind("id") int id, @Bind("quantity") int quantity);

    @SqlQuery("SELECT quantity FROM materials WHERE id = :id")
    int getQuantity(@Bind("id") int id);

    @SqlUpdate("DELETE FROM materials WHERE id = :id")
    void deleteById(@Bind("id") int id);
}