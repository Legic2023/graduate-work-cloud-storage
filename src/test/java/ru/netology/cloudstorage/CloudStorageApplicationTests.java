package ru.netology.cloudstorage;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.cloudstorage.entities.Storage;
import ru.netology.cloudstorage.entities.User;
import ru.netology.cloudstorage.repositories.StorageRepository;
import ru.netology.cloudstorage.repositories.UsersRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.netology.cloudstorage.enums.UserRole.ADMIN;
import static ru.netology.cloudstorage.enums.UserRole.USER;


@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
@Testcontainers
class CloudStorageApplicationTests {

    private static final String FILENAME_UPLOAD = "Book1.txt";
    private static final String FILENAME_EDIT = "Book1_rev2.txt";
    private static final String USER_ADMIN = "Admin";
    private static final String USER_USER = "User";
    private static final String PASSWORD_VALUE = "12345678";
    private static final String DOCKER_IMAGE_NAME = "postgres";
    private static final String DATABASE_NAME = "postgres";
    private static final String DATABASE_USERNAME = "postgres";
    private static final String DATABASE_PASSWORD = "postgres";

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private StorageRepository storageRepository;

    @Test
    void contextLoads() {
    }

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DOCKER_IMAGE_NAME)
            .withDatabaseName(DATABASE_NAME)
            .withUsername(DATABASE_USERNAME)
            .withPassword(DATABASE_PASSWORD);

    static {
        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
    public void containerRunning() {
        assertTrue(postgreSQLContainer.isRunning());
    }

    @Test
    @Transactional
    public void testDataBaseConnection() {

        assertEquals(DATABASE_NAME, postgreSQLContainer.getDatabaseName());
        assertEquals(DATABASE_USERNAME, postgreSQLContainer.getUsername());
        assertEquals(DATABASE_PASSWORD, postgreSQLContainer.getPassword());

        User userAdmin = new User(USER_ADMIN, PASSWORD_VALUE, ADMIN);
        User userUser = new User(USER_USER, PASSWORD_VALUE, USER);
        usersRepository.save(userAdmin);
        usersRepository.save(userUser);

        assertEquals(USER_ADMIN, usersRepository.findByLogin(USER_ADMIN).getUsername());
        assertEquals(USER_USER, usersRepository.findByLogin(USER_USER).getUsername());

        Storage storageUser = new Storage(FILENAME_UPLOAD, null, userUser);
        Storage storageAdmin = new Storage(FILENAME_EDIT, null, userAdmin);
        storageRepository.save(storageAdmin);
        storageRepository.save(storageUser);
        List<String> listFilesExpected = List.of(FILENAME_UPLOAD, FILENAME_EDIT);
        List<String> listFilesActual = storageRepository.getFileNames();

        assertEquals(FILENAME_UPLOAD, storageRepository.findByFileName(FILENAME_UPLOAD).getFileName());
        assertEquals(FILENAME_EDIT, storageRepository.findByFileName(FILENAME_EDIT).getFileName());
        assertEquals(listFilesExpected.stream().sorted().toList(), listFilesActual.stream().sorted().toList());

    }

}

