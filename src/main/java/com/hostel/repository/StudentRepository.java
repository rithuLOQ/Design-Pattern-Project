package com.hostel.repository;

import com.hostel.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRegistrationNumber(String registrationNumber);
    List<Student> findByNameContainingIgnoreCase(String name);
    List<Student> findByDepartmentContainingIgnoreCase(String department);
    boolean existsByRegistrationNumber(String registrationNumber);
    Optional<Student> findByUserUsername(String username);
}
