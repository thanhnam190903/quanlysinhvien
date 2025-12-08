package com.example.QuanLySinhVien.repository;

import com.example.QuanLySinhVien.entity.Clazz;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClassRepository extends JpaRepository<Clazz,Integer> {
    @Query("SELECT c FROM Clazz c WHERE c.department.id = :depId AND c.deleted = false")
    List<Clazz> getAllByDepartment(@Param("depId") int depId);

    @Query("SELECT c FROM Clazz c " +
            "LEFT JOIN c.teacher u " +
            "WHERE c.department.id = :depId AND c.deleted = false " +
            "AND (:keyword IS NULL OR :keyword = '' " +
            "OR c.name LIKE %:keyword% " +
            "OR u.name LIKE %:keyword%)")
    Page<Clazz> searchClassesByDepartment(@Param("depId") int depId, @Param("keyword") String keyword, Pageable pageable);

    @Modifying
    @Transactional
    @Query("update Clazz c set c.deleted = true where c.id = :id")
    int deleteclass(@Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO class_student(student_id, class_id) VALUES (:studentId, :classId)", nativeQuery = true)
    void addStudentToClass(@Param("studentId") String studentId, @Param("classId") int classId);

    List<Clazz> findByTeacher_Id(String teacherId);
    @Query("SELECT c FROM Clazz c " +
            "WHERE c.teacher.id = :teacherId AND c.deleted = false " +
            "AND (:keyword IS NULL OR :keyword = '' OR c.name LIKE %:keyword%)")
    Page<Clazz> searchClassesByTeacherId(@Param("teacherId") String teacherId, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM Clazz c JOIN c.students s WHERE s.id = :studentId")
    Clazz findClassByStudentId(@Param("studentId") String studentId);

}
