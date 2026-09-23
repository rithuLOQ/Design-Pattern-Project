package com.hostel.patterns.command;

import com.hostel.entity.Room;
import com.hostel.entity.Student;

public class AllocateRoomCommand implements Command {

    private final HostelManagementReceiver receiver;
    private final Student student;
    private final Room room;

    public AllocateRoomCommand(HostelManagementReceiver receiver, Student student, Room room) {
        this.receiver = receiver;
        this.student = student;
        this.room = room;
    }

    @Override
    public void execute() {
        receiver.allocateRoom(student, room);
    }
}
