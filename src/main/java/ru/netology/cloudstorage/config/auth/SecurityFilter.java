
package ru.netology.cloudstorage.config.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.netology.cloudstorage.repositories.UsersRepository;

import java.io.IOException;

@Slf4j
@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "auth-token";
    private static final String BEARER = "Bearer ";

    @Autowired(required = false)
    UsersRepository usersRepository;

    @Autowired(required = false)
    TokenProvider tokenProvider;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var authToken = this.recoverToken(request);
        if (authToken != null) {
            var login = tokenProvider.validateToken(authToken);
            var user = usersRepository.findByLogin(login);
            var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader(AUTH_HEADER);

        log.info("TokenFilter: {}", authHeader);

        if (authHeader == null)
            return null;
        return authHeader.replace(BEARER, "").strip();
    }

}
