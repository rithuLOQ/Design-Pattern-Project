package com.hostel.patterns.command;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HostelCommandInvoker {

    private final List<Command> commandHistory = new ArrayList<>();

    public void submit(Command command) {
        commandHistory.add(command);
        command.execute();
    }

    public List<Command> getCommandHistory() {
        return new ArrayList<>(commandHistory);
    }
}
