package com.hostel.system.controller;

import com.hostel.system.model.Student;
import com.hostel.system.model.User;
import com.hostel.system.model.enums.Role;
import com.hostel.system.service.*;
import com.hostel.system.singleton.DatabaseConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class DashboardController {

    private final StudentService studentService;
    private final RoomService roomService;
    private final ComplaintService complaintService;
    private final FeeService feeService;
    private final UserService userService;

    @Autowired
    public DashboardController(StudentService studentService,
                                RoomService roomService,
                                ComplaintService complaintService,
                                FeeService feeService,
                                UserService userService) {
        this.studentService = studentService;
        this.roomService = roomService;
        this.complaintService = complaintService;
        this.feeService = feeService;
        this.userService = userService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        Optional<User> currentUserOpt = userService.findByUsername(username);

        if (currentUserOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = currentUserOpt.get();
        model.addAttribute("currentUser", user);

        // Access Singleton Instance to report system DB health
        DatabaseConnection dbConn = DatabaseConnection.getInstance();
        model.addAttribute("singletonDbInfo", dbConn.getConnectionStatus());

        // Common Dashboard Metrics
        model.addAttribute("totalStudents", studentService.countStudents());
        model.addAttribute("totalRooms", roomService.countTotalRooms());
        model.addAttribute("occupiedRooms", roomService.countOccupiedRooms());
        model.addAttribute("availableRooms", roomService.countAvailableRooms());
        model.addAttribute("pendingComplaints", complaintService.countPendingComplaints());
        model.addAttribute("pendingFees", feeService.countPendingFees());

        if (user.getRole() == Role.ROLE_STUDENT) {
            Optional<Student> studentOpt = studentService.findByUsername(username);
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                model.addAttribute("student", student);
                model.addAttribute("studentComplaints", complaintService.findByStudentId(student.getId()));
                model.addAttribute("studentFees", feeService.findByStudentId(student.getId()));
            }
            return "dashboard/student";
        } else if (user.getRole() == Role.ROLE_WARDEN) {
            model.addAttribute("recentComplaints", complaintService.findByStatus(com.hostel.system.model.enums.ComplaintStatus.PENDING, org.springframework.data.domain.PageRequest.of(0, 5)).getContent());
            return "dashboard/warden";
        } else {
            // ADMIN
            model.addAttribute("recentStudents", studentService.searchStudents("", org.springframework.data.domain.PageRequest.of(0, 5)).getContent());
            return "dashboard/admin";
        }
    }
}
