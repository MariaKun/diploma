package ru.netology.springmvc.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.springmvc.model.FileDTO;
import ru.netology.springmvc.service.FileService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/list")
public class ListController {

    private final FileService fileService;

    @GetMapping
    List<FileDTO> getAllFiles(@RequestParam("limit") int limit) {
        return fileService.getAllFiles(1L, limit);
    }
}
