package com.example.QuanLySinhVien.repository;

import com.example.QuanLySinhVien.entity.Cycle;
import com.example.QuanLySinhVien.entity.SemesterStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CycleRepository extends JpaRepository<Cycle,Integer> {
    @Query("SELECT cy FROM Cycle cy WHERE cy.deleted = false")
    List<Cycle> getAllCycle();

    @Query("SELECT cy FROM Cycle cy WHERE cy.deleted = false " +
            "AND (:keyword IS NULL OR :keyword = '' OR cy.name LIKE %:keyword%)")
    Page<Cycle> searchCycles(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT DISTINCT c FROM Cycle c " +
            "JOIN c.subjects s " +
            "JOIN s.scoreSubjects ss " +
            "WHERE ss.student.id = :studentId " +
            "AND c.deleted = false " +
            "ORDER BY c.startDate DESC")
    List<Cycle> findCyclesByStudent(@Param("studentId") String studentId);

    @Query(value = """
          SELECT
              c.id AS cycleId,
              c.name AS semester,
              COUNT(DISTINCT s.id) AS classOfSchoolTime,
              COUNT(DISTINCT ss.student_id) AS numberOfStudentsRegistered
          FROM cycles c
          LEFT JOIN subjects s       ON s.cycle_id = c.id
          LEFT JOIN score_subjects ss ON ss.subject_id = s.id
          GROUP BY c.id, c.name
          ORDER BY c.id;
        """, nativeQuery = true)
    List<SemesterStatistics> getCycleBySemester();
}
