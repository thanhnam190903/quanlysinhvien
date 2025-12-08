package com.example.QuanLySinhVien.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Date;

@Entity
@Table(name = "score_subjects")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScoreSubject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;
    @Column(name = "score_process")
    Double scoreProcess;
    @Column(name = "score_final")
    Double scoreFinal;
    @Column(name = "total_score")
    Double totalScore;
    @Column(name = "attempt")
    int attempt;
    @Column(name = "exam_attempt")
    int examAttempt;
    @Column(name = "created_at")
    Date createdAt;
    @Column(name = "last_modified")
    Date lastModified;
    @Column(name = "description")
    String description;
    @Column(name = "deleted")
    boolean deleted;
    @ManyToOne
    @JoinColumn(name = "student_id")
    User student;
    @ManyToOne
    @JoinColumn(name = "subject_id")
    Subject subject;
}
