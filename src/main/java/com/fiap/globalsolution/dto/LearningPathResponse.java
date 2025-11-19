package com.fiap.globalsolution.dto;

import com.fiap.globalsolution.domain.TrilhaAprendizagem;
import com.fiap.globalsolution.domain.enums.StatusTrilha;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "DTO para representar os dados de uma trilha de aprendizado retornada pela API.")
public class LearningPathResponse {

    @Schema(description = "ID único da trilha de aprendizado.", example = "101")
    private Long idTrilha;

    @Schema(description = "ID do perfil de usuário associado a esta trilha.", example = "51")
    private Long idPerfil;

    @Schema(description = "Objetivo de carreira que guiou a criação desta trilha.", example = "Desenvolvedor Backend com Microsserviços")
    private String tituloObjetivo;

    @Schema(description = "Status atual do processamento da trilha.", example = "CONCLUIDA")
    private StatusTrilha status;

    @Schema(description = "String JSON contendo os passos da trilha de aprendizado gerados pela IA.")
    private String dadosJsonIA;

    public LearningPathResponse(TrilhaAprendizagem trilha) {
        this.idTrilha = trilha.getId();
        this.idPerfil = trilha.getPerfil() != null ? trilha.getPerfil().getId() : null;
        this.tituloObjetivo = trilha.getTituloObjetivo();
        this.status = trilha.getStatus();
        this.dadosJsonIA = trilha.getDadosJsonIA();
    }
}
