package ru.netology.cloudstorage.exceptions;

public class InvalidFindFile extends RuntimeException{
    public InvalidFindFile() {
        super("File: search FAILED!");
    }
}
