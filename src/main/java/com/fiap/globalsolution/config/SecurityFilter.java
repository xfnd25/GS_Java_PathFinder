package com.fiap.globalsolution.config;

import com.fiap.globalsolution.repository.UsuarioRepository;
import com.fiap.globalsolution.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that intercepts all requests to validate the JWT token.
 * If the token is valid, it authenticates the user in the security context.
 */
@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(SecurityFilter.class);
    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * This method is executed for each incoming request. It recovers the token,
     * validates it, and sets the authentication in the Spring Security context.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.startsWith("/auth/") || path.startsWith("/swagger-ui/") || path.startsWith("/v3/api-docs/") || path.startsWith("/actuator/")) {
            filterChain.doFilter(request, response);
            return;
        }

        var token = this.recoverToken(request);

        if (token != null) {
            var subject = tokenService.validateToken(token);
            if (subject != null) {
                UserDetails user = usuarioRepository.findByEmail(subject);

                if (user != null) {
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("User {} authenticated successfully.", subject);
                } else {
                    log.warn("User {} not found in database, but token was valid.", subject);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Recovers the JWT token from the Authorization header.
     * @param request The incoming HTTP request.
     * @return The token as a String, or null if not present or invalid.
     */
    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader(AUTH_HEADER);
        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
            return null;
        }
        return authHeader.substring(TOKEN_PREFIX.length());
    }
}