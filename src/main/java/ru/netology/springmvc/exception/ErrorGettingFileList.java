package ru.netology.springmvc.exception;

public class ErrorGettingFileList extends RuntimeException {
    public ErrorGettingFileList(String msg) {
        super(msg);
    }
}
