package com.practice.url_shortner.controller;
import com.practice.url_shortner.model.Student;
import com.practice.url_shortner.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
// ↑ NEW: this prefixes ALL endpoints in this class with /api/students
//   so @GetMapping("") below means GET /api/students
public class StudentController {
    @Autowired
    private StudentService studentService;

    // CREATE — POST /api/students
    @PostMapping
    public Student createStudent(@RequestBody StudentRequest request) {
        return studentService.createStudent(request);
    }

    // READ ALL — GET /api/students
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    // READ ONE — GET /api/students/5
    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    // UPDATE — PUT /api/students/5
    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable Long id, @RequestBody StudentRequest request) {
        return studentService.updateStudent(id, request);
    }

    // DELETE — DELETE /api/students/5
    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "Student with id " + id + " deleted successfully";
    }
    @GetMapping("/search/name/{name}")
    public List<Student> getStudentsByName(@PathVariable String name) {
        return studentService.getStudentsByName(name);
    }

    @GetMapping("/search/email/{email}")
    public Student getStudentByEmail(@PathVariable String email) {
        return studentService.getStudentByEmail(email);
    }

    @GetMapping("/search/older-than/{age}")
    public List<Student> getStudentsOlderThan(@PathVariable int age) {
        return studentService.getStudentsOlderThan(age);
    }

}
