package com.hostel.patterns.command;

public class ResolveComplaintCommand implements Command {

    private final HostelManagementReceiver receiver;
    private final Long complaintId;
    private final String status;

    public ResolveComplaintCommand(HostelManagementReceiver receiver, Long complaintId, String status) {
        this.receiver = receiver;
        this.complaintId = complaintId;
        this.status = status;
    }

    @Override
    public void execute() {
        receiver.resolveComplaint(complaintId, status);
    }
}
