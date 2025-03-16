package ru.netology.springmvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.springmvc.entity.File;
import ru.netology.springmvc.entity.User;
import ru.netology.springmvc.jwt.JwtService;
import ru.netology.springmvc.service.AuthenticationService;
import ru.netology.springmvc.service.FileService;
import ru.netology.springmvc.service.UserService;

import java.io.IOException;

import static org.mockito.Mockito.when;
import static ru.netology.springmvc.TestData.randomFile;
import static ru.netology.springmvc.TestData.randomUser;

@SpringBootTest
@AutoConfigureMockMvc
public class BaseControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected AuthenticationService authenticationService;

    @MockBean
    protected FileService service;

    @MockBean
    protected UserService userService;

    @Autowired
    protected JwtService jwtService;

    protected final User testUser = randomUser();

    protected final MockMultipartFile testFile = randomFile();

    protected final File fileEntity = File.builder()
            .filecontent(testFile.getBytes())
            .size((int) testFile.getSize())
            .filename(testFile.getName())
            .type(testFile.getContentType())
            .userid(testUser.getId()).build();

    public BaseControllerTest() throws IOException {
    }

    protected String getToken(User testUser) {
        String token = "Bearer " + jwtService.generateToken(testUser);
        when(userService.loadUserByUsername(testUser.getUsername())).thenReturn(testUser);
        when(userService.getCurrentUser()).thenReturn(testUser);
        return token;
    }
}
