package com.practice.url_shortner.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity  // "this class = a database table"
public class Student {
    @Id  // "this field = primary key"
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // ↑ "auto increment this — MySQL handles numbering"
    private Long id;

    private String name;
    private int age;
    private String email;

    // Empty constructor — JPA REQUIRES this to exist
    public Student() {
    }

    // Constructor with fields — for easy object creation
    public Student(String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }

    // Getters and Setters — JPA needs these too
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
