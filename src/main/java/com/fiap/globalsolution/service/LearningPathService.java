package com.fiap.globalsolution.service;

import com.fiap.globalsolution.domain.TrilhaAprendizagem;
import com.fiap.globalsolution.domain.enums.StatusTrilha;
import com.fiap.globalsolution.dto.request.CreateLearningPathRequest;
import com.fiap.globalsolution.dto.request.LearningPathCreateRequest;
import com.fiap.globalsolution.exception.ResourceNotFoundException;
import com.fiap.globalsolution.messaging.LearningPathProducer;
import com.fiap.globalsolution.repository.TrilhaAprendizagemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LearningPathService {

    private static final Logger log = LoggerFactory.getLogger(LearningPathService.class);
    private final TrilhaAprendizagemRepository trilhaRepository;
    private final LearningPathProducer learningPathProducer;

    public LearningPathService(TrilhaAprendizagemRepository trilhaRepository, LearningPathProducer learningPathProducer) {
        this.trilhaRepository = trilhaRepository;
        this.learningPathProducer = learningPathProducer;
    }

    /**
     * Inicia a criação de uma nova trilha, salvando no banco e enviando para a fila.
     * @param request Os dados da requisição.
     * @param userId O ID do usuário autenticado.
     * @return O ID da trilha criada.
     */
    @Transactional
    @CacheEvict(value = "learning-paths", key = "#userId")
    public Long criarTrilha(CreateLearningPathRequest request, Long userId) {
        Long trilhaId = trilhaRepository.prInserirTrilha(userId, request.getTituloObjetivo());

        LearningPathCreateRequest queueRequest = new LearningPathCreateRequest();
        queueRequest.setUserId(userId);
        queueRequest.setTrilhaId(trilhaId);
        queueRequest.setCargoAtual(request.getCargoAtual());
        queueRequest.setTituloObjetivo(request.getTituloObjetivo());

        learningPathProducer.sendGenerationRequest(queueRequest);
        log.info("Requisição para criar trilha com ID {} enviada para a fila.", trilhaId);
        return trilhaId;
    }

    /**
     * Atualiza uma trilha existente com o conteúdo gerado pela IA.
     * @param trilhaId O ID da trilha a ser atualizada.
     * @param conteudoJson O JSON retornado pela IA.
     */
    @Transactional
    public void atualizarTrilhaComConteudoIA(Long trilhaId, String conteudoJson) {
        TrilhaAprendizagem trilha = buscarTrilhaPorId(trilhaId);
        trilha.setDadosJsonIA(conteudoJson);
        trilha.setStatus(StatusTrilha.CONCLUIDA);
        trilhaRepository.save(trilha);
        log.info("Trilha {} atualizada com sucesso com o conteúdo da IA.", trilhaId);
    }

    /**
     * Marca o status de uma trilha como ERRO.
     * @param trilhaId O ID da trilha.
     */
    @Transactional
    public void marcarTrilhaComoErro(Long trilhaId) {
        TrilhaAprendizagem trilha = buscarTrilhaPorId(trilhaId);
        trilha.setStatus(StatusTrilha.ERRO);
        trilhaRepository.save(trilha);
        log.error("Falha ao processar a trilha ID: {}. Status alterado para ERRO.", trilhaId);
    }

    /**
     * Lista todas as trilhas de aprendizado de um usuário específico de forma paginada.
     * @param usuarioId O ID do usuário.
     * @param pageable Configuração de paginação.
     * @return Uma página de trilhas.
     */
    @Cacheable(value = "learning-paths", key = "#usuarioId")
    public Page<TrilhaAprendizagem> listarTrilhas(Long usuarioId, Pageable pageable) {
        return trilhaRepository.findByPerfilUsuarioId(usuarioId, pageable);
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
