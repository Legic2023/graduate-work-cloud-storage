package ru.netology.cloudstorage.exceptions;

public class InvalidUploadFile extends RuntimeException {
    public InvalidUploadFile() {
        super("File: upload FAILED!");
    }
}
