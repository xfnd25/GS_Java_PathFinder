package com.fiap.globalsolution.messaging;

import com.fiap.globalsolution.config.RabbitMQConfig;
import com.fiap.globalsolution.dto.CreateLearningPathRequest;
import com.fiap.globalsolution.service.AIService;
import com.fiap.globalsolution.service.LearningPathService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class LearningPathConsumer {

    private static final Logger logger = LoggerFactory.getLogger(LearningPathConsumer.class);

    private final AIService aiService;
    private final LearningPathService learningPathService;

    public LearningPathConsumer(AIService aiService, LearningPathService learningPathService) {
        this.aiService = aiService;
        this.learningPathService = learningPathService;
    }

    /**
     * Ouve a fila de geração de trilhas, processa a mensagem e atualiza o banco de dados.
     * @param request O DTO recebido da fila.
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(CreateLearningPathRequest request) {
        logger.info("Mensagem recebida para gerar trilha para o usuário ID: {}", request.getUserId());
        try {
            String aiGeneratedJson = aiService.gerarTrilha(
                request.getCargoAtual(),
                request.getTituloObjetivo()
            );

            learningPathService.atualizarTrilhaComConteudoIA(
                request.getTrilhaId(),
                aiGeneratedJson
            );
            logger.info("Trilha ID: {} atualizada com sucesso.", request.getTrilhaId());

        } catch (Exception e) {
            logger.error("Erro ao processar trilha ID: {}. Motivo: {}", request.getTrilhaId(), e.getMessage());
            learningPathService.marcarTrilhaComoErro(request.getTrilhaId());
        }
    }
}
