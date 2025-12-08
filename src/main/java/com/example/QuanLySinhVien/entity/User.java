package com.example.QuanLySinhVien.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @Column(name = "id")
    String id;
    @Column(name = "name")
    String name;
    @Column(name = "start_year")
    String startYear;
    @Column(name = "end_year")
    String endYear;
    @Column(name = "email")
    String email;
    @Column(name = "phone")
    String phone;
    @Column(name = "address")
    String address;
    @Column(name = "date_of_birth")
    LocalDate dateOfBirth;
    @Column(name = "create_at")
    Date createdAt;
    @Column(name = "last_modified")
    Date lastModified;
    @Column(name = "deleted")
    boolean deleted;
    @Column(name = "status")
    boolean status;
    @OneToOne(mappedBy = "users",cascade = CascadeType.ALL)
    Login login;
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    @JsonIgnore
    List<Subject> subjects;
    @OneToMany(mappedBy = "teacher",cascade = CascadeType.ALL)
    @JsonIgnore
    List<Clazz> clazzes;
    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    List<Role> roles = new ArrayList<>();
    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinTable(
            name = "class_student",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "class_id")
    )
    List<Clazz> classes;
    @OneToMany(mappedBy = "student",cascade = CascadeType.ALL)
    @JsonIgnore
    List<ScoreSubject> scoreSubjects;
}
