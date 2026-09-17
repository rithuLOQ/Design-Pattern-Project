package com.hostel.system.service.impl;

import com.hostel.system.builder.StudentBuilder;
import com.hostel.system.dto.StudentRegistrationDto;
import com.hostel.system.exception.ResourceNotFoundException;
import com.hostel.system.model.Room;
import com.hostel.system.model.Student;
import com.hostel.system.model.enums.RoomStatus;
import com.hostel.system.repository.RoomRepository;
import com.hostel.system.repository.StudentRepository;
import com.hostel.system.repository.UserRepository;
import com.hostel.system.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository,
                               UserRepository userRepository,
                               RoomRepository roomRepository,
                               PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Student registerStudent(StudentRegistrationDto dto) {
        if (studentRepository.existsByRegistrationNumber(dto.getRegistrationNumber())) {
            throw new IllegalArgumentException("Registration Number '" + dto.getRegistrationNumber() + "' already exists!");
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username '" + dto.getUsername() + "' already exists!");
        }

        Room assignedRoom = null;
        if (dto.getRoomId() != null) {
            assignedRoom = roomRepository.findById(dto.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room with ID " + dto.getRoomId() + " not found."));

            if (assignedRoom.getOccupiedCount() >= assignedRoom.getCapacity() || assignedRoom.getStatus() == RoomStatus.MAINTENANCE) {
                throw new IllegalArgumentException("Selected Room " + assignedRoom.getRoomNumber() + " is full or under maintenance!");
            }
        }

        String rawPassword = dto.getPassword() != null && !dto.getPassword().trim().isEmpty()
                ? dto.getPassword()
                : "student123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // =========================================================================
        // GANG OF FOUR (GoF) DESIGN PATTERN: BUILDER PATTERN IN ACTION
        // Fluent instantiation of Student object using StudentBuilder instead of huge constructor
        // =========================================================================
        Student student = new StudentBuilder()
                .setUsername(dto.getUsername())
                .setPassword(encodedPassword)
                .setName(dto.getName())
                .setEmail(dto.getEmail())
                .setPhone(dto.getPhone())
                .setRegistrationNumber(dto.getRegistrationNumber())
                .setDepartment(dto.getDepartment())
                .setYear(dto.getYear())
                .setGender(dto.getGender())
                .setAddress(dto.getAddress())
                .setParentName(dto.getParentName())
                .setParentPhone(dto.getParentPhone())
                .setBloodGroup(dto.getBloodGroup())
                .setMedicalCondition(dto.getMedicalCondition())
                .setRoom(assignedRoom)
                .build();

        if (assignedRoom != null) {
            assignedRoom.setOccupiedCount(assignedRoom.getOccupiedCount() + 1);
            assignedRoom.updateStatus();
            roomRepository.save(assignedRoom);
        }

        return studentRepository.save(student);
    }

    @Override
    @Transactional
    public Student updateStudent(Long id, StudentRegistrationDto dto) {
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        existing.setDepartment(dto.getDepartment());
        existing.setYear(dto.getYear());
        existing.setGender(dto.getGender());
        existing.setAddress(dto.getAddress());
        existing.setParentName(dto.getParentName());
        existing.setParentPhone(dto.getParentPhone());
        existing.setBloodGroup(dto.getBloodGroup());
        existing.setMedicalCondition(dto.getMedicalCondition());

        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return studentRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        if (student.getRoom() != null) {
            Room room = student.getRoom();
            room.setOccupiedCount(Math.max(0, room.getOccupiedCount() - 1));
            room.updateStatus();
            roomRepository.save(room);
        }

        studentRepository.delete(student);
    }

    @Override
    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    @Override
    public Optional<Student> findByRegistrationNumber(String registrationNumber) {
        return studentRepository.findByRegistrationNumber(registrationNumber);
    }

    @Override
    public Optional<Student> findByUsername(String username) {
        return studentRepository.findByUsername(username);
    }

    @Override
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Override
    public Page<Student> searchStudents(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return studentRepository.searchStudents(keyword.trim(), pageable);
        }
        return studentRepository.findAll(pageable);
    }

    @Override
    public long countStudents() {
        return studentRepository.count();
    }

    @Override
    @Transactional
    public Student allocateRoom(Long studentId, Long roomId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
        Room newRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        if (newRoom.getOccupiedCount() >= newRoom.getCapacity() || newRoom.getStatus() == RoomStatus.MAINTENANCE) {
            throw new IllegalArgumentException("Room " + newRoom.getRoomNumber() + " is not available for allocation!");
        }

        // Vacate current room if assigned
        if (student.getRoom() != null) {
            Room oldRoom = student.getRoom();
            oldRoom.setOccupiedCount(Math.max(0, oldRoom.getOccupiedCount() - 1));
            oldRoom.updateStatus();
            roomRepository.save(oldRoom);
        }

        student.setRoom(newRoom);
        newRoom.setOccupiedCount(newRoom.getOccupiedCount() + 1);
        newRoom.updateStatus();
        roomRepository.save(newRoom);

        return studentRepository.save(student);
    }

    @Override
    @Transactional
    public Student vacateRoom(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        if (student.getRoom() != null) {
            Room room = student.getRoom();
            room.setOccupiedCount(Math.max(0, room.getOccupiedCount() - 1));
            room.updateStatus();
            roomRepository.save(room);
            student.setRoom(null);
        }

        return studentRepository.save(student);
    }
}
