package com.hostel.system.repository;

import com.hostel.system.model.Fee;
import com.hostel.system.model.enums.FeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Long> {

    List<Fee> findByStudentIdOrderByDueDateDesc(Long studentId);

    Page<Fee> findByStudentId(Long studentId, Pageable pageable);

    long countByStatus(FeeStatus status);

    List<Fee> findByStatus(FeeStatus status);
}
