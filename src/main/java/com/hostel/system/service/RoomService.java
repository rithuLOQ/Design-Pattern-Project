package com.hostel.system.service;

import com.hostel.system.dto.RoomDto;
import com.hostel.system.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RoomService {

    Room addRoom(RoomDto roomDto);

    Room updateRoom(Long id, RoomDto roomDto);

    void deleteRoom(Long id);

    Optional<Room> findById(Long id);

    List<Room> findAll();

    List<Room> findAvailableRooms();

    Page<Room> searchRooms(String keyword, Pageable pageable);

    long countTotalRooms();

    long countOccupiedRooms();

    long countAvailableRooms();
}
