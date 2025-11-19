package com.fiap.globalsolution.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateLearningPathRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "O ID do usuário é obrigatório.")
    private Long userId;

    // Este ID será populado pelo serviço após a inserção inicial no banco
    private Long trilhaId;

    @NotEmpty(message = "O cargo atual é obrigatório.")
    private String cargoAtual;

    @NotEmpty(message = "O título do objetivo é obrigatório.")
    private String tituloObjetivo;
}
