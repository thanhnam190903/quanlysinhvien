package com.example.QuanLySinhVien.repository;

import com.example.QuanLySinhVien.entity.UserByDeptStatistics;
import com.example.QuanLySinhVien.entity.UserRoleStatistics;
import com.example.QuanLySinhVien.entity.User;
import com.example.QuanLySinhVien.entity.UserStatusStatistics;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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


    @Query(value = """
        SELECT 
            CASE r.name
                WHEN 'ROLE_staff'   THEN 'PDT'
                WHEN 'ROLE_teacher' THEN 'Giảng viên'
                WHEN 'ROLE_student' THEN 'Sinh viên'
                WHEN 'ROLE_admin'   THEN 'QTHT'
                ELSE r.name
            END AS roleName,
            COUNT(DISTINCT ur.user_id) AS totalUser
        FROM roles r
        JOIN users_roles ur ON r.id = ur.role_id
        GROUP BY r.name
        """,
            nativeQuery = true)
    List<UserRoleStatistics> getAllUserRole();


    @Query(value = """
            SELECT
               SUM(CASE WHEN u.status = b'0' THEN 1 ELSE 0 END) AS active,
               ROUND(
                   SUM(CASE WHEN u.status = b'0' THEN 1 ELSE 0 END) * 100.0 / COUNT(*),
                   2
               ) AS activePct,
               SUM(CASE WHEN u.status = b'1' THEN 1 ELSE 0 END) AS inactive,
               ROUND(
                   SUM(CASE WHEN u.status = b'1' THEN 1 ELSE 0 END) * 100.0 / COUNT(*),
                   2
               ) AS inactivePct,
               SUM(CASE WHEN u.create_at >= CURDATE() - INTERVAL 30 DAY THEN 1 ELSE 0 END) AS newUser,
               ROUND(
                   SUM(CASE WHEN u.create_at >= CURDATE() - INTERVAL 30 DAY THEN 1 ELSE 0 END) * 100.0 / COUNT(*),
                   2
               ) AS newUserPct
           
           FROM users u;
        """,
            nativeQuery = true)
    List<UserStatusStatistics> getUserStatus();

    @Query(value = """
            WITH all_students AS (
                SELECT DISTINCT ur.user_id AS student_id
                FROM users_roles ur
                JOIN roles r ON ur.role_id = r.id
                WHERE r.name = 'ROLE_student'
            ),
            sv_by_dept AS (
                SELECT d.id,
                       d.name AS departmentName,
                       COUNT(DISTINCT cs.student_id) AS countStudent
                FROM departments d
                JOIN classes c       ON c.department_id = d.id
                JOIN class_student cs ON cs.class_id = c.id
                JOIN all_students s   ON s.student_id = cs.student_id
                GROUP BY d.id, d.name
            )
            SELECT 
                departmentName,
                countStudent,
                ROUND(countStudent * 100.0 / (SELECT COUNT(*) FROM all_students), 2) AS percent
            FROM sv_by_dept
            ORDER BY countStudent DESC;
        """,
            nativeQuery = true)
    List<UserByDeptStatistics> getUserByDept();


}
