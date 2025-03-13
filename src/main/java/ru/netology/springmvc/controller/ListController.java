package ru.netology.springmvc.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.springmvc.model.FileDTO;
import ru.netology.springmvc.service.FileService;
import ru.netology.springmvc.service.UserService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/list")
public class ListController {

    private final FileService fileService;
    private final UserService userService;

    @GetMapping
    List<FileDTO> getAllFiles(@RequestParam("limit") int limit) {
        return fileService.getAllFiles(userService.getCurrentUser().getId(), limit);
    }
}
