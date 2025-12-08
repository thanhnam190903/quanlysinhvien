package com.example.QuanLySinhVien.repository;

import com.example.QuanLySinhVien.entity.Cycle;
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
}
