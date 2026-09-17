package com.hostel.system.controller;

import com.hostel.system.dto.ComplaintDto;
import com.hostel.system.model.Complaint;
import com.hostel.system.model.Student;
import com.hostel.system.model.User;
import com.hostel.system.model.enums.ComplaintStatus;
import com.hostel.system.model.enums.Role;
import com.hostel.system.service.ComplaintService;
import com.hostel.system.service.StudentService;
import com.hostel.system.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;
    private final StudentService studentService;
    private final UserService userService;

    @Autowired
    public ComplaintController(ComplaintService complaintService,
                               StudentService studentService,
                               UserService userService) {
        this.complaintService = complaintService;
        this.studentService = studentService;
        this.userService = userService;
    }

    @GetMapping
    public String listComplaints(@RequestParam(value = "status", required = false) String statusStr,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "10") int size,
                                 Authentication authentication,
                                 Model model) {
        String username = authentication.getName();
        Optional<User> currentUserOpt = userService.findByUsername(username);

        if (currentUserOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = currentUserOpt.get();
        model.addAttribute("currentUser", user);

        if (user.getRole() == Role.ROLE_STUDENT) {
            Optional<Student> studentOpt = studentService.findByUsername(username);
            if (studentOpt.isPresent()) {
                model.addAttribute("complaints", complaintService.findByStudentId(studentOpt.get().getId()));
            }
            return "complaints/list";
        }

        // ADMIN / WARDEN view with filter and pagination
        Page<Complaint> complaintPage;
        if (statusStr != null && !statusStr.trim().isEmpty()) {
            try {
                ComplaintStatus status = ComplaintStatus.valueOf(statusStr.toUpperCase());
                complaintPage = complaintService.findByStatus(status, PageRequest.of(page, size));
            } catch (Exception e) {
                complaintPage = complaintService.findAll(PageRequest.of(page, size));
            }
        } else {
            complaintPage = complaintService.findAll(PageRequest.of(page, size));
        }

        model.addAttribute("complaintPage", complaintPage);
        model.addAttribute("statuses", ComplaintStatus.values());
        model.addAttribute("selectedStatus", statusStr);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", complaintPage.getTotalPages());

        return "complaints/list";
    }

    @GetMapping("/new")
    public String showComplaintForm(Authentication authentication, Model model) {
        String username = authentication.getName();
        Optional<Student> studentOpt = studentService.findByUsername(username);

        if (studentOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Only registered students can raise complaints.");
            return "redirect:/complaints";
        }

        model.addAttribute("complaintDto", new ComplaintDto());
        return "complaints/form";
    }

    @PostMapping("/save")
    public String saveComplaint(@Valid @ModelAttribute("complaintDto") ComplaintDto complaintDto,
                                BindingResult bindingResult,
                                Authentication authentication,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "complaints/form";
        }

        String username = authentication.getName();
        Optional<Student> studentOpt = studentService.findByUsername(username);

        if (studentOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Student record not found.");
            return "redirect:/complaints";
        }

        complaintService.raiseComplaint(studentOpt.get().getId(), complaintDto);
        redirectAttributes.addFlashAttribute("successMessage", "Complaint submitted successfully.");

        return "redirect:/complaints";
    }

    @PostMapping("/resolve")
    public String resolveComplaint(@RequestParam("complaintId") Long complaintId,
                                   @RequestParam("status") ComplaintStatus status,
                                   @RequestParam(value = "resolutionNote", required = false) String resolutionNote,
                                   RedirectAttributes redirectAttributes) {
        try {
            complaintService.updateComplaintStatus(complaintId, status, resolutionNote);
            redirectAttributes.addFlashAttribute("successMessage", "Complaint status updated to " + status + "!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/complaints";
    }
}
