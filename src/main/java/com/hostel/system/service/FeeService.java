package com.hostel.system.service;

import com.hostel.system.dto.FeeDto;
import com.hostel.system.model.Fee;
import com.hostel.system.model.enums.FeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FeeService {

    Fee generateFee(FeeDto feeDto);

    Fee payFee(Long feeId, String transactionRef);

    Optional<Fee> findById(Long id);

    List<Fee> findByStudentId(Long studentId);

    Page<Fee> findAll(Pageable pageable);

    long countPendingFees();
}
