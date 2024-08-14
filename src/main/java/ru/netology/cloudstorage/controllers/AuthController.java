package ru.netology.cloudstorage.controllers;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.cloudstorage.config.auth.TokenProvider;
import ru.netology.cloudstorage.dtos.AuthToken;
import ru.netology.cloudstorage.dtos.LogInDto;
import ru.netology.cloudstorage.dtos.SignUpDto;
import ru.netology.cloudstorage.entities.User;
import ru.netology.cloudstorage.services.AuthService;

@Slf4j
@RestController
@RequestMapping
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthService authService;

    @Autowired
    private TokenProvider tokenProvider;


    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUpDto data) {
        authService.signUp(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthToken> login(@RequestBody @Valid LogInDto data) {
        String token = createUserAuth(data);

        log.info("Login User: {}. Token: {}", data.login(), token);

        AuthToken authToken = authService.login(token);
        return ResponseEntity.ok(authToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        authService.logout();
        return ResponseEntity.ok("Logout successful");
    }

    public String createUserAuth(LogInDto data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var userAuth = authenticationManager.authenticate(usernamePassword);
        return tokenProvider.generateAccessToken((User) userAuth.getPrincipal());
    }

}

