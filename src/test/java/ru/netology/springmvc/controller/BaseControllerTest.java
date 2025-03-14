package ru.netology.springmvc.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.springmvc.entity.Files;
import ru.netology.springmvc.entity.Role;
import ru.netology.springmvc.entity.User;
import ru.netology.springmvc.jwt.JwtService;
import ru.netology.springmvc.service.AuthenticationService;
import ru.netology.springmvc.service.FileService;
import ru.netology.springmvc.service.UserService;

import java.io.IOException;

import static org.mockito.Mockito.when;

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

    protected String token;
    protected final User testUser = new User(3L, "Jane23", "123", "re@d", Role.ROLE_USER);
    protected final String headerName = "auth-token";
    protected final String bearer = "Bearer ";
    protected final String validFilename = "file.txt";

    protected final MockMultipartFile file = new MockMultipartFile(
            validFilename, "file.txt", "text/plain", "text".getBytes());

    protected final Files fileEntity = Files.builder()
            .filecontent(file.getBytes())
            .size((int)file.getSize())
            .filename(file.getName())
            .type(file.getContentType())
            .userid(testUser.getId()).build();

    protected final String invalidStr = "qwerty";

    public BaseControllerTest() throws IOException {
    }

    @BeforeEach
    public void getToken()
    {
        token = bearer + jwtService.generateToken(testUser);
        when(userService.loadUserByUsername(testUser.getUsername())).thenReturn(testUser);
        when(userService.getCurrentUser()).thenReturn(testUser);
    }
}
