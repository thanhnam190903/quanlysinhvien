package com.example.QuanLySinhVien.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "departments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;
    @Column(name = "name")
    String name;
    @Column(name = "description",columnDefinition = "text")
    String description;
    @Column(name = "create_at")
    Date createdAt;
    @Column(name = "last_modified")
    Date lastModified;
    @Column(name = "deleted")
    boolean deleted;
    @Column(name = "status")
    boolean status;
    @OneToMany(mappedBy = "department",cascade = CascadeType.ALL)
    List<Clazz> clazzes;
}
