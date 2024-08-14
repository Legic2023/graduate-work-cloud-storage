package ru.netology.cloudstorage.services;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.netology.cloudstorage.controllers.AuthController;
import ru.netology.cloudstorage.dtos.LogInDto;
import ru.netology.cloudstorage.dtos.SignUpDto;
import ru.netology.cloudstorage.entities.Storage;
import ru.netology.cloudstorage.entities.User;
import ru.netology.cloudstorage.repositories.StorageRepository;
import ru.netology.cloudstorage.repositories.UsersRepository;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.cloudstorage.enums.UserRole.ADMIN;
import static ru.netology.cloudstorage.enums.UserRole.USER;


@SpringBootTest
public class StorageServiceTest {

    private static final String FILENAME_UPLOAD = "Book1.txt";
    private static final String FILENAME_EDIT = "Book1_rev2.txt";
    private static final String USER_ADMIN = "Admin";
    private static final String USER_USER = "User";
    private static final String PASSWORD_VALUE = "12345678";
    private final LogInDto logInDtoAdmin = new LogInDto(USER_ADMIN, PASSWORD_VALUE);
    private final SignUpDto signUpDtoAdmin = new SignUpDto(logInDtoAdmin, ADMIN);
    private final LogInDto logInDtoUser = new LogInDto(USER_USER, PASSWORD_VALUE);
    private final SignUpDto signUpDtoUser = new SignUpDto(logInDtoUser, USER);

    @Autowired
    StorageService storageService;

    @Autowired
    StorageRepository storageRepository;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    AuthController authController;


    @BeforeEach
    void setUp() {
        storageRepository.deleteAll();
        usersRepository.deleteAll();

        String encryptedPassword = new BCryptPasswordEncoder().encode(PASSWORD_VALUE);
        User userAdmin = new User(USER_ADMIN, encryptedPassword, ADMIN);
        User userUser = new User(USER_USER, encryptedPassword, ADMIN);
        usersRepository.save(userAdmin);
        usersRepository.save(userUser);
    }

    @Test
    @Transactional
    void uploadFile() throws Exception {
        MockMultipartFile mockMultipartFile = mockFile(FILENAME_UPLOAD);
        setAuthentication(signUpDtoAdmin);

        storageService.uploadFile(FILENAME_UPLOAD, mockMultipartFile);

        User userAdmin = (User) usersRepository.findByLogin(USER_ADMIN);

        var storage = storageRepository.findByFileNameAndUser(FILENAME_UPLOAD, userAdmin);
        assertEquals(FILENAME_UPLOAD, storage.getFileName());
        assertEquals(USER_ADMIN, storage.getUser().getLogin());
    }

    @Test
    void getFileList() {
        storageRepository.save(new Storage(FILENAME_UPLOAD, null, null));
        setAuthentication(signUpDtoUser);
        var fileList = storageService.getFileList();

        assertFalse(fileList.isEmpty());
        assertEquals(1, fileList.size());
        assertEquals(FILENAME_UPLOAD, fileList.get(0));
    }

    @Test
    void downloadFile() throws Exception {
        byte[] bytesActual = readResourceFile(FILENAME_UPLOAD);

        MockMultipartFile mockMultipartFile = mockFile(FILENAME_UPLOAD);
        User userAdmin = (User) usersRepository.findByLogin(USER_ADMIN);
        storageRepository.save(new Storage(FILENAME_UPLOAD, mockMultipartFile.getBytes(), userAdmin));
        setAuthentication(signUpDtoAdmin);

        var bytesExpected = storageService.downloadFile(userAdmin, FILENAME_UPLOAD);
        assertArrayEquals(bytesExpected, bytesActual);
    }

    @Test
    void editFileName() {
        User userAdmin = (User) usersRepository.findByLogin(USER_ADMIN);
        storageRepository.save(new Storage(FILENAME_UPLOAD, null, userAdmin));
        setAuthentication(signUpDtoAdmin);

        assertTrue(storageRepository.existsByFileNameAndUser(FILENAME_UPLOAD, userAdmin));
        storageService.editFileName(userAdmin, FILENAME_UPLOAD, FILENAME_EDIT);
        assertFalse(storageRepository.existsByFileNameAndUser(FILENAME_UPLOAD, userAdmin));

        var storage = storageRepository.findByFileNameAndUser(FILENAME_EDIT, userAdmin);
        assertEquals(FILENAME_EDIT, storage.getFileName());
    }

    @Test
    void deleteFile() throws Exception {
        MockMultipartFile mockMultipartFile = mockFile(FILENAME_UPLOAD);
        User userAdmin = (User) usersRepository.findByLogin(USER_ADMIN);
        storageRepository.save(new Storage(FILENAME_UPLOAD, mockMultipartFile.getBytes(), userAdmin));
        setAuthentication(signUpDtoAdmin);

        assertTrue(storageRepository.existsByFileNameAndUser(FILENAME_UPLOAD, userAdmin));
        storageService.deleteFile(userAdmin, FILENAME_UPLOAD);
        assertFalse(storageRepository.existsByFileNameAndUser(FILENAME_UPLOAD, userAdmin));
    }

    void setAuthentication(SignUpDto signUpDto) {
        String username = signUpDto.logInDto().login();
        User user = (User) usersRepository.findByLogin(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private MockMultipartFile mockFile(String filename) throws Exception {
        return new MockMultipartFile(filename, null,
                String.valueOf(MediaType.APPLICATION_JSON), readResourceFile(filename));
    }

    private byte[] readResourceFile(String pathOnClassPath) throws Exception {
        return Files.readAllBytes(Paths.get(Objects.requireNonNull(Thread.currentThread().getContextClassLoader()
                .getResource(pathOnClassPath)).toURI()));
    }

}
