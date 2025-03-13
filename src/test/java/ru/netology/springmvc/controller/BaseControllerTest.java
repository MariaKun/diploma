package ru.netology.springmvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.springmvc.entity.Files;
import ru.netology.springmvc.service.FileService;

import java.io.IOException;

public class BaseControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected FileService service;

    protected final String validFilename = "file";

    protected final MockMultipartFile file = new MockMultipartFile(
            validFilename, "test.txt", "text/plain", "text".getBytes());

    protected final long userId1 = 1L;

    protected final Files fileEntity = Files.builder()
            .filecontent(file.getBytes())
            .size((int)file.getSize())
            .filename(file.getName())
            .type(file.getContentType())
            .userid(userId1).build();

    protected final String invalidStr = "";

    public BaseControllerTest() throws IOException {
    }
}
