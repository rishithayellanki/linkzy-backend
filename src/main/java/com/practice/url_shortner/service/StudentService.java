package com.practice.url_shortner.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.url_shortner.controller.StudentRequest;
import com.practice.url_shortner.exception.StudentNotFoundException;
import com.practice.url_shortner.model.Student;
import com.practice.url_shortner.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class StudentService {
    @Autowired
    private RateLimitService rateLimitService;
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();
    // ↑ Jackson's converter — Java object ↔ JSON string

    private static final long CACHE_EXPIRY_SECONDS = 300;

    public Student createStudent(StudentRequest request) {
        // Using email as the rate-limit identifier for this test
        rateLimitService.checkRateLimit(request.getEmail());

        Student student = new Student(request.getName(), request.getAge(), request.getEmail());
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        String cacheKey = "student:" + id;

        // STEP 1 — Check Redis first
        String cachedJson = redisTemplate.opsForValue().get(cacheKey);

        if (cachedJson != null) {
            System.out.println("CACHE HIT for: " + cacheKey + " — NO database call!");
            try {
                // Convert JSON string back into a Student object
                return objectMapper.readValue(cachedJson, Student.class);
            } catch (JsonProcessingException e) {
                // If conversion fails for any reason, fall through to DB
                System.out.println("Failed to parse cached JSON, falling back to DB");
            }
        }

        // STEP 2 — CACHE MISS — go to MySQL
        System.out.println("CACHE MISS for: " + cacheKey + " — checking MySQL");

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));

        // STEP 3 — Save FULL object as JSON into Redis
        try {
            String studentJson = objectMapper.writeValueAsString(student);
            redisTemplate.opsForValue().set(cacheKey, studentJson, CACHE_EXPIRY_SECONDS, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            System.out.println("Failed to cache student as JSON: " + e.getMessage());
        }

        return student;
    }

    public Student updateStudent(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));

        student.setName(request.getName());
        student.setAge(request.getAge());
        student.setEmail(request.getEmail());

        Student updated = studentRepository.save(student);

        String cacheKey = "student:" + id;
        redisTemplate.delete(cacheKey);
        System.out.println("Cache invalidated for: " + cacheKey + " (data was updated)");

        return updated;
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException(id);
        }
        studentRepository.deleteById(id);

        String cacheKey = "student:" + id;
        redisTemplate.delete(cacheKey);
        System.out.println("Cache removed for: " + cacheKey + " (data was deleted)");
    }

    public List<Student> getStudentsByName(String name) {
        return studentRepository.findByName(name);
    }

    public Student getStudentByEmail(String email) {
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with email: " + email));
    }

    public List<Student> getStudentsOlderThan(int age) {
        return studentRepository.findByAgeGreaterThan(age);
    }

    public boolean checkEmailExists(String email) {
        return studentRepository.existsByEmail(email);
    }
}