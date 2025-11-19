package com.fiap.globalsolution.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateLearningPathRequest {

    @NotBlank(message = "O título do objetivo não pode ser vazio.")
    private String tituloObjetivo;
}
