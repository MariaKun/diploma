package ru.netology.springmvc.exception;

public class ErrorDeleteFile extends RuntimeException {
    public ErrorDeleteFile(String msg) {
        super(msg);
    }
}
