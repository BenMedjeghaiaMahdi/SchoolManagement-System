package com.school.backend.dao;

import com.school.backend.model.Absence;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.time.LocalDate;
import java.util.List;

@RegisterBeanMapper(Absence.class)
public interface AbsenceDao {

    /* ========== INSERT ABSENCE ========== */
    @SqlUpdate("INSERT INTO absences (person_id, person_type, absence_date) VALUES (:personId, :personType, :absenceDate)")
    void recordAbsence(@BindBean Absence absence);

    /* ========== JUSTIFY ABSENCE ========== */
    @SqlUpdate(" UPDATE absences SET is_explanation = 1, explanation_date = CURRENT_TIMESTAMP WHERE id = :id")
    int justifyAbsence(@Bind("id") int id);

    /* ========== SELECT ========== */
    @SqlQuery(" SELECT * FROM absences WHERE person_id = :personId AND person_type = :personType ORDER BY absence_date DESC")
    List<Absence> findByPerson(@Bind("personId") int personId, @Bind("personType") String personType);
    /* ========== COUNT ========== */
    @SqlQuery(" SELECT COUNT(*) FROM absences WHERE person_id = :personId AND person_type = :personType")
    int countAbsences(@Bind("personId") int personId, @Bind("personType") String personType);

    @SqlQuery(" SELECT COUNT(*) FROM absences WHERE person_id = :personId AND person_type = :personType AND is_explanation = 0")
    int countAbsencesWithoutExplanation(@Bind("personId") int personId, @Bind("personType") String personType);
}
