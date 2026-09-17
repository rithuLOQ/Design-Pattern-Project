package com.hostel.system.controller;

import com.hostel.system.dto.RoomDto;
import com.hostel.system.exception.ResourceNotFoundException;
import com.hostel.system.model.Room;
import com.hostel.system.model.enums.RoomStatus;
import com.hostel.system.model.enums.RoomType;
import com.hostel.system.service.RoomService;
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
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public String listRooms(@RequestParam(value = "keyword", required = false) String keyword,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "10") int size,
                            Model model) {
        Page<Room> roomPage = roomService.searchRooms(keyword, PageRequest.of(page, size));

        model.addAttribute("roomPage", roomPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", roomPage.getTotalPages());

        return "rooms/list";
    }

    @GetMapping("/new")
    public String showAddRoomForm(Model model) {
        model.addAttribute("roomDto", new RoomDto());
        model.addAttribute("roomTypes", RoomType.values());
        model.addAttribute("roomStatuses", RoomStatus.values());
        model.addAttribute("isEdit", false);
        return "rooms/form";
    }

    @PostMapping("/save")
    public String saveRoom(@Valid @ModelAttribute("roomDto") RoomDto roomDto,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roomTypes", RoomType.values());
            model.addAttribute("roomStatuses", RoomStatus.values());
            model.addAttribute("isEdit", roomDto.getId() != null);
            return "rooms/form";
        }

        try {
            if (roomDto.getId() == null) {
                roomService.addRoom(roomDto);
                redirectAttributes.addFlashAttribute("successMessage", "Room added successfully!");
            } else {
                roomService.updateRoom(roomDto.getId(), roomDto);
                redirectAttributes.addFlashAttribute("successMessage", "Room updated successfully!");
            }
        } catch (Exception ex) {
            model.addAttribute("roomTypes", RoomType.values());
            model.addAttribute("roomStatuses", RoomStatus.values());
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("isEdit", roomDto.getId() != null);
            return "rooms/form";
        }

        return "redirect:/rooms";
    }

    @GetMapping("/edit/{id}")
    public String showEditRoomForm(@PathVariable("id") Long id, Model model) {
        Room room = roomService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + id));

        RoomDto dto = new RoomDto();
        dto.setId(room.getId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setFloor(room.getFloor());
        dto.setCapacity(room.getCapacity());
        dto.setOccupiedCount(room.getOccupiedCount());
        dto.setRoomType(room.getRoomType());
        dto.setStatus(room.getStatus());

        model.addAttribute("roomDto", dto);
        model.addAttribute("roomTypes", RoomType.values());
        model.addAttribute("roomStatuses", RoomStatus.values());
        model.addAttribute("isEdit", true);

        return "rooms/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteRoom(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            roomService.deleteRoom(id);
            redirectAttributes.addFlashAttribute("successMessage", "Room deleted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/rooms";
    }
}
