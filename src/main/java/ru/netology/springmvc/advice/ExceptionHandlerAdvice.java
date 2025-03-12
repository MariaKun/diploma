package ru.netology.springmvc.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.springmvc.exception.*;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidCredentials.class)
    public String handleBadCredentials(InvalidCredentials ex) {
        return ex.getMessage();
    }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedUser.class)
    public String handleUnauthorizedUser(UnauthorizedUser ex) {
        return ex.getMessage();
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ErrorDeleteFile.class)
    public String handleErrorDeleteFile(ErrorDeleteFile ex) {
        return ex.getMessage();
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ErrorGettingFileList.class)
    public String handleErrorGettingFileList(ErrorGettingFileList ex) {
        return ex.getMessage();
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ErrorInputData.class)
    public String handleErrorInputData(ErrorInputData ex) {
        return ex.getMessage();
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ErrorUploadFile.class)
    public String handleErrorUploadFile(ErrorUploadFile ex) {
        return ex.getMessage();
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ErrorDownloadFile.class)
    public String handleErrorDownloadFile(ErrorDownloadFile ex) {
        return ex.getMessage();
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(FileNotFound.class)
    public String handleFileNotFound(FileNotFound ex) {
        return ex.getMessage();
    }
}
