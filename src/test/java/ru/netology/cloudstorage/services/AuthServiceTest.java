package ru.netology.cloudstorage.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.netology.cloudstorage.controllers.AuthController;
import ru.netology.cloudstorage.dtos.AuthToken;
import ru.netology.cloudstorage.dtos.JwtDto;
import ru.netology.cloudstorage.dtos.LogInDto;
import ru.netology.cloudstorage.dtos.SignUpDto;
import ru.netology.cloudstorage.repositories.StorageRepository;
import ru.netology.cloudstorage.repositories.UsersRepository;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.cloudstorage.enums.UserRole.ADMIN;


@SpringBootTest
public class AuthServiceTest {

    private static final String USER_ADMIN = "testAdmin";
    private static final String PASSWORD_VALUE = "12345678";
    private final LogInDto logInDto = new LogInDto(USER_ADMIN, PASSWORD_VALUE);
    private final SignUpDto signUpDto = new SignUpDto(logInDto, ADMIN);


    @Autowired
    AuthController authController;

    @Autowired
    AuthService authService;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    StorageRepository storageRepository;


    @BeforeEach
    void setUp() {
        storageRepository.deleteAll();
        usersRepository.deleteAll();
    }


    @Test
    void testAuthService_do_methods() {

        //test signUp() method
        authService.signUp(signUpDto);
        var user = authService.usersRepository.findByLogin(USER_ADMIN);
        assertEquals(USER_ADMIN, user.getUsername());

        //test logIn() method
        var token = authController.createUserAuth(logInDto);
        var authToken = new AuthToken(new JwtDto(token).accessToken());
        var loginAuthToken = authService.login(token);
        assertEquals(authToken.getAuthToken(), loginAuthToken.getAuthToken());

        //test logOut() method
        setAuthentication(signUpDto); // установить аутентификацию
        var isAuth = SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
        assertTrue(isAuth);
        authService.logout();
        var NullAuth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(NullAuth);
    }

    public Collection<? extends GrantedAuthority> getSignupAuthorities(SignUpDto signUpDto) {
        if (signUpDto.role() == ADMIN) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    private void setAuthentication(SignUpDto signUpDto) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(signUpDto.logInDto().login(), null, getSignupAuthorities(signUpDto));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
