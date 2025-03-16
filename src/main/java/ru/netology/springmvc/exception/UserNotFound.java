package ru.netology.springmvc.exception;

public class UserNotFound extends RuntimeException {
    public UserNotFound(String msg) {
        super(msg);
    }
}