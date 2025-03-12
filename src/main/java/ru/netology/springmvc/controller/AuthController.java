package ru.netology.springmvc.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import ru.netology.springmvc.model.SignInRequest;
import ru.netology.springmvc.model.JwtAuthenticationResponse;

import ru.netology.springmvc.model.SignUpRequest;
import ru.netology.springmvc.service.AuthenticationService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    @GetMapping("/all")
    public String test() {
        return "testAll";
    }

    @PostMapping("/pall")
    public String ptest(@RequestBody SignUpRequest request) {
        return "testAll";
    }

    @GetMapping("/logout")
    public String logout() {
        return "testAll";
    }

    @PostMapping("/signup")
    public JwtAuthenticationResponse signUp(@RequestBody SignUpRequest request) {
        return authenticationService.signUp(request);
    }

    @PostMapping("/signin")
    public JwtAuthenticationResponse signIn(@RequestBody SignInRequest request) {
        return authenticationService.signIn(request);
    }
    }
   /* @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User.TokenDto> login(@Valid @RequestBody User.Credentials credentials) {
        User.TokenDto tokenDto = authService.login(credentials);
        return ResponseEntity.ok().body(tokenDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<ResultMessageDto> logout(@RequestHeader("auth-token") Optional<String> headerAuthToken) {
        ResultMessageDto resultMessageDto = authService.logout(headerAuthToken);
        return ResponseEntity.ok().body(resultMessageDto);
    }*/



