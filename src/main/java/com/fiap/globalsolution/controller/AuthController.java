package com.fiap.globalsolution.controller;

import com.fiap.globalsolution.domain.Usuario;
import com.fiap.globalsolution.dto.LoginRequest;
import com.fiap.globalsolution.dto.RegisterRequest;
import com.fiap.globalsolution.dto.TokenResponse;
import com.fiap.globalsolution.exception.UserAlreadyExistsException;
import com.fiap.globalsolution.repository.UsuarioRepository;
import com.fiap.globalsolution.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((Usuario) auth.getPrincipal());

        return ResponseEntity.ok(new TokenResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest data) {
        if(usuarioRepository.findByEmail(data.email()) != null) {
            throw new UserAlreadyExistsException("O email informado já está em uso.");
        }

        String encryptedPassword = passwordEncoder.encode(data.senha());
        usuarioRepository.prInserirUsuario(data.nome(), data.email(), encryptedPassword);

        return ResponseEntity.ok().build();
    }
}
