package com.hostel.system.service;

import com.hostel.system.dto.ComplaintDto;
import com.hostel.system.model.Complaint;
import com.hostel.system.model.enums.ComplaintStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ComplaintService {

    Complaint raiseComplaint(Long studentId, ComplaintDto complaintDto);

    Complaint updateComplaintStatus(Long complaintId, ComplaintStatus status, String resolutionNote);

    Optional<Complaint> findById(Long id);

    List<Complaint> findByStudentId(Long studentId);

    Page<Complaint> findAll(Pageable pageable);

    Page<Complaint> findByStatus(ComplaintStatus status, Pageable pageable);

    long countPendingComplaints();
}
