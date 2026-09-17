package com.hostel.system.repository;

import com.hostel.system.model.Room;
import com.hostel.system.model.enums.RoomStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByRoomNumber(String roomNumber);

    boolean existsByRoomNumber(String roomNumber);

    List<Room> findByStatus(RoomStatus status);

    @Query("SELECT r FROM Room r WHERE r.status = 'AVAILABLE' AND r.occupiedCount < r.capacity")
    List<Room> findAvailableRooms();

    @Query("SELECT r FROM Room r WHERE " +
           "LOWER(r.roomNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "CAST(r.floor AS string) LIKE CONCAT('%', :keyword, '%') OR " +
           "LOWER(r.roomType) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Room> searchRooms(@Param("keyword") String keyword, Pageable pageable);
}
