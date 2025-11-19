package com.fiap.globalsolution.service;

import com.fiap.globalsolution.dto.RegisterRequest;
import com.fiap.globalsolution.exception.UserAlreadyExistsException;
import com.fiap.globalsolution.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = usuarioRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        return user;
    }

    @Transactional
    public void registerUser(RegisterRequest data) {
        if (usuarioRepository.findByEmail(data.email()) != null) {
            throw new UserAlreadyExistsException("The provided email is already in use.");
        }

        String encryptedPassword = passwordEncoder.encode(data.senha());
        usuarioRepository.prInserirUsuario(data.nome(), data.email(), encryptedPassword);
    }
}
