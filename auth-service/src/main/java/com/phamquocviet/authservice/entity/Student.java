package com.phamquocviet.authservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "student_code", nullable = false, unique = true, length = 30)
    private String studentCode;
    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;
    @OneToOne(optional = false) @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public Long getId() { return id; }
    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
