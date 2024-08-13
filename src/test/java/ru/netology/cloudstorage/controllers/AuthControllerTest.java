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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.netology.cloudstorage.dtos.LogInDto;
import ru.netology.cloudstorage.dtos.SignUpDto;
import ru.netology.cloudstorage.services.AuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.netology.cloudstorage.enums.UserRole.ADMIN;


@Slf4j
@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    private static final String ENDPOINT_SIGNUP = "/signup";
    private static final String ENDPOINT_LOGIN = "/login";
    private static final String ENDPOINT_LOGOUT = "/logout";
    private static final String USER_ADMIN = "Admin";
    private static final String PASSWORD_VALUE = "12345678";
    private static final String AUTH_HEADER = "auth-token";
    private static final String BEARER = "Bearer ";
    private static final String CHARSET = "UTF-8";
    private static final MediaType CONTENT_TYPE = MediaType.APPLICATION_JSON;
    private static final String CONTENT_TYPE_VAL = "application/json";

    private final LogInDto logInDto = new LogInDto(USER_ADMIN, PASSWORD_VALUE);
    private final SignUpDto signUpDto = new SignUpDto(logInDto, ADMIN);

    @MockBean
    AuthService authService;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthController mockAuthController;

    @Autowired
    ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mockAuthController).build();
    }

    @Test
    void testSignUp() throws Exception {

        var signUpBody = objectMapper.writeValueAsString(signUpDto);

        mockMvc.perform(post(ENDPOINT_SIGNUP)
                        .header(AUTH_HEADER, BEARER + PASSWORD_VALUE)
                        .contentType(CONTENT_TYPE)
                        .content(signUpBody).characterEncoding(CHARSET)
                        .accept(CONTENT_TYPE_VAL))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito.verify(mockAuthController).signUp(any());
    }

    @Test
    void testLogin() throws Exception {

        var logInBody = objectMapper.writeValueAsString(logInDto);

        mockMvc.perform(post(ENDPOINT_LOGIN)
                        .contentType(CONTENT_TYPE)
                        .content(logInBody).characterEncoding(CHARSET)
                        .accept(CONTENT_TYPE_VAL))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito.verify(mockAuthController).login(any());
    }

    @Test
    void testLogout() throws Exception {

        var logInBody = objectMapper.writeValueAsString(logInDto);

        mockMvc.perform(post(ENDPOINT_LOGOUT)
                        .header(AUTH_HEADER, BEARER + PASSWORD_VALUE)
                        .contentType(CONTENT_TYPE)
                        .content(logInBody).characterEncoding(CHARSET)
                        .accept(CONTENT_TYPE_VAL))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito.verify(mockAuthController).logout();
    }

}
