package com.hostel.patterns.command;

/**
 * Command pattern.
 *
 * Interface: Command defines the contract for actions.
 * Concrete commands carry work to receivers for execution.
 */
public interface Command {
    void execute();
}
