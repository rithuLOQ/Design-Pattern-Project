package com.hostel.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            DuplicateStudentException.class,
            DuplicateRoomException.class,
            RoomFullException.class,
            StudentNotFoundException.class,
            RoomNotFoundException.class,
            ComplaintNotFoundException.class,
            FeeNotFoundException.class,
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public String handleBusinessError(RuntimeException exception, Model model) {
        model.addAttribute("errorTitle", "Action could not be completed");
        model.addAttribute("errorMessage", exception.getMessage());
        return "error";
    }
}
