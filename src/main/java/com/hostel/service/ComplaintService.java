package com.hostel.service;

import com.hostel.entity.Complaint;
import com.hostel.entity.ComplaintStatus;
import com.hostel.exception.ComplaintNotFoundException;
import com.hostel.repository.ComplaintRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;

    public ComplaintService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    public Complaint createComplaint(Complaint complaint) {
        complaint.setStatus(ComplaintStatus.PENDING);
        complaint.setCreatedAt(LocalDateTime.now());
        complaint.setUpdatedAt(LocalDateTime.now());
        return complaintRepository.save(complaint);
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public List<Complaint> getComplaintsByStudentId(Long studentId) {
        return complaintRepository.findByStudentId(studentId);
    }

    public Complaint getComplaintById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new ComplaintNotFoundException("Complaint not found with ID: " + id));
    }

    @Transactional
    public Complaint resolveComplaint(Long complaintId, String newStatus) {
        Complaint complaint = getComplaintById(complaintId);
        ComplaintStatus status;
        try {
            status = ComplaintStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid complaint status");
        }
        complaint.setStatus(status);
        complaint.setResolvedAt(status == ComplaintStatus.RESOLVED ? LocalDateTime.now() : null);
        complaint.setUpdatedAt(LocalDateTime.now());
        return complaintRepository.save(complaint);
    }
}
