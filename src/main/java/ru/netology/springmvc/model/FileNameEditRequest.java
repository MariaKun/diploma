package ru.netology.springmvc.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class FileNameEditRequest {
    @NotBlank(message = "Имя файла не может быть пустыми")
    private String filename;
}
