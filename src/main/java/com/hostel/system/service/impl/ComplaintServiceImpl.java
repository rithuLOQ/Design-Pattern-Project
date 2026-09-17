package com.hostel.system.service.impl;

import com.hostel.system.dto.ComplaintDto;
import com.hostel.system.exception.ResourceNotFoundException;
import com.hostel.system.model.Complaint;
import com.hostel.system.model.Student;
import com.hostel.system.model.enums.ComplaintStatus;
import com.hostel.system.repository.ComplaintRepository;
import com.hostel.system.repository.StudentRepository;
import com.hostel.system.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public ComplaintServiceImpl(ComplaintRepository complaintRepository, StudentRepository studentRepository) {
        this.complaintRepository = complaintRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public Complaint raiseComplaint(Long studentId, ComplaintDto complaintDto) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        Complaint complaint = new Complaint(student, complaintDto.getTitle(), complaintDto.getDescription());
        return complaintRepository.save(complaint);
    }

    @Override
    @Transactional
    public Complaint updateComplaintStatus(Long complaintId, ComplaintStatus status, String resolutionNote) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with ID: " + complaintId));

        complaint.setStatus(status);
        if (status == ComplaintStatus.RESOLVED) {
            complaint.setResolvedAt(LocalDateTime.now());
        }
        if (resolutionNote != null && !resolutionNote.trim().isEmpty()) {
            complaint.setResolutionNote(resolutionNote.trim());
        }

        return complaintRepository.save(complaint);
    }

    @Override
    public Optional<Complaint> findById(Long id) {
        return complaintRepository.findById(id);
    }

    @Override
    public List<Complaint> findByStudentId(Long studentId) {
        return complaintRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
    }

    @Override
    public Page<Complaint> findAll(Pageable pageable) {
        return complaintRepository.findAll(pageable);
    }

    @Override
    public Page<Complaint> findByStatus(ComplaintStatus status, Pageable pageable) {
        return complaintRepository.findByStatus(status, pageable);
    }

    @Override
    public long countPendingComplaints() {
        return complaintRepository.countByStatus(ComplaintStatus.PENDING);
    }
}
