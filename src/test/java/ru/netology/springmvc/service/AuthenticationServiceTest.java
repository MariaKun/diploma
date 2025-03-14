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
import ru.netology.springmvc.entity.Role;
import ru.netology.springmvc.entity.User;
import ru.netology.springmvc.jwt.JwtService;
import ru.netology.springmvc.model.JwtAuthenticationResponse;
import ru.netology.springmvc.model.SignInRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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

    private final User testUser = new User(1L, "test", "123", "test@test.com", Role.ROLE_USER);

    @Test
    void login() {
        SignInRequest signInRequest = new SignInRequest(testUser.getUsername(), testUser.getPassword());

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        signInRequest.getLogin(),
                        signInRequest.getPassword()))).thenReturn(new TestingAuthenticationToken("Principal", "Credentials"));
        when(userService.loadUserByUsername(signInRequest.getLogin())).thenReturn(testUser);
        when(jwtService.generateToken(testUser)).thenReturn(RandomStringUtils.random(10));

        JwtAuthenticationResponse login = authenticationService.login(signInRequest);
        assertNotNull(login.getToken());
    }
}