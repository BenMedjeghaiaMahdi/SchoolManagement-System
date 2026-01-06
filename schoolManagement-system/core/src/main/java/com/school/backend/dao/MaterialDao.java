package com.school.backend.dao;

import com.school.backend.model.Material;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

@RegisterBeanMapper(Material.class)
public interface MaterialDao {

    @SqlUpdate("INSERT INTO materials (name, quantity, model, category, status, photo_path) " +
            "VALUES (:name, :quantity, :model, :category, :status, :photoPath)")
    @GetGeneratedKeys("id")
    int insert(@BindBean Material material);

    @SqlQuery("SELECT * FROM materials ORDER BY name ASC")
    List<Material> listAll();

    @SqlQuery("SELECT * FROM materials WHERE id = :id")
    Material findById(@Bind("id") int id);

    @SqlUpdate("UPDATE materials SET name = :name, quantity = :quantity, " +
            "model = :model, category = :category, status = :status, photo_path = :photoPath " +
            "WHERE id = :id")
    void update(@BindBean Material material);

    @SqlQuery("SELECT quantity FROM materials WHERE id = :id")
    int getQuantity(@Bind("id") int id);

    @SqlUpdate("UPDATE materials SET quantity = :quantity WHERE id = :id")
    void updateQuantity(@Bind("id") int id, @Bind("quantity") int quantity);

    @SqlUpdate("UPDATE materials SET photo_path = :path WHERE id = :id")
    void updatePhotoPath(@Bind("id") int id, @Bind("path") String path);

    @SqlUpdate("DELETE FROM materials WHERE id = :id")
    void deleteById(@Bind("id") int id);

    @SqlQuery("SELECT * FROM materials WHERE category = :category ORDER BY name ASC")
    List<Material> findByCategory(@Bind("category") String category);

    @SqlQuery("SELECT * FROM materials WHERE status = :status ORDER BY name ASC")
    List<Material> findByStatus(@Bind("status") String status);

    @SqlQuery("SELECT * FROM materials WHERE LOWER(name) LIKE '%' || LOWER(:searchText) || '%' " +
            "ORDER BY name ASC")
    List<Material> searchByName(@Bind("searchText") String searchText);

    @SqlUpdate("UPDATE materials SET status = :status WHERE id = :id")
    void updateStatus(@Bind("id") int id, @Bind("status") String status);

    @SqlQuery("SELECT COUNT(*) FROM materials")
    int countAll();

    @SqlQuery("SELECT COUNT(*) FROM materials WHERE category = :category")
    int countByCategory(@Bind("category") String category);

    @SqlQuery("SELECT COALESCE(SUM(quantity), 0) FROM materials WHERE category = :category")
    int getTotalQuantityByCategory(@Bind("category") String category);

    @SqlQuery("SELECT COALESCE(SUM(quantity), 0) FROM materials")
    int getTotalQuantity();
}