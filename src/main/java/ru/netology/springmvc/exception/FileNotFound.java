package ru.netology.springmvc.exception;

public class FileNotFound extends RuntimeException {
    public FileNotFound(String msg) {
        super(msg);
    }
}
