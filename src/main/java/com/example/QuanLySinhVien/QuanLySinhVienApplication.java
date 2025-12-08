package com.example.QuanLySinhVien;

import com.example.QuanLySinhVien.service.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class QuanLySinhVienApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuanLySinhVienApplication.class, args);
	}
	@Bean
	CommandLineRunner init(StorageService storageService){
		return (args)->{
			storageService.init();
		};
	}
}
