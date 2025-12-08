package com.example.QuanLySinhVien.repository;

import com.example.QuanLySinhVien.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface UserRepository extends JpaRepository<User,String> {

    @Query("SELECT u FROM User u JOIN u.roles r " +
            "WHERE r.name = 'ROLE_teacher' AND u.deleted = false " +
            "AND (:keyword IS NULL OR :keyword = '' OR u.id LIKE %:keyword% OR u.name LIKE %:keyword%)")
    Page<User> searchTeachers(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT u FROM User u JOIN u.roles r " +
            "WHERE r.name = 'ROLE_student' AND u.deleted = false " +
            "AND (:keyword IS NULL OR :keyword = '' OR u.id LIKE %:keyword% OR u.name LIKE %:keyword%)")
    Page<User> searchStudent(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT u FROM User u JOIN u.roles r " +
            "WHERE r.name = 'ROLE_staff' AND u.deleted = false " +
            "AND (:keyword IS NULL OR :keyword = '' OR u.id LIKE %:keyword% OR u.name LIKE %:keyword%)")
    Page<User> searchStaff(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE " +
            " r.name = 'ROLE_teacher' AND u.deleted = false")
    List<User> getAllTeacher();


    @Modifying
    @Transactional
    @Query("update User u set u.deleted = false where u.id = :id")
    int deleteUser(@Param("id") String id);

    @Query(" SELECT u FROM User u JOIN u.classes c JOIN u.roles r " +
            "WHERE c.id = :classId AND r.name = 'ROLE_student'")
    List<User> findStudentsByClassId(@Param("classId") int classId);

    @Query("SELECT u FROM User u JOIN u.classes c JOIN u.roles r " +
            "WHERE c.id = :classId AND r.name = 'ROLE_student' AND u.deleted = false " +
            "AND (:keyword IS NULL OR :keyword = '' " +
            "OR u.id LIKE %:keyword% " +
            "OR u.name LIKE %:keyword%)")
    Page<User> searchStudentsByClassId(@Param("classId") int classId, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'ROLE_student' AND (u.classes IS EMPTY)")
    List<User> findAllStudentsNotInAnyClass();

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = 'ROLE_student' " +
            "AND u.classes IS EMPTY")
    long countStudentsNotInAnyClass();

    @Query("SELECT u FROM User u JOIN u.roles r " +
            "WHERE r.name = 'ROLE_student' AND u.classes IS EMPTY AND " +
            "(LOWER(u.name) LIKE LOWER(:keyword) OR LOWER(u.id) LIKE LOWER(:keyword) OR LOWER(u.email) LIKE LOWER(:keyword))")
    List<User> findStudentsNotInClassByKeyword(@Param("keyword") String keyword);

}
