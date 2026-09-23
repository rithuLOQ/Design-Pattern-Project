package com.hostel.controller;

import com.hostel.entity.Complaint;
import com.hostel.entity.Fee;
import com.hostel.entity.Student;
import com.hostel.service.ComplaintService;
import com.hostel.service.FeeService;
import com.hostel.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final StudentService studentService;
    private final ComplaintService complaintService;
    private final FeeService feeService;

    public StudentController(StudentService studentService, ComplaintService complaintService, FeeService feeService) {
        this.studentService = studentService;
        this.complaintService = complaintService;
        this.feeService = feeService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        Student student = studentService.getStudentByUserName(authentication.getName());
        model.addAttribute("student", student);
        model.addAttribute("complaints", complaintService.getComplaintsByStudentId(student.getId()));
        model.addAttribute("fees", feeService.getFeesByStudentId(student.getId()));
        return "student/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        model.addAttribute("student", studentService.getStudentByUserName(authentication.getName()));
        return "student/profile";
    }

    @GetMapping("/room")
    public String room(Authentication authentication, Model model) {
        model.addAttribute("student", studentService.getStudentByUserName(authentication.getName()));
        return "student/room";
    }

    @GetMapping("/complaints")
    public String complaints(Authentication authentication, Model model) {
        Student student = studentService.getStudentByUserName(authentication.getName());
        model.addAttribute("student", student);
        model.addAttribute("complaints", complaintService.getComplaintsByStudentId(student.getId()));
        model.addAttribute("complaint", new Complaint());
        return "student/complaints";
    }

    @PostMapping("/complaints")
    public String createComplaint(Authentication authentication,
                                 @Valid @ModelAttribute("complaint") Complaint complaint,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        Student student = studentService.getStudentByUserName(authentication.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("student", student);
                model.addAttribute("complaints", complaintService.getComplaintsByStudentId(student.getId()));
            return "student/complaints";
        }
        complaint.setStudent(student);
        complaintService.createComplaint(complaint);
        redirectAttributes.addFlashAttribute("successMessage", "Complaint submitted successfully");
        return "redirect:/student/complaints";
    }

    @GetMapping("/fees")
    public String fees(Authentication authentication, Model model) {
        Student student = studentService.getStudentByUserName(authentication.getName());
        model.addAttribute("student", student);
        model.addAttribute("fees", feeService.getFeesByStudentId(student.getId()));
        return "student/fees";
    }
}
