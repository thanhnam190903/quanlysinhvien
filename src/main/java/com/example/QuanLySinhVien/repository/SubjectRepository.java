package com.example.QuanLySinhVien.repository;

import com.example.QuanLySinhVien.entity.Subject;
import com.example.QuanLySinhVien.entity.SubjectByStudentStatistics;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject,Integer> {
    @Query("SELECT s FROM Subject s WHERE s.deleted = false")
    List<Subject> getAllSubject();

    @Modifying
    @Transactional
    @Query("update Subject s set s.deleted = true where s.id = :id")
    int deleteSubject(@Param("id") int id);

    @Query(" SELECT s FROM Subject s JOIN s.user t JOIN Clazz c ON c.teacher = t " +
            " JOIN c.department d WHERE (:departmentId IS NULL OR d.id = :departmentId) " +
            " AND (:cycleId IS NULL OR s.cycle.id = :cycleId) AND s.deleted = false ")
    Page<Subject> findSubjectsByDepartmentAndCycle(@Param("departmentId") Integer departmentId,
                                                   @Param("cycleId") Integer cycleId, Pageable pageable);

    @Query("SELECT s FROM Subject s " +
            "LEFT JOIN s.cycle cy " +
            "LEFT JOIN s.user u " +
            "WHERE s.deleted = false " +
            "AND (:keyword IS NULL OR :keyword = '' " +
            "OR s.name LIKE %:keyword% " +
            "OR cy.name LIKE %:keyword% " +
            "OR u.name LIKE %:keyword%)")
    Page<Subject> searchSubjects(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = """
          SELECT 
                s.id AS subjectId,
                s.name AS subjectName,
                COUNT(DISTINCT ss.student_id) AS numberStudentsRegistered
            FROM subjects s
            LEFT JOIN score_subjects ss ON ss.subject_id = s.id
            GROUP BY s.id, s.name
            ORDER BY numberStudentsRegistered DESC;
            
        """, nativeQuery = true)
    List<SubjectByStudentStatistics> getSubjectsByStudent();
}
