package com.hostel.service;

import com.hostel.entity.Fee;
import com.hostel.entity.Payment;
import com.hostel.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final FeeService feeService;

    public PaymentService(PaymentRepository paymentRepository, FeeService feeService) {
        this.paymentRepository = paymentRepository;
        this.feeService = feeService;
    }

    public Payment recordPayment(Fee fee, BigDecimal amount, String note) {
        if (fee == null) {
            throw new IllegalArgumentException("Fee is required");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }
        if (amount.compareTo(fee.getAmount()) > 0) {
            throw new IllegalArgumentException("Payment cannot exceed the fee amount");
        }

        Payment payment = new Payment();
        payment.setFee(fee);
        payment.setAmount(amount);
        payment.setNote(note);
        Payment saved = paymentRepository.save(payment);
        feeService.markPaid(fee.getId());
        return saved;
    }
}
