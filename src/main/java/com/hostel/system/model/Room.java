package com.hostel.system.model;

import com.hostel.system.model.enums.RoomStatus;
import com.hostel.system.model.enums.RoomType;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_number", nullable = false, unique = true, length = 20)
    private String roomNumber;

    @Column(nullable = false)
    private Integer floor;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "occupied_count", nullable = false)
    private Integer occupiedCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false, length = 20)
    private RoomType roomType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoomStatus status = RoomStatus.AVAILABLE;

    @OneToMany(mappedBy = "room", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Student> students = new ArrayList<>();

    public Room() {
    }

    public Room(String roomNumber, Integer floor, Integer capacity, RoomType roomType, RoomStatus status) {
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.capacity = capacity;
        this.roomType = roomType;
        this.status = status != null ? status : RoomStatus.AVAILABLE;
        this.occupiedCount = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getOccupiedCount() {
        return occupiedCount;
    }

    public void setOccupiedCount(Integer occupiedCount) {
        this.occupiedCount = occupiedCount;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    /**
     * Recalculates and updates room availability status based on current capacity and occupied count.
     */
    public void updateStatus() {
        if (this.occupiedCount >= this.capacity) {
            this.status = RoomStatus.FULL;
        } else if (this.status != RoomStatus.MAINTENANCE) {
            this.status = RoomStatus.AVAILABLE;
        }
    }
}
