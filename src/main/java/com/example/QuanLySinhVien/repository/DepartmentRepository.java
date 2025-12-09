package com.example.QuanLySinhVien.repository;

import com.example.QuanLySinhVien.entity.Department;
import com.example.QuanLySinhVien.entity.DepartmentMediumStatistics;
import com.example.QuanLySinhVien.entity.UserAllocationStatistics;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department,Integer> {
    @Modifying
    @Transactional
    @Query("update Department d set d.deleted = false where d.id = :id")
    int deleteDepartment(@Param("id") int id);

    @Query(" SELECT c.department FROM Clazz c JOIN c.students s WHERE s.id = :studentId ")
    Department findDepartmentByStudentId(@Param("studentId") String studentId);

    @Query("SELECT d FROM Department d WHERE d.deleted = false")
    List<Department> findAllDepartment();

    @Query("SELECT d FROM Department d WHERE d.deleted = false " +
            "AND (:keyword IS NULL OR :keyword = '' OR d.name LIKE %:keyword%)")
    Page<Department> searchDepartments(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = """
          SELECT
              (SELECT COUNT(*) FROM departments) AS countDepartments,
              (SELECT COUNT(*) FROM subjects s WHERE s.status = b'1') AS countSubjects,
              (SELECT COUNT(*) FROM classes c WHERE c.status = b'1')  AS countClasses;
        """,
            nativeQuery = true)
    List<UserAllocationStatistics> getUserAllocation();

    @Query(value = """
          SELECT 
                  d.name AS departmentName,
                  COUNT(DISTINCT cs.student_id) AS studentNumber,
                  AVG(ss.total_score) AS pointMedium
              FROM departments d
              JOIN classes c 
                  ON c.department_id = d.id
              JOIN class_student cs 
                  ON cs.class_id = c.id
              LEFT JOIN score_subjects ss 
                  ON ss.student_id = cs.student_id
              GROUP BY d.name
              ORDER BY d.name;
              
        """,
            nativeQuery = true)
    List<DepartmentMediumStatistics> getDepartmentsMedium();

}
