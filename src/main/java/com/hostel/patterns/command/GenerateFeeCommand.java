package com.hostel.patterns.command;

import com.hostel.entity.Student;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GenerateFeeCommand implements Command {

    private final HostelManagementReceiver receiver;
    private final Student student;
    private final BigDecimal amount;
    private final LocalDate dueDate;

    public GenerateFeeCommand(HostelManagementReceiver receiver, Student student, BigDecimal amount, LocalDate dueDate) {
        this.receiver = receiver;
        this.student = student;
        this.amount = amount;
        this.dueDate = dueDate;
    }

    @Override
    public void execute() {
        receiver.generateFee(student, amount, dueDate);
    }
}
