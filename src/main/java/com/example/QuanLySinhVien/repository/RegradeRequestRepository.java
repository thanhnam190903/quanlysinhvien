package com.example.QuanLySinhVien.repository;

import com.example.QuanLySinhVien.entity.RegradeRequest;
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

}
