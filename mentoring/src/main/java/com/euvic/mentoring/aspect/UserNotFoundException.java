package com.euvic.mentoring.aspect;

public class UserNotFoundException extends RuntimeException {

    private int userId;

    public UserNotFoundException() {
        super("User not found");
    }

    public UserNotFoundException(int userId) {
        super("User with specified id not found: " + userId);
        this.userId = userId;

    }
}
