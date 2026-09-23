package com.hostel.patterns.command;

import com.hostel.entity.Complaint;
import com.hostel.entity.Fee;
import com.hostel.entity.Room;
import com.hostel.entity.Student;
import com.hostel.service.ComplaintService;
import com.hostel.service.FeeService;
import com.hostel.service.RoomService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class HostelManagementReceiver {

    private final RoomService roomService;
    private final ComplaintService complaintService;
    private final FeeService feeService;

    public HostelManagementReceiver(RoomService roomService, ComplaintService complaintService, FeeService feeService) {
        this.roomService = roomService;
        this.complaintService = complaintService;
        this.feeService = feeService;
    }

    public Room allocateRoom(Student student, Room room) {
        return roomService.allocateRoom(student, room);
    }

    public Room vacateRoom(Student student) {
        return roomService.vacateRoom(student);
    }

    public Complaint resolveComplaint(Long complaintId, String status) {
        return complaintService.resolveComplaint(complaintId, status);
    }

    public Fee generateFee(Student student, BigDecimal amount, LocalDate dueDate) {
        return feeService.generateFee(student, amount, dueDate);
    }
}
