package com.fiap.globalsolution.dto;

import com.fiap.globalsolution.domain.TrilhaAprendizagem;
import com.fiap.globalsolution.domain.enums.StatusTrilha;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "DTO de resposta com os detalhes da trilha de aprendizado.")
public class LearningPathResponse {

    @Schema(description = "ID único da trilha.", example = "10")
    private Long idTrilha;

    @Schema(description = "ID do perfil do usuário.", example = "1")
    private Long idPerfil;

    @Schema(description = "Objetivo definido para a trilha.", example = "Engenheiro de Dados")
    private String tituloObjetivo;

    @Schema(description = "Status atual do processamento.", example = "CONCLUIDA")
    private StatusTrilha status;

    @Schema(description = "Conteúdo JSON gerado pela IA com os passos de estudo.", example = "{\"trilha\": [{\"titulo\": \"Introdução\", ...}]}")
    private String dadosJsonIA;

    public LearningPathResponse(TrilhaAprendizagem trilha) {
        this.idTrilha = trilha.getId();
        this.idPerfil = trilha.getPerfil() != null ? trilha.getPerfil().getId() : null;
        this.tituloObjetivo = trilha.getTituloObjetivo();
        this.status = trilha.getStatus();
        this.dadosJsonIA = trilha.getDadosJsonIA();
    }
}