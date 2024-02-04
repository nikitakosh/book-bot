package com.nikitakosh.BookBot.exceptions;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String cause) {
        super(cause);
    }
}
