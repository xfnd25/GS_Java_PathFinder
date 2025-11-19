package com.fiap.globalsolution.dto;

import com.fiap.globalsolution.domain.TrilhaAprendizagem;
import com.fiap.globalsolution.domain.enums.StatusTrilha;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LearningPathResponse {

    private Long idTrilha;
    private Long idPerfil;
    private String tituloObjetivo;
    private StatusTrilha status;
    private String dadosJsonIA;

    public LearningPathResponse(TrilhaAprendizagem trilha) {
        this.idTrilha = trilha.getId();
        this.idPerfil = trilha.getPerfil() != null ? trilha.getPerfil().getId() : null;
        this.tituloObjetivo = trilha.getTituloObjetivo();
        this.status = trilha.getStatus();
        this.dadosJsonIA = trilha.getDadosJsonIA();
    }
}
