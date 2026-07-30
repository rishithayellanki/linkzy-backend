package com.practice.url_shortner.repository;
import com.practice.url_shortner.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student,Long>{
    // SELECT * FROM student WHERE name = ?
    List<Student> findByName(String name);

    // SELECT * FROM student WHERE email = ?
    Optional<Student> findByEmail(String email);
    // ↑ Optional because email should be unique — at most ONE result

    // SELECT * FROM student WHERE age > ?
    List<Student> findByAgeGreaterThan(int age);

    // SELECT * FROM student WHERE age < ?
    List<Student> findByAgeLessThan(int age);

    // SELECT * FROM student WHERE name = ? AND age = ?
    List<Student> findByNameAndAge(String name, int age);

    // SELECT COUNT(*) FROM student WHERE age > ?
    long countByAgeGreaterThan(int age);

    // SELECT EXISTS(... ) WHERE email = ?
    boolean existsByEmail(String email);
}
