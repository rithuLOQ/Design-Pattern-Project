package com.hostel.controller;

import com.hostel.entity.User;
import com.hostel.entity.UserRole;
import com.hostel.patterns.factory.UserFactory;
import com.hostel.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;
    private final UserFactory userFactory;

    public AuthController(UserService userService, UserFactory userFactory) {
        this.userService = userService;
        this.userFactory = userFactory;
    }

    @GetMapping({"/", "/login"})
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "You have been logged out successfully");
        }
        return "login";
    }

    @GetMapping("/default")
    public String defaultAfterLogin(Authentication authentication) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_ADMIN")) {
                return "redirect:/admin/dashboard";
            }
            if (role.equals("ROLE_WARDEN")) {
                return "redirect:/warden/dashboard";
            }
            if (role.equals("ROLE_STUDENT")) {
                return "redirect:/student/dashboard";
            }
        }
        return "redirect:/login?error=true";
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("errorTitle", "Access denied");
        model.addAttribute("errorMessage", "Your profile does not have permission to open this area.");
        return "error";
    }

}
