package com.example.QuanLySinhVien.repository;

import com.example.QuanLySinhVien.entity.Department;
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

}
