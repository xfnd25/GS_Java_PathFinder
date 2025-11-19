package com.fiap.globalsolution.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        String nome,
        @Email String email,
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=.*[0-9]).{8,}$",
                 message = "A senha deve ter no mínimo 8 caracteres, uma letra maiúscula, um caractere especial e um número.")
        String senha
) {
}
