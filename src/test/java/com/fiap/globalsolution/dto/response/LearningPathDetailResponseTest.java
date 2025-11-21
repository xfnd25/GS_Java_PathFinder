package com.fiap.globalsolution.dto.response;

import com.fiap.globalsolution.domain.TrilhaAprendizagem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class LearningPathDetailResponseTest {

    @Test
    void shouldParseMarkdownFormattedJson() {
        String markdownJson = "```json\n{\"key\": \"value\"}\n```";
        TrilhaAprendizagem trilha = new TrilhaAprendizagem();
        trilha.setId(1L);
        trilha.setDadosJsonIA(markdownJson);

        LearningPathDetailResponse response = new LearningPathDetailResponse(trilha);

        Assertions.assertNotNull(response.getDadosJsonIA());
        Assertions.assertTrue(response.getDadosJsonIA() instanceof Map);
        Assertions.assertEquals("value", ((Map) response.getDadosJsonIA()).get("key"));
    }

    @Test
    void shouldParseCleanJson() {
        String cleanJson = "{\"key\": \"value\"}";
        TrilhaAprendizagem trilha = new TrilhaAprendizagem();
        trilha.setId(1L);
        trilha.setDadosJsonIA(cleanJson);

        LearningPathDetailResponse response = new LearningPathDetailResponse(trilha);

        Assertions.assertNotNull(response.getDadosJsonIA());
    }
}
