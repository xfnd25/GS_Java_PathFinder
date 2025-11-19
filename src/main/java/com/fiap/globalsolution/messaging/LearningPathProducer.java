package com.fiap.globalsolution.messaging;

import com.fiap.globalsolution.config.RabbitMQConfig;
import com.fiap.globalsolution.dto.CreateLearningPathRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class LearningPathProducer {

    private final RabbitTemplate rabbitTemplate;

    public LearningPathProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Envia uma solicitação para a fila de geração de trilha de aprendizado.
     * @param request O DTO contendo os dados para a geração da trilha.
     */
    public void sendGenerationRequest(CreateLearningPathRequest request) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_NAME,
            RabbitMQConfig.ROUTING_KEY,
            request
        );
    }
}
