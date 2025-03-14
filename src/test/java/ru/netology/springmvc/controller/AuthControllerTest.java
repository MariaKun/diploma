package ru.netology.springmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import ru.netology.springmvc.entity.Role;
import ru.netology.springmvc.entity.User;
import ru.netology.springmvc.model.FileNameEditRequest;
import ru.netology.springmvc.model.JwtAuthenticationResponse;
import ru.netology.springmvc.model.SignInRequest;
import ru.netology.springmvc.service.AuthenticationService;
import ru.netology.springmvc.service.FileService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected AuthenticationService authenticationService;

    private final User testUser = new User(1L, "test", "123", "test@test.com", Role.ROLE_USER);

    @Test
    void login() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        SignInRequest signInRequest = new SignInRequest(testUser.getUsername(), testUser.getPassword());
        String json = mapper.writeValueAsString(signInRequest);

        JwtAuthenticationResponse expectedResponse = new JwtAuthenticationResponse(RandomStringUtils.randomAlphabetic(5));
        when(authenticationService.login(signInRequest)).thenReturn(expectedResponse);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        JwtAuthenticationResponse response = mapper.readValue(mvcResult.getResponse().getContentAsString(), JwtAuthenticationResponse.class);
        assertEquals(expectedResponse.getToken(), response.getToken());
    }
}