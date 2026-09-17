package com.hostel.system.repository;

import com.hostel.system.model.Complaint;
import com.hostel.system.model.enums.ComplaintStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    Page<Complaint> findByStudentId(Long studentId, Pageable pageable);

    long countByStatus(ComplaintStatus status);

    Page<Complaint> findByStatus(ComplaintStatus status, Pageable pageable);
}
