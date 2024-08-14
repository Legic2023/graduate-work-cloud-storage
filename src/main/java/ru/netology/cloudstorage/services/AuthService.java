package ru.netology.cloudstorage.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.dtos.AuthToken;
import ru.netology.cloudstorage.dtos.JwtDto;
import ru.netology.cloudstorage.dtos.SignUpDto;
import ru.netology.cloudstorage.entities.User;
import ru.netology.cloudstorage.exceptions.InvalidJwtException;
import ru.netology.cloudstorage.repositories.UsersRepository;


@Service
public class AuthService implements UserDetailsService {

    @Autowired
    UsersRepository usersRepository;


    public void signUp(SignUpDto data) throws InvalidJwtException {
        if (usersRepository.findByLogin(data.logInDto().login()) != null) {
            throw new InvalidJwtException("Username already exists");
        }
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.logInDto().password());
        usersRepository.save(new User(data.logInDto().login(), encryptedPassword, data.role()));
    }

    public AuthToken login(String token) {
        return new AuthToken(new JwtDto(token).accessToken());
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usersRepository.findByLogin(username);
    }


}
