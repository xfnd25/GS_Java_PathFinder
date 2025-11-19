package com.fiap.globalsolution.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotBlank
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=.*[0-9]).{8,}$",
                message = "Senha deve ter min 8 caracteres, 1 maiúscula, 1 número e 1 especial")
        String senha
) {}