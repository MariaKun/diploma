package ru.netology.springmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import ru.netology.springmvc.entity.User;
import ru.netology.springmvc.model.JwtAuthenticationResponse;
import ru.netology.springmvc.model.SignInRequest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.netology.springmvc.TestData.loginUri;
import static ru.netology.springmvc.TestData.randomUser;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest extends BaseControllerTest {

    public AuthControllerTest() throws IOException {
    }

    @Test
    void login() throws Exception {
        User testUser = randomUser();
        ObjectMapper mapper = new ObjectMapper();
        SignInRequest signInRequest = new SignInRequest(testUser.getUsername(), testUser.getPassword());
        String json = mapper.writeValueAsString(signInRequest);

        JwtAuthenticationResponse expectedResponse = new JwtAuthenticationResponse(RandomStringUtils.randomAlphabetic(5));
        when(authenticationService.login(signInRequest)).thenReturn(expectedResponse);

        RequestBuilder request = MockMvcRequestBuilders
                .post(loginUri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        JwtAuthenticationResponse response = mapper.readValue(mvcResult.getResponse().getContentAsString(), JwtAuthenticationResponse.class);
        assertEquals(expectedResponse.getToken(), response.getToken());
        verify(authenticationService).login(signInRequest);
    }
}