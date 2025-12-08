package com.example.QuanLySinhVien.repository;

import com.example.QuanLySinhVien.dto.ScoreSubjectProjection;
import com.example.QuanLySinhVien.entity.ScoreSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScoreSubjectRepository extends JpaRepository<ScoreSubject,Integer> {
    @Query(value = " SELECT DISTINCT " +
            " st.id AS student_id, st.name AS student_name, " +
            " sb.id AS subject_id, sb.name AS subject_name, " +
            " sb.start_date AS subject_start_date, sb.end_date AS subject_end_date, " +
            " COALESCE(ss.score_process, 0) AS score_process, " +
            " COALESCE(ss.score_final, 0) AS score_final, " +
            " COALESCE(ss.total_score, 0) AS total_score, " +
            " ss.id AS score_subject_id " +
            " FROM class_student cs " +
            " JOIN users st ON st.id = cs.student_id " +
            " JOIN classes c ON c.id = cs.class_id " +
            " JOIN subjects sb ON sb.teacher_id = c.teacher_id " +
            " LEFT JOIN score_subjects ss ON ss.student_id = st.id AND ss.subject_id = sb.id " +
            " WHERE st.id = ?1 AND c.teacher_id = ?2 " +
            " AND st.deleted = 0 AND c.deleted = 0 AND sb.deleted = 0 ",
            nativeQuery = true)
    List<ScoreSubjectProjection> getScoreSubjectRaw(String studentId, String teacherId);

    @Query("SELECT ss FROM ScoreSubject ss " +
            "JOIN FETCH ss.subject s " +
            "JOIN FETCH s.cycle c " +
            "WHERE ss.student.id = :studentId " +
            "AND c.id = :cycleId " +
            "AND ss.deleted = false " +
            "ORDER BY ss.id")
    List<ScoreSubject> findByStudentAndCycle(@Param("studentId") String studentId,
                                             @Param("cycleId") int cycleId);

    @Query("SELECT ss FROM ScoreSubject ss " +
            "JOIN FETCH ss.subject s " +
            "JOIN FETCH s.cycle c " +
            "WHERE ss.student.id = :studentId " +
            "AND ss.deleted = false " +
            "ORDER BY c.startDate DESC, ss.id")
    List<ScoreSubject> findByStudentId(@Param("studentId") String studentId);

    @Query("SELECT AVG(ss.totalScore) FROM ScoreSubject ss " +
            "WHERE ss.student.id = :studentId " +
            "AND ss.subject.cycle.id = :cycleId " +
            "AND ss.deleted = false")
    Double getAverageScoreByCycle(@Param("studentId") String studentId,
                                  @Param("cycleId") int cycleId);

    @Query("SELECT COUNT(ss) FROM ScoreSubject ss " +
            "WHERE ss.student.id = :studentId " +
            "AND ss.subject.cycle.id = :cycleId " +
            "AND ss.deleted = false")
    Long countByCycle(@Param("studentId") String studentId,
                      @Param("cycleId") int cycleId);

    @Query("SELECT AVG(ss.totalScore) FROM ScoreSubject ss " +
            "WHERE ss.student.id = :studentId " +
            "AND ss.deleted = false")
    Double getOverallAverage(@Param("studentId") String studentId);

    @Query("SELECT COUNT(ss) FROM ScoreSubject ss " +
            "WHERE ss.student.id = :studentId " +
            "AND ss.deleted = false")
    Long countAllSubjects(@Param("studentId") String studentId);

    @Query("SELECT s FROM ScoreSubject s WHERE s.student.id = :studentId AND s.subject.id = :subjectId AND s.deleted = false")
    ScoreSubject findScoreByStudentAndSubject(@Param("studentId") String studentId, @Param("subjectId") int subjectId);


}
