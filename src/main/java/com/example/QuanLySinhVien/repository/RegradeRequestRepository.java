package com.example.QuanLySinhVien.repository;

import com.example.QuanLySinhVien.dto.MediumProcessAndAcceptChangeRateSubjects;
import com.example.QuanLySinhVien.dto.RegradeRequestByDepartmentSubjects;
import com.example.QuanLySinhVien.dto.RegradeRequestSubjects;
import com.example.QuanLySinhVien.dto.RejectAndAcceptSubjects;
import com.example.QuanLySinhVien.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RegradeRequestRepository extends JpaRepository<RegradeRequest,Integer> {
//    @Query("SELECT COUNT(rr) FROM RegradeRequest rr " +
//            "WHERE rr.student.id = :studentId " +
//            "AND rr.subject.cycle.id = :cycleId " +
//            "AND rr.deleted = false")
//    Long countByCycle(@Param("studentId") String studentId,
//                      @Param("cycleId") int cycleId);
    @Query("SELECT COUNT(sc) FROM ScoreSubject sc " +
            "WHERE sc.student.id = :studentId " +
            "AND sc.subject.cycle.id = :cycleId " +
            "AND sc.attempt >= 2 " +
            "AND sc.deleted = false")
    Long countByCycle(@Param("studentId") String studentId,
                                    @Param("cycleId") int cycleId);
    @Query("SELECT COUNT(rr) FROM RegradeRequest rr " +
            "WHERE rr.student.id = :studentId " +
            "AND rr.subject.id = :subjectId " +
            "AND rr.deleted = false")
    Long checkRegradeExists(@Param("studentId") String studentId,
                            @Param("subjectId") int subjectId);

    @Query("SELECT r FROM RegradeRequest r WHERE r.deleted = false AND r.student.id = :id")
    List<RegradeRequest> findByStudentCode(@Param("id") String id);

    @Query(" SELECT r FROM RegradeRequest r WHERE r.createdBy IS NOT NULL " +
            " AND r.deleted = false AND r.status IN ('Chờ xét duyệt', 'Đã duyệt hồ sơ')  " +
            " AND (:studentCode IS NULL OR r.student.id = :studentCode) " +
            " AND (:subjectId IS NULL OR r.subject.name = :subjectId) " +
            " AND (:createdAt IS NULL OR r.createdAt = :createdAt) ")
    Page<RegradeRequest> searchRegrades(
            @Param("studentCode") String studentCode,
            @Param("subjectId") String subjectId,
            @Param("createdAt") LocalDate createdAt,
            Pageable pageable
    );

    @Query("SELECT r FROM RegradeRequest r WHERE r.status IN ('Đã duyệt hồ sơ','Đã cập nhật điểm') " +
            " AND r.subject.user.id = :teacherId " +
            " AND (:studentCode IS NULL OR r.student.id = :studentCode) " +
            " AND (:status IS NULL OR r.status = :status) "  +
            " AND (:createdAt IS NULL OR r.createdAt = :createdAt) ")
    Page<RegradeRequest> findByTeacherId(@Param("teacherId") String teacherId,
                                         @Param("studentCode") String studentCode,
                                         @Param("status") String status,
                                         @Param("createdAt") LocalDate createdAt,
                                         Pageable pageable);

    @Query(value = """
            SELECT
                COUNT(*) AS total,
                SUM(CASE WHEN status IS NULL OR status = 'Chờ xét duyệt' THEN 1 ELSE 0 END) AS waiting,
                SUM(CASE WHEN status IS NOT NULL AND status not in ('Từ chối', 'Đã cập nhật điểm') THEN 1 ELSE 0 END) AS process
            FROM regrade_request;
        """,
            nativeQuery = true)
    List<RegradeRequestSubjects> getRegradeRequest();

    @Query(value = """
           SELECT
                 SUM(CASE WHEN status IN ('Đã cập nhật điểm', 'Đã duyệt hồ sơ') THEN 1 ELSE 0 END) AS accept,
                 SUM(CASE WHEN status = 'Từ chối' THEN 1 ELSE 0 END) AS reject,
                 COUNT(*) AS tong_don,
                 ROUND(SUM(CASE WHEN status IN ('Đã cập nhật điểm', 'Đã duyệt hồ sơ') THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS acceptRate,
                 ROUND(SUM(CASE WHEN status = 'Từ chối' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS rejectRate
             FROM regrade_request;
        """,
            nativeQuery = true)
    List<RejectAndAcceptSubjects> getRegradeRequestRejectAndAccept();

    @Query(value = """
           SELECT
               ROUND(AVG(DATEDIFF(updated_at, request_date)), 1) AS mediumProcess,
               ROUND(
                   SUM(CASE WHEN new_score IS NOT NULL AND old_score IS NOT NULL AND new_score <> old_score THEN 1 ELSE 0 END)
                   * 100.0 / COUNT(*),
                   2
               ) AS acceptChangeRate
           FROM regrade_request
           WHERE updated_at IS NOT NULL;
        """,
            nativeQuery = true)
    List<MediumProcessAndAcceptChangeRateSubjects> getMediumProcessAndAcceptChangeRate();

    @Query(value = """
           SELECT  
               d.name AS departmentName,
               COUNT(r.id) AS total,
               SUM(CASE  
                   WHEN r.status IN ('Đã cập nhật điểm', 'Đã duyệt hồ sơ') THEN 1  
                   ELSE 0  
               END) AS acceptNumber,
               CONCAT(
                   SUM(CASE  
                       WHEN r.status IN ('Đã cập nhật điểm', 'Đã duyệt hồ sơ') THEN 1  
                       ELSE 0  
                   END),
                   '/', COUNT(r.id)
               ) AS acceptRateText,
               ROUND(
                   SUM(CASE  
                       WHEN r.status IN ('Đã cập nhật điểm', 'Đã duyệt hồ sơ') THEN 1  
                       ELSE 0  
                   END) * 100.0 / COUNT(r.id),
                   2
               ) AS acceptRate
           FROM regrade_request r
           JOIN users u           ON u.id = r.student_id
           JOIN class_student cs  ON cs.student_id = u.id
           JOIN classes c         ON c.id = cs.class_id
           JOIN departments d     ON d.id = c.department_id
           GROUP BY d.name;
        """,
            nativeQuery = true)
    List<RegradeRequestByDepartmentSubjects> getRegradeRequestByDepartment();
}
