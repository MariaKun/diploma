package ru.netology.springmvc.controller;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.com.google.common.reflect.TypeToken;
import ru.netology.springmvc.entity.Role;
import ru.netology.springmvc.entity.User;
import ru.netology.springmvc.exception.ErrorInputData;
import ru.netology.springmvc.jwt.JwtService;
import ru.netology.springmvc.model.FileDTO;
import ru.netology.springmvc.service.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ListControllerTest extends BaseControllerTest {

    ListControllerTest() throws IOException { }

    @Test
    void getAllFiles() throws Exception {
        int limit = 100;
        RequestBuilder request = MockMvcRequestBuilders
                .get("/list")
                .header(headerName, token)
                .param("limit", String.valueOf(limit));

        List<FileDTO> expectedFileDTOList = new ArrayList<>();
        FileDTO fileDTO = new FileDTO(fileEntity.getFilename(), fileEntity.getSize());
        expectedFileDTOList.add(fileDTO);

        when(service.getAllFiles(testUser.getId(), limit)).thenReturn(expectedFileDTOList);

        MvcResult result = this.mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        TypeToken<List<FileDTO>> typeToken = new TypeToken<>(){};
        List<FileDTO> fileDTOList = new Gson().fromJson(result.getResponse().getContentAsString(), typeToken.getType());
        assertEquals(expectedFileDTOList, fileDTOList);
        verify(service, times(1)).getAllFiles(testUser.getId(), limit);
    }

    @Test
    void getAllFiles_invalidLimit() throws Exception {
        int limit = 0;

        when(service.getAllFiles(testUser.getId(), limit)).thenThrow(ErrorInputData.class);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/list")
                .header(headerName, token)
                .param("limit", String.valueOf(limit));
        this.mockMvc.perform(request).andExpect(status().isBadRequest());
        verify(service, times(1)).getAllFiles(testUser.getId(), limit);
    }
}