package com.fiap.globalsolution.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.globalsolution.domain.TrilhaAprendizagem;
import com.fiap.globalsolution.domain.Usuario;
import com.fiap.globalsolution.dto.request.CreateLearningPathRequest;
import com.fiap.globalsolution.dto.UpdateLearningPathRequest;
import com.fiap.globalsolution.messaging.LearningPathProducer;
import com.fiap.globalsolution.service.LearningPathService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest(LearningPathController.class)
class LearningPathControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LearningPathService learningPathService;

    @MockBean
    private LearningPathProducer learningPathProducer;

    @MockBean
    private com.fiap.globalsolution.service.TokenService tokenService;

    @MockBean
    private com.fiap.globalsolution.repository.UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createLearningPath_shouldReturnAccepted() throws Exception {
        var request = new CreateLearningPathRequest("Dev", "Architect");

        // Create a mock user
        Usuario mockUser = new Usuario();
        mockUser.setId(1L);
        mockUser.setEmail("test@test.com");
        mockUser.setSenhaHash("pass");
        mockUser.setNome("Test User");

        mockMvc.perform(post("/api/v1/learning-paths")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                        .with(user(mockUser)))
                .andExpect(status().isAccepted());
    }

    @Test
    @WithMockUser
    void getLearningPathById_shouldReturnLearningPath() throws Exception {
        var trilha = new TrilhaAprendizagem();
        trilha.setId(1L);
        given(learningPathService.buscarTrilhaPorId(1L)).willReturn(trilha);

        mockMvc.perform(get("/api/v1/learning-paths/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTrilha").value(1L));
    }

    @Test
    @WithMockUser
    void updateLearningPath_shouldReturnUpdatedLearningPath() throws Exception {
        var request = new UpdateLearningPathRequest();
        request.setTituloObjetivo("Novo Titulo");

        var trilha = new TrilhaAprendizagem();
        trilha.setId(1L);
        trilha.setTituloObjetivo("Novo Titulo");

        given(learningPathService.atualizarTrilha(1L, "Novo Titulo")).willReturn(trilha);

        mockMvc.perform(put("/api/v1/learning-paths/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tituloObjetivo").value("Novo Titulo"));
    }

    @Test
    @WithMockUser
    void deleteLearningPath_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/learning-paths/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
