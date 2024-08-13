package ru.netology.cloudstorage.exceptions;

public class AuthMessageException extends RuntimeException {
    public AuthMessageException() {
        super("User authentication failed");
    }
}
