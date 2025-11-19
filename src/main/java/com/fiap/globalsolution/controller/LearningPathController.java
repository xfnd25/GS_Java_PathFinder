package com.fiap.globalsolution.controller;

import com.fiap.globalsolution.dto.CreateLearningPathRequest;
import com.fiap.globalsolution.dto.LearningPathResponse;
import com.fiap.globalsolution.messaging.LearningPathProducer;
import com.fiap.globalsolution.service.LearningPathService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/learning-paths")
@Tag(name = "Trilhas de Aprendizado", description = "Endpoints para gerenciamento de trilhas de aprendizado geradas por IA.")
public class LearningPathController {

    private final LearningPathService learningPathService;
    private final LearningPathProducer learningPathProducer;

    public LearningPathController(LearningPathService learningPathService, LearningPathProducer learningPathProducer) {
        this.learningPathService = learningPathService;
        this.learningPathProducer = learningPathProducer;
    }

    @Operation(
        summary = "Inicia a Geração de uma Nova Trilha",
        description = "Recebe os dados do perfil do usuário e seu objetivo, inicia o registro no banco de dados com status 'PENDENTE' e envia uma mensagem para o RabbitMQ para processamento assíncrono. Retorna imediatamente o status 202 (Accepted)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Requisição aceita para processamento."),
        @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos."),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
    })
    @PostMapping
    public ResponseEntity<Void> createLearningPath(@Valid @RequestBody CreateLearningPathRequest request) {
        // 1. Inicia a transação no banco e obtém o ID da trilha
        Long trilhaId = learningPathService.iniciarCriacaoTrilha(
            request.getUserId(),
            request.getTituloObjetivo()
        );

        // 2. Prepara a mensagem para o RabbitMQ com o ID gerado
        request.setTrilhaId(trilhaId);

        // 3. Envia a mensagem para a fila para processamento assíncrono
        learningPathProducer.sendGenerationRequest(request);

        return ResponseEntity.accepted().build();
    }

    @Operation(
        summary = "Lista Todas as Trilhas de Aprendizado",
        description = "Retorna uma lista paginada de todas as trilhas de aprendizado existentes no sistema. O resultado é cacheado para otimizar a performance."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de trilhas retornada com sucesso."),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
    })
    @GetMapping
    public ResponseEntity<Page<LearningPathResponse>> getAllLearningPaths(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        Page<LearningPathResponse> responsePage = learningPathService.listarTrilhas(pageable)
            .map(LearningPathResponse::new);

        return ResponseEntity.ok(responsePage);
    }
}