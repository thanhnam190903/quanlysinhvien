package com.example.QuanLySinhVien.repository;

import com.example.QuanLySinhVien.entity.Login;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<Login,Integer> {
    Login findByUsername(String username);
}
