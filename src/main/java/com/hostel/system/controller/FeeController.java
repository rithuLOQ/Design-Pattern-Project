package com.hostel.system.controller;

import com.hostel.system.dto.FeeDto;
import com.hostel.system.model.Fee;
import com.hostel.system.model.Student;
import com.hostel.system.model.User;
import com.hostel.system.model.enums.Role;
import com.hostel.system.service.FeeService;
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
@RequestMapping("/fees")
public class FeeController {

    private final FeeService feeService;
    private final StudentService studentService;
    private final UserService userService;

    @Autowired
    public FeeController(FeeService feeService, StudentService studentService, UserService userService) {
        this.feeService = feeService;
        this.studentService = studentService;
        this.userService = userService;
    }

    @GetMapping
    public String listFees(@RequestParam(value = "page", defaultValue = "0") int page,
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
                model.addAttribute("fees", feeService.findByStudentId(studentOpt.get().getId()));
            }
            return "fees/list";
        }

        // ADMIN / WARDEN view all fees
        Page<Fee> feePage = feeService.findAll(PageRequest.of(page, size));
        model.addAttribute("feePage", feePage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", feePage.getTotalPages());
        model.addAttribute("students", studentService.findAll());

        return "fees/list";
    }

    @GetMapping("/generate")
    public String showGenerateFeeForm(Model model) {
        model.addAttribute("feeDto", new FeeDto());
        model.addAttribute("students", studentService.findAll());
        return "fees/generate";
    }

    @PostMapping("/save")
    public String saveFee(@Valid @ModelAttribute("feeDto") FeeDto feeDto,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("students", studentService.findAll());
            return "fees/generate";
        }

        try {
            feeService.generateFee(feeDto);
            redirectAttributes.addFlashAttribute("successMessage", "Hostel fee generated successfully!");
        } catch (Exception ex) {
            model.addAttribute("students", studentService.findAll());
            model.addAttribute("errorMessage", ex.getMessage());
            return "fees/generate";
        }

        return "redirect:/fees";
    }

    @PostMapping("/pay")
    public String payFee(@RequestParam("feeId") Long feeId,
                         @RequestParam(value = "transactionRef", required = false) String transactionRef,
                         RedirectAttributes redirectAttributes) {
        try {
            feeService.payFee(feeId, transactionRef);
            redirectAttributes.addFlashAttribute("successMessage", "Fee payment recorded successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/fees";
    }
}
