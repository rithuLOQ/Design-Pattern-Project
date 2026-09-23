package com.hostel.config;

import com.hostel.entity.User;
import com.hostel.entity.UserRole;
import com.hostel.entity.Student;
import com.hostel.repository.UserRepository;
import com.hostel.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, StudentRepository studentRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        User admin = ensureUser("admin", "admin123", UserRole.ADMIN);
        ensureUser("warden", "warden123", UserRole.WARDEN);
        User studentUser = ensureUser("student", "student123", UserRole.STUDENT);

        if (studentRepository.findByUserUsername("student").isEmpty()) {
            Student student = new Student();
            student.setUser(studentUser);
            student.setRegistrationNumber("V2-STU-001");
            student.setName("Demo Student");
            student.setEmail("student@hostel.local");
            student.setPhone("9876543210");
            student.setDepartment("Computer Science");
            student.setYear(2);
            student.setAddress("VIT Hostel");
            student.setParentName("Demo Parent");
            student.setParentPhone("9876543211");
            studentRepository.save(student);
        }
    }

    private User ensureUser(String username, String password, UserRole role) {
        User user = userRepository.findByUsername(username).orElseGet(User::new);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setEnabled(true);
        return userRepository.save(user);
    }
}
