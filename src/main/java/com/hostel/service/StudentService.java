package com.hostel.service;

import com.hostel.entity.Student;
import com.hostel.entity.User;
import com.hostel.exception.DuplicateStudentException;
import com.hostel.exception.StudentNotFoundException;
import com.hostel.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional
    public Student createStudent(Student student) {
        if (studentRepository.existsByRegistrationNumber(student.getRegistrationNumber())) {
            throw new DuplicateStudentException("Student with registration number already exists");
        }
        if (student.getUser() != null && student.getUser().getId() == null) {
            student.getUser().setEnabled(true);
        }
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with ID: " + id));
    }

    public Student getStudentByUserName(String username) {
        return studentRepository.findByUserUsername(username)
                .orElseThrow(() -> new StudentNotFoundException("Student not found for user: " + username));
    }

    public void deleteStudent(Long id) {
        Student student = getStudentById(id);
        studentRepository.delete(student);
    }

    public Student updateStudent(Student student) {
        getStudentById(student.getId());
        return studentRepository.save(student);
    }
}
