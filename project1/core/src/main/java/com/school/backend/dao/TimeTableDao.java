package com.school.backend.dao;

import com.school.backend.model.TimeTable;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;

import java.util.List;

@RegisterBeanMapper(TimeTable.class)
public interface TimeTableDao {

    /* ========== CREATE ========== */
    @SqlUpdate("""
        INSERT INTO timetable (day, hour, teacher_id, group_id, room_id)
        VALUES (:day, :hour, :teacherId, :groupId, :roomId)
    """)
    @GetGeneratedKeys
    int insert(@BindBean TimeTable timeTable);

    /* ========== READ ========== */
    @SqlQuery("SELECT * FROM timetable")
    List<TimeTable> findAll();

    @SqlQuery("""
        SELECT * FROM timetable
        WHERE day = :day
        ORDER BY hour
    """)
    List<TimeTable> findByDay(@Bind("day") int day);

    @SqlQuery("""
        SELECT * FROM timetable
        WHERE teacher_id = :teacherId
        ORDER BY day, hour
    """)
    List<TimeTable> findByTeacher(@Bind("teacherId") int teacherId);

    @SqlQuery("""
        SELECT * FROM timetable
        WHERE group_id = :groupId
        ORDER BY day, hour
    """)
    List<TimeTable> findByGroup(@Bind("groupId") int groupId);

    @SqlQuery("""
        SELECT * FROM timetable
        WHERE room_id = :roomId
        ORDER BY day, hour
    """)
    List<TimeTable> findByRoom(@Bind("roomId") int roomId);

    /* ========== UPDATE ========== */
    @SqlUpdate("""
        UPDATE timetable
        SET teacher_id = :teacherId,
            group_id = :groupId,
            room_id = :roomId
        WHERE id = :id
    """)
    int update(@BindBean TimeTable timeTable);

    /* ========== DELETE ========== */
    @SqlUpdate("DELETE FROM timetable WHERE id = :id")
    int delete(@Bind("id") int id);

    /* ========== CHECK CONFLICT ========== */
    @SqlQuery("""
        SELECT COUNT(*)
        FROM timetable
        WHERE day = :day
          AND hour = :hour
          AND room_id = :roomId
    """)
    int countRoomConflicts(
            @Bind("day") int day,
            @Bind("hour") int hour,
            @Bind("roomId") int roomId
    );
}
