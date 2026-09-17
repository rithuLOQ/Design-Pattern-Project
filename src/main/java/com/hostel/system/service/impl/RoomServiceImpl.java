package com.hostel.system.service.impl;

import com.hostel.system.dto.RoomDto;
import com.hostel.system.exception.ResourceNotFoundException;
import com.hostel.system.model.Room;
import com.hostel.system.model.enums.RoomStatus;
import com.hostel.system.repository.RoomRepository;
import com.hostel.system.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    @Transactional
    public Room addRoom(RoomDto roomDto) {
        if (roomRepository.existsByRoomNumber(roomDto.getRoomNumber())) {
            throw new IllegalArgumentException("Room Number '" + roomDto.getRoomNumber() + "' already exists!");
        }

        Room room = new Room(
                roomDto.getRoomNumber(),
                roomDto.getFloor(),
                roomDto.getCapacity(),
                roomDto.getRoomType(),
                roomDto.getStatus() != null ? roomDto.getStatus() : RoomStatus.AVAILABLE
        );

        return roomRepository.save(room);
    }

    @Override
    @Transactional
    public Room updateRoom(Long id, RoomDto roomDto) {
        Room existing = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + id));

        existing.setRoomNumber(roomDto.getRoomNumber());
        existing.setFloor(roomDto.getFloor());
        existing.setCapacity(roomDto.getCapacity());
        existing.setRoomType(roomDto.getRoomType());
        if (roomDto.getStatus() != null) {
            existing.setStatus(roomDto.getStatus());
        }
        existing.updateStatus();

        return roomRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + id));

        if (!room.getStudents().isEmpty()) {
            throw new IllegalStateException("Cannot delete Room " + room.getRoomNumber() + " while students are allocated to it!");
        }

        roomRepository.delete(room);
    }

    @Override
    public Optional<Room> findById(Long id) {
        return roomRepository.findById(id);
    }

    @Override
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    @Override
    public List<Room> findAvailableRooms() {
        return roomRepository.findAvailableRooms();
    }

    @Override
    public Page<Room> searchRooms(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return roomRepository.searchRooms(keyword.trim(), pageable);
        }
        return roomRepository.findAll(pageable);
    }

    @Override
    public long countTotalRooms() {
        return roomRepository.count();
    }

    @Override
    public long countOccupiedRooms() {
        return roomRepository.findAll().stream()
                .filter(r -> r.getOccupiedCount() > 0)
                .count();
    }

    @Override
    public long countAvailableRooms() {
        return roomRepository.findByStatus(RoomStatus.AVAILABLE).size();
    }
}
