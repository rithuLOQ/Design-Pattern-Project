package com.hostel.service;

import com.hostel.entity.Room;
import com.hostel.entity.RoomStatus;
import com.hostel.entity.Student;
import com.hostel.exception.DuplicateRoomException;
import com.hostel.exception.RoomFullException;
import com.hostel.exception.RoomNotFoundException;
import com.hostel.repository.RoomRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with ID: " + id));
    }

    @Transactional
    public Room createRoom(Room room) {
        if (roomRepository.existsByRoomNumber(room.getRoomNumber())) {
            throw new DuplicateRoomException("Room number already exists: " + room.getRoomNumber());
        }
        room.setOccupiedCount(0);
        room.setStatus(RoomStatus.AVAILABLE);
        return roomRepository.save(room);
    }

    @Transactional
    public Room allocateRoom(Student student, Room room) {
        if (room == null) throw new RoomNotFoundException("Room cannot be null");
        if (student == null) throw new IllegalArgumentException("Student cannot be null");
        if (student.getRoom() != null) {
            throw new IllegalStateException("Student already assigned to a room");
        }
        if (room.getOccupiedCount() >= room.getCapacity()) {
            throw new RoomFullException("Room is full already");
        }
        room.setOccupiedCount(room.getOccupiedCount() + 1);
        if (room.getOccupiedCount() >= room.getCapacity()) {
            room.setStatus(RoomStatus.OCCUPIED);
        } else {
            room.setStatus(RoomStatus.AVAILABLE);
        }
        student.setRoom(room);
        return roomRepository.save(room);
    }

    @Transactional
    public Room vacateRoom(Student student) {
        if (student == null || student.getRoom() == null) {
            throw new RoomNotFoundException("Student is not assigned to any room");
        }
        Room room = student.getRoom();
        room.setOccupiedCount(Math.max(0, room.getOccupiedCount() - 1));
        room.setStatus(room.getOccupiedCount() == 0 ? RoomStatus.AVAILABLE : RoomStatus.OCCUPIED);
        student.setRoom(null);
        return roomRepository.save(room);
    }

    public void deleteRoom(Long id) {
        Room room = getRoomById(id);
        roomRepository.delete(room);
    }

    public Room updateRoom(Room room) {
        Room existing = getRoomById(room.getId());
        if (room.getCapacity() < existing.getOccupiedCount()) {
            throw new IllegalArgumentException("Capacity cannot be lower than current occupancy");
        }
        room.setOccupiedCount(existing.getOccupiedCount());
        room.setStatus(existing.getOccupiedCount() >= room.getCapacity()
                ? RoomStatus.OCCUPIED : RoomStatus.AVAILABLE);
        return roomRepository.save(room);
    }
}
