package com.hostel.system.service.impl;

import com.hostel.system.dto.FeeDto;
import com.hostel.system.exception.ResourceNotFoundException;
import com.hostel.system.model.Fee;
import com.hostel.system.model.Student;
import com.hostel.system.model.enums.FeeStatus;
import com.hostel.system.repository.FeeRepository;
import com.hostel.system.repository.StudentRepository;
import com.hostel.system.service.FeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FeeServiceImpl implements FeeService {

    private final FeeRepository feeRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public FeeServiceImpl(FeeRepository feeRepository, StudentRepository studentRepository) {
        this.feeRepository = feeRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public Fee generateFee(FeeDto feeDto) {
        Student student = studentRepository.findById(feeDto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + feeDto.getStudentId()));

        Fee fee = new Fee(student, feeDto.getAmount(), feeDto.getDueDate(), FeeStatus.UNPAID);
        return feeRepository.save(fee);
    }

    @Override
    @Transactional
    public Fee payFee(Long feeId, String transactionRef) {
        Fee fee = feeRepository.findById(feeId)
                .orElseThrow(() -> new ResourceNotFoundException("Fee record not found with ID: " + feeId));

        if (fee.getStatus() == FeeStatus.PAID) {
            throw new IllegalArgumentException("Fee record is already PAID!");
        }

        fee.setStatus(FeeStatus.PAID);
        fee.setPaymentDate(LocalDate.now());
        fee.setTransactionRef(transactionRef != null && !transactionRef.trim().isEmpty() 
                ? transactionRef.trim() 
                : "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        return feeRepository.save(fee);
    }

    @Override
    public Optional<Fee> findById(Long id) {
        return feeRepository.findById(id);
    }

    @Override
    public List<Fee> findByStudentId(Long studentId) {
        return feeRepository.findByStudentIdOrderByDueDateDesc(studentId);
    }

    @Override
    public Page<Fee> findAll(Pageable pageable) {
        return feeRepository.findAll(pageable);
    }

    @Override
    public long countPendingFees() {
        return feeRepository.countByStatus(FeeStatus.UNPAID) + feeRepository.countByStatus(FeeStatus.OVERDUE);
    }
}
