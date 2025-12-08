package com.example.QuanLySinhVien.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "classes")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Clazz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;
    @Column(name = "name")
    String name;
    @Column(name = "description",columnDefinition = "text")
    String description;
    @Column(name = "created_at")
    Date createdAt;
    @Column(name = "last_modified")
    Date lastModified;
    @Column(name = "deleted")
    boolean deleted;
    @Column(name = "status")
    boolean status;
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    User teacher;
    @ManyToOne
    @JoinColumn(name = "department_id")
    Department department;
    @ManyToMany(mappedBy = "classes")
    List<User> students;
}
