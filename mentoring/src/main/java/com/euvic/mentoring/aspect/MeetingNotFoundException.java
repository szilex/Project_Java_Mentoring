package com.euvic.mentoring.aspect;

public class MeetingNotFoundException extends RuntimeException {

    private int userId;

    public MeetingNotFoundException() {
        super("Meeting not found");
    }

    public MeetingNotFoundException(int userId) {
        super("Meeting with specified id not found: " + userId);
        this.userId = userId;

    }
}
