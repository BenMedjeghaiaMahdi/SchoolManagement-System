package com.school.backend.dao;

import com.school.backend.model.Room;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import java.util.List;

@RegisterBeanMapper(Room.class)
public interface RoomDao {
    @SqlUpdate("INSERT INTO rooms (room_number, type) VALUES (:roomNumber, :type)")
    void insert(@BindBean Room room);

    @SqlQuery("SELECT * FROM rooms")
    List<Room> listAll();

    @SqlUpdate("DELETE FROM rooms WHERE id = :id")
    void deleteById(@Bind("id") int id);
}