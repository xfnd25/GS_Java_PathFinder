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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration.")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and return a JWT token.")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((Usuario) auth.getPrincipal());

        return ResponseEntity.ok(new TokenResponse(token));
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user.")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest data) {
        authenticationService.registerUser(data);
        return ResponseEntity.ok().build();
    }
}
