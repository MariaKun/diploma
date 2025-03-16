package ru.netology.springmvc.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import ru.netology.springmvc.entity.User;
import ru.netology.springmvc.jwt.JwtService;
import ru.netology.springmvc.model.JwtAuthenticationResponse;
import ru.netology.springmvc.model.SignInRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static ru.netology.springmvc.TestData.randomUser;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @InjectMocks
    AuthenticationService authenticationService;

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserService userService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void login() {
        User testUser = randomUser();
        SignInRequest signInRequest = new SignInRequest(testUser.getUsername(), testUser.getPassword());
        String token = RandomStringUtils.random(10);

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                signInRequest.getLogin(),
                signInRequest.getPassword()))).thenReturn(new TestingAuthenticationToken("Principal", "Credentials"));
        when(userService.loadUserByUsername(signInRequest.getLogin())).thenReturn(testUser);
        when(jwtService.generateToken(testUser)).thenReturn(token);

        JwtAuthenticationResponse login = authenticationService.login(signInRequest);
        assertEquals(token, login.getToken());
    }
}