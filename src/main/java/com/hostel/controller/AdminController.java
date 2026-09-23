package com.hostel.controller;

import com.hostel.entity.*;
import com.hostel.patterns.builder.StudentBuilder;
import com.hostel.patterns.command.*;
import com.hostel.patterns.facade.HostelManagementFacade;
import com.hostel.patterns.factory.UserFactory;
import com.hostel.patterns.singleton.SystemConfigurationManager;
import com.hostel.service.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final StudentService studentService;
    private final RoomService roomService;
    private final ComplaintService complaintService;
    private final FeeService feeService;
    private final HostelCommandInvoker hostelCommandInvoker;
    private final HostelManagementReceiver hostelManagementReceiver;
    private final UserFactory userFactory;
    private final HostelManagementFacade hostelManagementFacade;
    private final SystemConfigurationManager systemConfigurationManager;

    public AdminController(UserService userService, StudentService studentService, RoomService roomService,
                          ComplaintService complaintService, FeeService feeService,
                          HostelCommandInvoker hostelCommandInvoker, HostelManagementReceiver hostelManagementReceiver,
                          UserFactory userFactory, HostelManagementFacade hostelManagementFacade) {
        this.userService = userService;
        this.studentService = studentService;
        this.roomService = roomService;
        this.complaintService = complaintService;
        this.feeService = feeService;
        this.hostelCommandInvoker = hostelCommandInvoker;
        this.hostelManagementReceiver = hostelManagementReceiver;
        this.userFactory = userFactory;
        this.hostelManagementFacade = hostelManagementFacade;
        this.systemConfigurationManager = SystemConfigurationManager.getInstance();
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long students = studentService.getAllStudents().size();
        long rooms = roomService.getAllRooms().size();
        long occupied = roomService.getAllRooms().stream().filter(r -> r.getOccupiedCount() > 0).count();
        long available = roomService.getAllRooms().stream().filter(r -> r.getOccupiedCount() < r.getCapacity()).count();
        long pendingComplaints = complaintService.getAllComplaints().stream()
                .filter(c -> c.getStatus() == ComplaintStatus.PENDING || c.getStatus() == ComplaintStatus.IN_PROGRESS)
                .count();
        long pendingFees = feeService.getAllFees().stream()
                .filter(f -> f.getStatus() == FeeStatus.PENDING || f.getStatus() == FeeStatus.OVERDUE)
                .count();

        model.addAttribute("studentCount", students);
        model.addAttribute("roomCount", rooms);
        model.addAttribute("occupiedRooms", occupied);
        model.addAttribute("availableRooms", available);
        model.addAttribute("pendingComplaints", pendingComplaints);
        model.addAttribute("pendingFees", pendingFees);
        model.addAttribute("singletonInfo", systemConfigurationManager.getDefaultFeeCurrency());
        return "admin/dashboard";
    }

    @GetMapping("/students")
    public String listStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "admin/students";
    }

    @GetMapping("/students/add")
    public String showAddStudentForm(Model model) {
        model.addAttribute("student", new Student());
        return "admin/student-form";
    }

    @PostMapping("/students/add")
    public String addStudent(@Valid @ModelAttribute("student") Student student,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/student-form";
        }

        User user = userFactory.createUser(student.getEmail().split("@")[0], "student123", UserRole.STUDENT);
        userService.createUser(user);

        Student generated = new StudentBuilder()
                .setUser(user)
                .setRegistrationNumber(student.getRegistrationNumber())
                .setName(student.getName())
                .setEmail(student.getEmail())
                .setPhone(student.getPhone())
                .setDepartment(student.getDepartment())
                .setYear(student.getYear())
                .setAddress(student.getAddress())
                .setParentName(student.getParentName())
                .setParentPhone(student.getParentPhone())
                .build();

        hostelManagementFacade.registerStudent(generated);
        redirectAttributes.addFlashAttribute("successMessage", "Student created successfully");
        return "redirect:/admin/students";
    }

    @GetMapping("/students/edit/{id}")
    public String showEditStudentForm(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.getStudentById(id));
        return "admin/student-form";
    }

    @PostMapping("/students/edit/{id}")
    public String updateStudent(@PathVariable Long id, @Valid @ModelAttribute("student") Student student,
                               BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/student-form";
        }
        Student existing = studentService.getStudentById(id);
        existing.setName(student.getName());
        existing.setEmail(student.getEmail());
        existing.setPhone(student.getPhone());
        existing.setDepartment(student.getDepartment());
        existing.setYear(student.getYear());
        existing.setAddress(student.getAddress());
        existing.setParentName(student.getParentName());
        existing.setParentPhone(student.getParentPhone());
        studentService.updateStudent(existing);
        redirectAttributes.addFlashAttribute("successMessage", "Student updated successfully");
        return "redirect:/admin/students";
    }

    @PostMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        studentService.deleteStudent(id);
        redirectAttributes.addFlashAttribute("successMessage", "Student deleted successfully");
        return "redirect:/admin/students";
    }

    @GetMapping("/rooms")
    public String listRooms(Model model) {
        model.addAttribute("rooms", roomService.getAllRooms());
        return "admin/rooms";
    }

    @GetMapping("/rooms/add")
    public String showAddRoomForm(Model model) {
        model.addAttribute("room", new Room());
        return "admin/room-form";
    }

    @PostMapping("/rooms/add")
    public String addRoom(@Valid @ModelAttribute("room") Room room,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/room-form";
        }
        roomService.createRoom(room);
        redirectAttributes.addFlashAttribute("successMessage", "Room created successfully");
        return "redirect:/admin/rooms";
    }

    @GetMapping("/rooms/edit/{id}")
    public String showEditRoomForm(@PathVariable Long id, Model model) {
        model.addAttribute("room", roomService.getRoomById(id));
        return "admin/room-form";
    }

    @PostMapping("/rooms/edit/{id}")
    public String updateRoom(@PathVariable Long id, @Valid @ModelAttribute("room") Room room,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/room-form";
        }
        Room existing = roomService.getRoomById(id);
        existing.setRoomNumber(room.getRoomNumber());
        existing.setRoomType(room.getRoomType());
        existing.setCapacity(room.getCapacity());
        roomService.updateRoom(existing);
        redirectAttributes.addFlashAttribute("successMessage", "Room updated successfully");
        return "redirect:/admin/rooms";
    }

    @PostMapping("/rooms/delete/{id}")
    public String deleteRoom(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        roomService.deleteRoom(id);
        redirectAttributes.addFlashAttribute("successMessage", "Room deleted successfully");
        return "redirect:/admin/rooms";
    }

    @GetMapping("/rooms/allocate")
    public String showAllocateForm(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("rooms", roomService.getAllRooms());
        return "admin/allocate-room";
    }

    @PostMapping("/rooms/allocate")
    public String allocateRoom(@RequestParam Long studentId, @RequestParam Long roomId,
                              RedirectAttributes redirectAttributes) {
        hostelManagementFacade.allocateRoom(studentId, roomId);
        redirectAttributes.addFlashAttribute("successMessage", "Room allocated successfully");
        return "redirect:/admin/rooms";
    }

    @PostMapping("/rooms/vacate")
    public String vacateRoom(@RequestParam Long studentId, RedirectAttributes redirectAttributes) {
        Student student = studentService.getStudentById(studentId);
        VacateRoomCommand command = new VacateRoomCommand(hostelManagementReceiver, student);
        hostelCommandInvoker.submit(command);
        redirectAttributes.addFlashAttribute("successMessage", "Room vacated successfully");
        return "redirect:/admin/rooms";
    }

    @GetMapping("/complaints")
    public String complaints(Model model) {
        model.addAttribute("complaints", complaintService.getAllComplaints());
        return "admin/complaints";
    }

    @GetMapping("/complaints/{id}")
    public String complaintDetails(@PathVariable Long id, Model model) {
        model.addAttribute("complaint", complaintService.getComplaintById(id));
        return "admin/complaint-details";
    }

    @PostMapping("/complaints/resolve/{id}")
    public String resolveComplaint(@PathVariable Long id, @RequestParam String status,
                                  RedirectAttributes redirectAttributes) {
        ResolveComplaintCommand command = new ResolveComplaintCommand(hostelManagementReceiver, id, status);
        hostelCommandInvoker.submit(command);
        redirectAttributes.addFlashAttribute("successMessage", "Complaint status updated");
        return "redirect:/admin/complaints";
    }

    @GetMapping("/fees")
    public String fees(Model model) {
        model.addAttribute("fees", feeService.getAllFees());
        model.addAttribute("students", studentService.getAllStudents());
        return "admin/fees";
    }

    @PostMapping("/fees/generate")
    public String generateFee(@RequestParam Long studentId, @RequestParam BigDecimal amount,
                              @RequestParam LocalDate dueDate, RedirectAttributes redirectAttributes) {
        Student student = studentService.getStudentById(studentId);
        GenerateFeeCommand command = new GenerateFeeCommand(hostelManagementReceiver, student, amount, dueDate);
        hostelCommandInvoker.submit(command);
        redirectAttributes.addFlashAttribute("successMessage", "Fee generated successfully");
        return "redirect:/admin/fees";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }
}
