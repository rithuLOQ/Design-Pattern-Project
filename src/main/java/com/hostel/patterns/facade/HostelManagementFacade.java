package com.hostel.patterns.facade;

import com.hostel.entity.*;
import com.hostel.exception.*;
import com.hostel.service.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Facade pattern.
 *
 * Simplifies client interaction with the hostel subsystems.
 * StudentService, RoomService, ComplaintService and FeeService are coordinated behind one API.
 */
@Component
public class HostelManagementFacade {

    private final StudentService studentService;
    private final RoomService roomService;
    private final ComplaintService complaintService;
    private final FeeService feeService;

    public HostelManagementFacade(StudentService studentService, RoomService roomService,
                                 ComplaintService complaintService, FeeService feeService) {
        this.studentService = studentService;
        this.roomService = roomService;
        this.complaintService = complaintService;
        this.feeService = feeService;
    }

    public Student registerStudent(Student student) {
        return studentService.createStudent(student);
    }

    public Student getStudentById(Long id) {
        return studentService.getStudentById(id);
    }

    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    public Room allocateRoom(Long studentId, Long roomId) {
        Student student = studentService.getStudentById(studentId);
        Room room = roomService.getRoomById(roomId);
        return roomService.allocateRoom(student, room);
    }

    public Room vacateRoom(Long studentId) {
        Student student = studentService.getStudentById(studentId);
        return roomService.vacateRoom(student);
    }

    public Complaint raiseComplaint(Complaint complaint) {
        return complaintService.createComplaint(complaint);
    }

    public Complaint resolveComplaint(Long complaintId, String resolutionStatus) {
        return complaintService.resolveComplaint(complaintId, resolutionStatus);
    }

    public Fee generateFee(Long studentId, BigDecimal amount, LocalDate dueDate) {
        Student student = studentService.getStudentById(studentId);
        return feeService.generateFee(student, amount, dueDate);
    }

    public List<Complaint> getAllComplaints() {
        return complaintService.getAllComplaints();
    }

    public List<Fee> getAllFees() {
        return feeService.getAllFees();
    }
}
