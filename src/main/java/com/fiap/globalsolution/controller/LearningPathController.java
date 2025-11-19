package com.fiap.globalsolution.controller;

import com.fiap.globalsolution.dto.CreateLearningPathRequest;
import com.fiap.globalsolution.dto.LearningPathResponse;
import com.fiap.globalsolution.messaging.LearningPathProducer;
import com.fiap.globalsolution.service.LearningPathService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/learning-paths")
public class LearningPathController {

    private final LearningPathService learningPathService;
    private final LearningPathProducer learningPathProducer;

    public LearningPathController(LearningPathService learningPathService, LearningPathProducer learningPathProducer) {
        this.learningPathService = learningPathService;
        this.learningPathProducer = learningPathProducer;
    }

    /**
     * Inicia a criação de uma nova trilha de aprendizado de forma assíncrona.
     * Retorna HTTP 202 (Accepted) imediatamente.
     * @param request DTO com os dados para criar a trilha.
     * @return Resposta vazia com status 202.
     */
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

    /**
     * Lista todas as trilhas de aprendizado de forma paginada.
     * @param pageable Parâmetros de paginação (size, page, sort).
     * @return Uma página com as trilhas de aprendizado.
     */
    @GetMapping
    public ResponseEntity<Page<LearningPathResponse>> getAllLearningPaths(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        Page<LearningPathResponse> responsePage = learningPathService.listarTrilhas(pageable)
            .map(LearningPathResponse::new);

        return ResponseEntity.ok(responsePage);
    }
}
