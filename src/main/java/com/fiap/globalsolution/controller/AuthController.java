package com.fiap.globalsolution.controller;

import com.fiap.globalsolution.domain.Usuario;
import com.fiap.globalsolution.dto.LoginRequest;
import com.fiap.globalsolution.dto.RegisterRequest;
import com.fiap.globalsolution.dto.TokenResponse;
import com.fiap.globalsolution.service.AuthenticationService;
import com.fiap.globalsolution.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.fiap.globalsolution.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration.")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and return a JWT token.")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest data) {
        Usuario user = (Usuario) usuarioRepository.findByEmail(data.email());

        if (user == null || !passwordEncoder.matches(data.senha(), user.getSenhaHash())) {
            return ResponseEntity.status(403).build();
        }

        var token = tokenService.generateToken(user);

        return ResponseEntity.ok(new TokenResponse(token));
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user.")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest data) {
        authenticationService.registerUser(data);
        return ResponseEntity.ok().build();
    }
}
