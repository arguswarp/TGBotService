package com.argus.service.enums;

public enum ServiceCommands {
    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start");
    private final String command;
    ServiceCommands(String command) {
        this.command = command;
    }

    public boolean equals(String command) {
        return this.command.equals(command);
    }
}
