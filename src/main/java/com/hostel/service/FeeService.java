package com.hostel.service;

import com.hostel.entity.Fee;
import com.hostel.entity.FeeStatus;
import com.hostel.entity.Student;
import com.hostel.exception.FeeNotFoundException;
import com.hostel.repository.FeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class FeeService {

    private final FeeRepository feeRepository;

    public FeeService(FeeRepository feeRepository) {
        this.feeRepository = feeRepository;
    }

    @Transactional
    public Fee generateFee(Student student, BigDecimal amount, LocalDate dueDate) {
        if (student == null) {
            throw new IllegalArgumentException("Student is required");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Fee amount must be greater than zero");
        }
        if (dueDate == null || dueDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Due date must be valid");
        }

        Fee fee = new Fee();
        fee.setStudent(student);
        fee.setAmount(amount);
        fee.setDueDate(dueDate);
        fee.setStatus(FeeStatus.PENDING);
        return feeRepository.save(fee);
    }

    public List<Fee> getAllFees() {
        return feeRepository.findAll();
    }

    public List<Fee> getFeesByStudentId(Long studentId) {
        return feeRepository.findByStudentId(studentId);
    }

    public Fee getFeeById(Long id) {
        return feeRepository.findById(id)
                .orElseThrow(() -> new FeeNotFoundException("Fee not found with ID: " + id));
    }

    @Transactional
    public Fee markPaid(Long feeId) {
        Fee fee = getFeeById(feeId);
        fee.setStatus(FeeStatus.PAID);
        return feeRepository.save(fee);
    }
}
