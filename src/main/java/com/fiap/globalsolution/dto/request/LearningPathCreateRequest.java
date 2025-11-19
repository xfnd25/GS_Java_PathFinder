package com.fiap.globalsolution.dto.request;

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
@Schema(description = "DTO for requesting the creation of a new learning path.")
public class LearningPathCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "User ID is mandatory.")
    @Schema(description = "ID of the user requesting the path.", example = "1")
    private Long userId;

    @Schema(description = "Path ID, populated by the service after creation. Do not send in the request.", accessMode = Schema.AccessMode.READ_ONLY)
    private Long trilhaId;

    @NotEmpty(message = "Current role is mandatory.")
    @Schema(description = "Current role of the professional.", example = "Junior Support Analyst")
    private String cargoAtual;

    @NotEmpty(message = "Objective title is mandatory.")
    @Schema(description = "Career objective or technology to learn.", example = "Machine Learning Engineer")
    private String tituloObjetivo;
}