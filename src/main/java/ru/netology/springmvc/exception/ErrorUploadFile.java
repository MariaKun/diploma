package ru.netology.springmvc.exception;

public class ErrorUploadFile extends RuntimeException {
    public ErrorUploadFile(String msg) {
        super(msg);
    }
}
