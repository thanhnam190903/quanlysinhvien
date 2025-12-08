package com.example.QuanLySinhVien.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;


@Entity
@Table(name = "regrade_request")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegradeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;
    @ManyToOne
    @JoinColumn(name = "student_id")
    User student;
    @ManyToOne
    @JoinColumn(name = "subject_id")
    Subject subject;
    @Column(name = "old_score")
    Double oldScore;
    @Column(name = "new_score")
    Double newScore;
    @Column(length = 50)
    String status;
    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    User receiver;
    @Column(name = "request_reason", length = 500)
    String requestReason;
    @Column(name = "teacher_note", length = 500)
    String teacherNote;
    @Column(name = "training_note", length = 500)
    String trainingNote;
    @Column(name = "request_date")
    LocalDate requestDate ;
    @Column(name = "payment_image")
    String paymentImage;
    @Column(name = "created_at")
    LocalDate createdAt ;
    @Column(name = "created_by")
    String createdBy;
    @Column(name = "updated_at")
    LocalDate updatedAt;
    @Column(name = "updated_by")
    String updatedBy;
    Boolean deleted ;
}
