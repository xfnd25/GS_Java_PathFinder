package com.fiap.globalsolution.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para solicitar a criação de uma nova trilha de aprendizado.")
public class CreateLearningPathRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "O ID do usuário é obrigatório.")
    @Schema(description = "ID do usuário associado ao perfil que está solicitando a trilha.", example = "1")
    private Long userId;

    // Este ID será populado pelo serviço após a inserção inicial no banco
    @Schema(description = "ID da trilha que é gerado após a inserção inicial no banco. Não precisa ser enviado na requisição.", accessMode = Schema.AccessMode.READ_ONLY)
    private Long trilhaId;

    @NotEmpty(message = "O cargo atual é obrigatório.")
    @Schema(description = "Cargo atual do profissional que busca requalificação.", example = "Analista de Suporte Junior")
    private String cargoAtual;

    @NotEmpty(message = "O título do objetivo é obrigatório.")
    @Schema(description = "Objetivo de carreira ou tecnologia que o profissional deseja aprender.", example = "Engenheiro de Machine Learning")
    private String tituloObjetivo;
}