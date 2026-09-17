package com.hostel.system.controller;

import com.hostel.system.dto.StudentRegistrationDto;
import com.hostel.system.exception.ResourceNotFoundException;
import com.hostel.system.model.Student;
import com.hostel.system.service.RoomService;
import com.hostel.system.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final RoomService roomService;

    @Autowired
    public StudentController(StudentService studentService, RoomService roomService) {
        this.studentService = studentService;
        this.roomService = roomService;
    }

    @GetMapping
    public String listStudents(@RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "10") int size,
                               Model model) {
        Page<Student> studentPage = studentService.searchStudents(keyword, PageRequest.of(page, size));
        
        model.addAttribute("studentPage", studentPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        model.addAttribute("availableRooms", roomService.findAvailableRooms());
        
        return "students/list";
    }

    @GetMapping("/new")
    public String showRegistrationForm(Model model) {
        model.addAttribute("studentDto", new StudentRegistrationDto());
        model.addAttribute("availableRooms", roomService.findAvailableRooms());
        model.addAttribute("isEdit", false);
        return "students/form";
    }

    @PostMapping("/save")
    public String saveStudent(@Valid @ModelAttribute("studentDto") StudentRegistrationDto studentDto,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("availableRooms", roomService.findAvailableRooms());
            model.addAttribute("isEdit", studentDto.getId() != null);
            return "students/form";
        }

        try {
            if (studentDto.getId() == null) {
                studentService.registerStudent(studentDto);
                redirectAttributes.addFlashAttribute("successMessage", "Student registered successfully using StudentBuilder (GoF Builder Pattern)!");
            } else {
                studentService.updateStudent(studentDto.getId(), studentDto);
                redirectAttributes.addFlashAttribute("successMessage", "Student updated successfully!");
            }
        } catch (Exception ex) {
            model.addAttribute("availableRooms", roomService.findAvailableRooms());
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("isEdit", studentDto.getId() != null);
            return "students/form";
        }

        return "redirect:/students";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Student student = studentService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        StudentRegistrationDto dto = new StudentRegistrationDto();
        dto.setId(student.getId());
        dto.setUsername(student.getUsername());
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setPhone(student.getPhone());
        dto.setRegistrationNumber(student.getRegistrationNumber());
        dto.setDepartment(student.getDepartment());
        dto.setYear(student.getYear());
        dto.setGender(student.getGender());
        dto.setAddress(student.getAddress());
        dto.setParentName(student.getParentName());
        dto.setParentPhone(student.getParentPhone());
        dto.setBloodGroup(student.getBloodGroup());
        dto.setMedicalCondition(student.getMedicalCondition());
        if (student.getRoom() != null) {
            dto.setRoomId(student.getRoom().getId());
        }

        model.addAttribute("studentDto", dto);
        model.addAttribute("availableRooms", roomService.findAvailableRooms());
        model.addAttribute("isEdit", true);
        return "students/form";
    }

    @GetMapping("/view/{id}")
    public String viewStudent(@PathVariable("id") Long id, Model model) {
        Student student = studentService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        model.addAttribute("student", student);
        return "students/view";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("successMessage", "Student deleted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/students";
    }

    @PostMapping("/allocate")
    public String allocateRoom(@RequestParam("studentId") Long studentId,
                               @RequestParam("roomId") Long roomId,
                               RedirectAttributes redirectAttributes) {
        try {
            studentService.allocateRoom(studentId, roomId);
            redirectAttributes.addFlashAttribute("successMessage", "Room allocated successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/students";
    }

    @PostMapping("/vacate")
    public String vacateRoom(@RequestParam("studentId") Long studentId,
                             RedirectAttributes redirectAttributes) {
        try {
            studentService.vacateRoom(studentId);
            redirectAttributes.addFlashAttribute("successMessage", "Room vacated successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/students";
    }
}
