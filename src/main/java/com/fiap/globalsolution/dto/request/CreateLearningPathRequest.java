package com.fiap.globalsolution.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for requesting the creation of a new learning path.")
public class CreateLearningPathRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "Current role is mandatory.")
    @Schema(description = "Current role of the professional.", example = "Junior Support Analyst")
    private String cargoAtual;

    @NotEmpty(message = "Objective title is mandatory.")
    @Schema(description = "Career objective or technology to learn.", example = "Machine Learning Engineer")
    private String tituloObjetivo;
}
