package com.example.QuanLySinhVien.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "subjects")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;
    @Column(name = "name")
    String name;
    @Column(name = "description",columnDefinition = "text")
    String description;
    @Column(name = "process_coefficient")
    float processCoefficient;
    @Column(name = "exam_coefficient")
    float examCoefficient;
    @Column(name = "credit")
    int credit;
    @Column(name = "start_date")
    LocalDate startDate;
    @Column(name = "end_date")
    LocalDate endDate;
    @Column(name = "regrade_start")
    LocalDate regradeStart;
    @Column(name = "regrade_end")
    LocalDate regradeEnd;
    @Column(name = "created_at")
    Date createdAt;
    @Column(name = "last_modified")
    Date lastModified;
    @Column(name = "deleted")
    boolean deleted;
    @Column(name = "status")
    boolean status;
    @ManyToOne
    @JoinColumn(name = "cycle_id")
    Cycle cycle;
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    User user;
    @OneToMany(mappedBy = "subject",cascade = CascadeType.ALL)
    List<ScoreSubject> scoreSubjects;
}
