package com.hostel.system.repository;

import com.hostel.system.model.Warden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WardenRepository extends JpaRepository<Warden, Long> {
}
