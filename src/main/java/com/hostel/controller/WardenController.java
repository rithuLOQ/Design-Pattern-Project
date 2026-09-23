package com.hostel.controller;

import com.hostel.entity.*;
import com.hostel.patterns.command.*;
import com.hostel.service.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/warden")
@PreAuthorize("hasRole('WARDEN')")
public class WardenController {

    private final StudentService studentService;
    private final RoomService roomService;
    private final ComplaintService complaintService;
    private final FeeService feeService;
    private final HostelCommandInvoker hostelCommandInvoker;
    private final HostelManagementReceiver hostelManagementReceiver;

    public WardenController(StudentService studentService, RoomService roomService,
                           ComplaintService complaintService, FeeService feeService,
                           HostelCommandInvoker hostelCommandInvoker,
                           HostelManagementReceiver hostelManagementReceiver) {
        this.studentService = studentService;
        this.roomService = roomService;
        this.complaintService = complaintService;
        this.feeService = feeService;
        this.hostelCommandInvoker = hostelCommandInvoker;
        this.hostelManagementReceiver = hostelManagementReceiver;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("studentCount", studentService.getAllStudents().size());
        model.addAttribute("roomStats", roomService.getAllRooms());
        model.addAttribute("pendingComplaints", complaintService.getAllComplaints().stream()
                .filter(c -> c.getStatus() == ComplaintStatus.PENDING || c.getStatus() == ComplaintStatus.IN_PROGRESS)
                .count());
        model.addAttribute("feeCount", feeService.getAllFees().size());
        return "warden/dashboard";
    }

    @GetMapping("/students")
    public String students(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "warden/students";
    }

    @GetMapping("/rooms")
    public String rooms(Model model) {
        model.addAttribute("rooms", roomService.getAllRooms());
        return "warden/rooms";
    }

    @GetMapping("/complaints")
    public String complaints(Model model) {
        model.addAttribute("complaints", complaintService.getAllComplaints());
        return "warden/complaints";
    }

    @PostMapping("/rooms/allocate")
    public String allocateRoom(@RequestParam Long studentId, @RequestParam Long roomId,
                              RedirectAttributes redirectAttributes) {
        Student student = studentService.getStudentById(studentId);
        Room room = roomService.getRoomById(roomId);
        hostelCommandInvoker.submit(new AllocateRoomCommand(hostelManagementReceiver, student, room));
        redirectAttributes.addFlashAttribute("successMessage", "Room allocated by warden");
        return "redirect:/warden/rooms";
    }

    @PostMapping("/rooms/vacate")
    public String vacateRoom(@RequestParam Long studentId, RedirectAttributes redirectAttributes) {
        Student student = studentService.getStudentById(studentId);
        hostelCommandInvoker.submit(new VacateRoomCommand(hostelManagementReceiver, student));
        redirectAttributes.addFlashAttribute("successMessage", "Room vacated by warden");
        return "redirect:/warden/rooms";
    }

    @PostMapping("/complaints/resolve/{id}")
    public String resolveComplaint(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        hostelCommandInvoker.submit(new ResolveComplaintCommand(hostelManagementReceiver, id, "RESOLVED"));
        redirectAttributes.addFlashAttribute("successMessage", "Complaint resolved");
        return "redirect:/warden/complaints";
    }

    @GetMapping("/fees")
    public String fees(Model model) {
        model.addAttribute("fees", feeService.getAllFees());
        return "warden/fees";
    }
}
