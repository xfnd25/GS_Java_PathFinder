package com.fiap.globalsolution.service;

import com.fiap.globalsolution.domain.TrilhaAprendizagem;
import com.fiap.globalsolution.exception.ResourceNotFoundException;
import com.fiap.globalsolution.repository.TrilhaAprendizagemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LearningPathServiceTest {

    @Mock
    private TrilhaAprendizagemRepository trilhaRepository;

    @InjectMocks
    private LearningPathService learningPathService;

    @Test
    void buscarTrilhaPorId_deveRetornarTrilha_quandoEncontrada() {
        var trilha = new TrilhaAprendizagem();
        trilha.setId(1L);
        when(trilhaRepository.findById(1L)).thenReturn(Optional.of(trilha));

        var result = learningPathService.buscarTrilhaPorId(1L);

        assertEquals(1L, result.getId());
        verify(trilhaRepository).findById(1L);
    }

    @Test
    void buscarTrilhaPorId_deveLancarExcecao_quandoNaoEncontrada() {
        when(trilhaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            learningPathService.buscarTrilhaPorId(1L);
        });

        verify(trilhaRepository).findById(1L);
    }

    @Test
    void atualizarTrilha_deveChamarProcedureDeAtualizacao() {
        var trilha = new TrilhaAprendizagem();
        trilha.setId(1L);
        trilha.setTituloObjetivo("Novo");
        doNothing().when(trilhaRepository).prAtualizarTrilha(1L, "Novo");
        when(trilhaRepository.findById(1L)).thenReturn(Optional.of(trilha));

        var result = learningPathService.atualizarTrilha(1L, "Novo");

        assertEquals("Novo", result.getTituloObjetivo());
        verify(trilhaRepository).prAtualizarTrilha(1L, "Novo");
    }

    @Test
    void deletarTrilha_deveChamarProcedureDeDelecao() {
        doNothing().when(trilhaRepository).prDeletarTrilha(1L);

        learningPathService.deletarTrilha(1L);

        verify(trilhaRepository).prDeletarTrilha(1L);
    }
}
