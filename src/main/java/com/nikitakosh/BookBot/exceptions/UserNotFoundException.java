package com.nikitakosh.BookBot.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String cause) {
        super(cause);
    }
}
