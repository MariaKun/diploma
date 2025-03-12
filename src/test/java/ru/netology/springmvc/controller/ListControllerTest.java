package ru.netology.springmvc.controller;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.com.google.common.reflect.TypeToken;
import ru.netology.springmvc.model.FileDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ListController.class)
class ListControllerTest extends BaseControllerTest {

    ListControllerTest() throws IOException {
    }

    @Test
    void getAllFiles() throws Exception {
        List<FileDTO> fileDTOList = new ArrayList<>();
        FileDTO fileDTO = new FileDTO(fileEntity.getFilename(), fileEntity.getSize());
        fileDTOList.add(fileDTO);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/list")
                .param("limit", "100");
        when(service.getAllFiles(userId1, 100)).thenReturn(fileDTOList);
        MvcResult result = this.mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TypeToken<List<FileDTO>> typeToken = new TypeToken<>() {
        };
        List<FileDTO> fileDTOList1 = new Gson().fromJson(json, typeToken.getType());
        assertEquals(fileDTOList1, fileDTOList);
    }
}