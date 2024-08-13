package ru.netology.cloudstorage.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.netology.cloudstorage.dtos.FileNameInRequest;
import ru.netology.cloudstorage.services.AuthService;
import ru.netology.cloudstorage.services.StorageServiceImpl;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
@WebMvcTest(StorageController.class)
class StorageControllerTest {

    private static final String ENDPOINT_FILES = "/files";
    private static final String ENDPOINT_LIST = "/list";
    private static final String FILENAME_PARAM = "filename";
    private static final String FILENAME_UPLOAD = "Book1.txt";
    private static final String FILENAME_EDIT = "Book1_rev2.txt";
    private static final String PASSWORD_VALUE = "12345678";
    private static final String AUTH_HEADER = "auth-token";
    private static final String BEARER = "Bearer ";
    private static final MediaType CONTENT_TYPE = MediaType.APPLICATION_JSON;
    private static final String CONTENT_TYPE_VAL = "application/json";
    private static final String CHARSET = "UTF-8";


    @MockBean
    StorageServiceImpl mockStorageServiceImpl;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthService authService;

    @Autowired
    StorageController storageController;

    @Autowired
    ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(storageController).build();
    }

    @Test
    void testUploadFile() throws Exception {

        mockMvc.perform(post(ENDPOINT_FILES)
                        .header(AUTH_HEADER, BEARER + PASSWORD_VALUE)
                        .param(FILENAME_PARAM, FILENAME_UPLOAD)
                        .contentType(CONTENT_TYPE)
                        .content(mockMultipartFile(FILENAME_UPLOAD).getBytes()).characterEncoding(CHARSET)
                        .accept(CONTENT_TYPE_VAL))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito.verify(mockStorageServiceImpl).uploadFile(any(), any(), any());
    }

    @Test
    void testGetFileList() throws Exception {

        List<String> list = List.of(FILENAME_UPLOAD, FILENAME_EDIT);
        when(mockStorageServiceImpl.getFileList(any())).thenReturn(list);

        mockMvc.perform(get(ENDPOINT_LIST)
                        .header(AUTH_HEADER, BEARER + PASSWORD_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito.verify(mockStorageServiceImpl).getFileList(any());
    }

    @Test
    void testEditFileName() throws Exception {

        when(mockStorageServiceImpl.editFileName(any(), eq(FILENAME_UPLOAD), eq(FILENAME_EDIT))).thenReturn(String.valueOf(String.class));

        var body = objectMapper.writeValueAsString(new FileNameInRequest(FILENAME_EDIT));

        mockMvc.perform(put(ENDPOINT_FILES)
                        .header(AUTH_HEADER, BEARER + PASSWORD_VALUE)
                        .param(FILENAME_PARAM, FILENAME_UPLOAD, FILENAME_EDIT)
                        .contentType(CONTENT_TYPE)
                        .content(body).characterEncoding(CHARSET)
                        .accept(CONTENT_TYPE_VAL))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteFile() throws Exception {

        when(mockStorageServiceImpl.deleteFile(any(), any())).thenReturn(String.valueOf(String.class));

        mockMvc.perform(delete(ENDPOINT_FILES).param(FILENAME_PARAM, FILENAME_UPLOAD)
                        .header(AUTH_HEADER, BEARER + PASSWORD_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    void testDownloadFile() throws Exception {

        when(mockStorageServiceImpl.downloadFile(any(), any())).thenReturn(FILENAME_UPLOAD.getBytes());

        var result = mockMvc.perform(get(ENDPOINT_FILES).param(FILENAME_PARAM, FILENAME_UPLOAD)
                        .header(AUTH_HEADER, BEARER + PASSWORD_VALUE)
                ).andExpect(status().isOk())
                .andReturn();

        assertArrayEquals(result.getResponse().getContentAsByteArray(), FILENAME_UPLOAD.getBytes());
    }


    private MockMultipartFile mockMultipartFile(String filename) throws Exception {
        return new MockMultipartFile(filename, null,
                String.valueOf(MediaType.APPLICATION_JSON), readResourceFile(filename));
    }


    private byte[] readResourceFile(String pathOnClassPath) throws Exception {
        return Files.readAllBytes(Paths.get(Objects.requireNonNull(Thread.currentThread().getContextClassLoader()
                .getResource(pathOnClassPath)).toURI()));
    }


}