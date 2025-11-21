package com.fiap.globalsolution.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AIServiceTest {

    @Mock
    private ChatModel chatModel;

    @InjectMocks
    private AIService aiService;

    @Test
    void gerarTrilha_deveLimparMarkdownDaResposta() {
        // Arrange
        String rawResponse = "```json\n" +
                "{\n" +
                "  \"trilha\": []\n" +
                "}\n" +
                "```";
        String expectedJson = "{\n" +
                "  \"trilha\": []\n" +
                "}";

        ChatResponse chatResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        AssistantMessage assistantMessage = new AssistantMessage(rawResponse);

        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse);
        when(chatResponse.getResult()).thenReturn(generation);
        when(generation.getOutput()).thenReturn(assistantMessage);

        // Act
        String result = aiService.gerarTrilha("Dev", "Senior");

        // Assert
        // Normalize whitespace for comparison if needed, but the sanitization should handle it
        // For this test, we expect the clean version.
        // Since the implementation isn't there yet, this test is expected to fail or pass if I were TDDing strictly.
        // But here I will implement the fix next.
        assertEquals(expectedJson.trim(), result.trim());
    }

    @Test
    void gerarTrilha_deveManterJsonLimpoSeJaEstiverLimpo() {
        // Arrange
        String rawResponse = "{\"trilha\": []}";

        ChatResponse chatResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        AssistantMessage assistantMessage = new AssistantMessage(rawResponse);

        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse);
        when(chatResponse.getResult()).thenReturn(generation);
        when(generation.getOutput()).thenReturn(assistantMessage);

        // Act
        String result = aiService.gerarTrilha("Dev", "Senior");

        // Assert
        assertEquals(rawResponse, result);
    }

     @Test
    void gerarTrilha_deveLimparMarkdownSemJsonKeyword() {
        // Arrange
        String rawResponse = "```\n{\"trilha\": []}\n```";
        String expectedJson = "{\"trilha\": []}";

        ChatResponse chatResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        AssistantMessage assistantMessage = new AssistantMessage(rawResponse);

        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse);
        when(chatResponse.getResult()).thenReturn(generation);
        when(generation.getOutput()).thenReturn(assistantMessage);

        // Act
        String result = aiService.gerarTrilha("Dev", "Senior");

        // Assert
        assertEquals(expectedJson, result);
    }
}
