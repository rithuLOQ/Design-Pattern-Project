package com.hostel.patterns.command;

import com.hostel.entity.Student;

public class VacateRoomCommand implements Command {

    private final HostelManagementReceiver receiver;
    private final Student student;

    public VacateRoomCommand(HostelManagementReceiver receiver, Student student) {
        this.receiver = receiver;
        this.student = student;
    }

    @Override
    public void execute() {
        receiver.vacateRoom(student);
    }
}
