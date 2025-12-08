package com.example.QuanLySinhVien.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "cycles")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cycle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;
    @Column(name = "name")
    String name;
    @Column(name = "description",columnDefinition = "text")
    String description;
    @Column(name = "start_date")
    LocalDate startDate;
    @Column(name = "end_date")
    LocalDate endDate;
    @Column(name ="created_at")
    Date createdAt;
    @Column(name = "last_modified")
    Date lastModified;
    @Column(name = "deleted")
    boolean deleted;
    @Column(name = "status")
    boolean status;
    @OneToMany(mappedBy = "cycle",cascade = CascadeType.ALL)
    List<Subject> subjects;
}
