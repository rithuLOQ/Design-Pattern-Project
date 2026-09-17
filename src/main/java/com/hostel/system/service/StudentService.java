package com.hostel.system.service;

import com.hostel.system.dto.StudentRegistrationDto;
import com.hostel.system.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    Student registerStudent(StudentRegistrationDto dto);

    Student updateStudent(Long id, StudentRegistrationDto dto);

    void deleteStudent(Long id);

    Optional<Student> findById(Long id);

    Optional<Student> findByRegistrationNumber(String registrationNumber);

    Optional<Student> findByUsername(String username);

    List<Student> findAll();

    Page<Student> searchStudents(String keyword, Pageable pageable);

    long countStudents();

    Student allocateRoom(Long studentId, Long roomId);

    Student vacateRoom(Long studentId);
}
