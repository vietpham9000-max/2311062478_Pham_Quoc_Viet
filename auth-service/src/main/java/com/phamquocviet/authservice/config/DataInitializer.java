package com.phamquocviet.authservice.config;

import com.phamquocviet.authservice.entity.*;
import com.phamquocviet.authservice.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    @Bean CommandLineRunner seedUsers(UserRepository users, StudentRepository students, PasswordEncoder encoder) {
        return args -> {
            if (users.findByUsername("admin").isEmpty()) {
                User admin = new User(); admin.setUsername("admin"); admin.setPassword(encoder.encode("admin123")); admin.setRole("ADMIN"); users.save(admin);
            }
            User studentUser = users.findByUsername("student").orElseGet(() -> {
                User user = new User(); user.setUsername("student"); user.setPassword(encoder.encode("student123")); user.setRole("STUDENT"); return users.save(user);
            });
            if (!students.existsByStudentCode("SV001")) {
                Student student = new Student(); student.setStudentCode("SV001"); student.setFullName("Pham Quoc Viet"); student.setUser(studentUser); students.save(student);
            }
        };
    }
}
