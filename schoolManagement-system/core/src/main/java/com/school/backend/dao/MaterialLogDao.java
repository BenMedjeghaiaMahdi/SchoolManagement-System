package com.school.backend.dao;

import com.school.backend.model.MaterialLog;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

@RegisterBeanMapper(MaterialLog.class)
public interface MaterialLogDao {

    @SqlUpdate("""
        INSERT INTO material_logs (
            material_id,
            action,
            quantity_change,
            quantity_before,
            quantity_after,
            user_id
        )
        VALUES (
            :materialId,
            :action,
            :quantityChange,
            :quantityBefore,
            :quantityAfter,
            :userId
        )
    """)
    void insert(@BindBean MaterialLog log);

    @SqlQuery("""
        SELECT *
        FROM material_logs
        WHERE material_id = :materialId
        ORDER BY log_date DESC
    """)
    List<MaterialLog> findByMaterial(@Bind("materialId") int materialId);

    @SqlQuery("""
        SELECT *
        FROM material_logs
        ORDER BY log_date DESC
    """)
    List<MaterialLog> listAll();

    @SqlQuery("""
        SELECT *
        FROM material_logs
        WHERE user_id = :userId
        ORDER BY log_date DESC
    """)
    List<MaterialLog> findByUser(@Bind("userId") int userId);
}