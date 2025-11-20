package com.fiap.globalsolution.controller;

import com.fiap.globalsolution.domain.TrilhaAprendizagem;
import com.fiap.globalsolution.domain.Usuario;
import com.fiap.globalsolution.dto.request.CreateLearningPathRequest;
import com.fiap.globalsolution.dto.response.LearningPathDetailResponse;
import com.fiap.globalsolution.dto.UpdateLearningPathRequest;
import com.fiap.globalsolution.service.LearningPathService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/learning-paths")
@Tag(name = "Trilhas de Aprendizado", description = "Endpoints para gerenciamento de trilhas de aprendizado.")
public class LearningPathController {

    private final LearningPathService learningPathService;

    public LearningPathController(LearningPathService learningPathService) {
        this.learningPathService = learningPathService;
    }

    @Operation(
            summary = "Inicia a Geração de uma Nova Trilha",
            description = "Cria o registro da trilha e envia para processamento assíncrono. Retorna 202 (Accepted)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Requisição aceita."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos.")
    })
    @PostMapping
    public ResponseEntity<Void> createLearningPath(
            @Valid @RequestBody CreateLearningPathRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioLogado) {
        learningPathService.criarTrilha(request, usuarioLogado.getId());
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Lista Todas as Trilhas de Aprendizado")
    @GetMapping
    public ResponseEntity<Page<LearningPathDetailResponse>> getAllLearningPaths(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<LearningPathDetailResponse> responsePage = learningPathService.listarTrilhas(pageable)
                .map(LearningPathDetailResponse::new);

        return ResponseEntity.ok(responsePage);
    }

    @Operation(summary = "Busca uma Trilha por ID")
    @GetMapping("/{id}")
    public ResponseEntity<LearningPathDetailResponse> getLearningPathById(@PathVariable Long id) {
        TrilhaAprendizagem learningPath = learningPathService.buscarTrilhaPorId(id);
        return ResponseEntity.ok(new LearningPathDetailResponse(learningPath));
    }

    @Operation(summary = "Atualiza o Objetivo de uma Trilha")
    @PutMapping("/{id}")
    public ResponseEntity<LearningPathDetailResponse> updateLearningPath(@PathVariable Long id, @Valid @RequestBody UpdateLearningPathRequest request) {
        TrilhaAprendizagem learningPath = learningPathService.atualizarTrilha(id, request.getTituloObjetivo());
        return ResponseEntity.ok(new LearningPathDetailResponse(learningPath));
    }

    @Operation(summary = "Exclui uma Trilha")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLearningPath(@PathVariable Long id) {
        learningPathService.deletarTrilha(id);
        return ResponseEntity.noContent().build();
    }
}