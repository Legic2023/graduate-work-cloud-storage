package ru.netology.cloudstorage.exceptions;

public class InvalidAuthException extends RuntimeException {
    public InvalidAuthException() {
        super("User authentication failed");
    }
}
