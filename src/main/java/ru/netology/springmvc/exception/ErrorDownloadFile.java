package ru.netology.springmvc.exception;

public class ErrorDownloadFile extends RuntimeException {
    public ErrorDownloadFile(String msg) {
        super(msg);
    }
}
