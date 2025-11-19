package com.fiap.globalsolution.dto;

import jakarta.validation.constraints.Email;

public record LoginRequest(@Email String email, String senha) {
}
