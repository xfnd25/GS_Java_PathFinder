package com.fiap.globalsolution.service;

import com.fiap.globalsolution.domain.TrilhaAprendizagem;
import com.fiap.globalsolution.domain.enums.StatusTrilha;
import com.fiap.globalsolution.exception.ResourceNotFoundException;
import com.fiap.globalsolution.repository.TrilhaAprendizagemRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LearningPathService {

    private final TrilhaAprendizagemRepository trilhaRepository;

    public LearningPathService(TrilhaAprendizagemRepository trilhaRepository) {
        this.trilhaRepository = trilhaRepository;
    }

    /**
     * Inicia a criação de uma nova trilha de aprendizado chamando a Stored Procedure.
     * O status inicial será PENDENTE.
     * @param userId O ID do usuário.
     * @param tituloObjetivo O objetivo da trilha.
     * @return O ID da nova trilha criada.
     */
    @Transactional
    public Long iniciarCriacaoTrilha(Long userId, String tituloObjetivo) {
        return trilhaRepository.prInserirTrilha(userId, tituloObjetivo);
    }

    /**
     * Atualiza uma trilha existente com o conteúdo gerado pela IA.
     * Este método é chamado pelo consumer RabbitMQ.
     * @param trilhaId O ID da trilha a ser atualizada.
     * @param conteudoJson O JSON retornado pela IA.
     */
    @Transactional
    public void atualizarTrilhaComConteudoIA(Long trilhaId, String conteudoJson) {
        TrilhaAprendizagem trilha = trilhaRepository.findById(trilhaId)
            .orElseThrow(() -> new ResourceNotFoundException("Trilha não encontrada com o ID: " + trilhaId));

        trilha.setDadosJsonIA(conteudoJson);
        trilha.setStatus(StatusTrilha.CONCLUIDA);
        trilhaRepository.save(trilha);
    }

    /**
     * Marca o status de uma trilha como ERRO.
     * @param trilhaId O ID da trilha.
     */
    @Transactional
    public void marcarTrilhaComoErro(Long trilhaId) {
        TrilhaAprendizagem trilha = trilhaRepository.findById(trilhaId)
            .orElseThrow(() -> new ResourceNotFoundException("Trilha não encontrada com o ID: " + trilhaId));

        trilha.setStatus(StatusTrilha.ERRO);
        trilhaRepository.save(trilha);
    }

    /**
     * Lista todas as trilhas de aprendizado de forma paginada.
     * O resultado é cacheado para melhorar a performance.
     * @param pageable Configuração de paginação.
     * @return Uma página de trilhas.
     */
    @Cacheable("learning-paths")
    public Page<TrilhaAprendizagem> listarTrilhas(Pageable pageable) {
        return trilhaRepository.findAll(pageable);
    }

    public TrilhaAprendizagem buscarTrilhaPorId(Long trilhaId) {
        return trilhaRepository.findById(trilhaId)
                .orElseThrow(() -> new ResourceNotFoundException("Trilha não encontrada com o ID: " + trilhaId));
    }

    @Transactional
    public TrilhaAprendizagem atualizarTrilha(Long trilhaId, String novoTitulo) {
        trilhaRepository.prAtualizarTrilha(trilhaId, novoTitulo);
        return buscarTrilhaPorId(trilhaId);
    }

    @Transactional
    public void deletarTrilha(Long trilhaId) {
        trilhaRepository.prDeletarTrilha(trilhaId);
    }
}
