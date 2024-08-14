package ru.netology.cloudstorage.exceptions;

public class InvalidEditFile extends RuntimeException{
    public InvalidEditFile() {
        super("File: edit FAILED!");
    }
}
