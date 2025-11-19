package com.fiap.globalsolution.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AIService {

    private final ChatModel chatModel;

    public AIService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * Gera o conteúdo de uma trilha de aprendizado usando IA Generativa.
     * @param cargoAtual O cargo atual do profissional.
     * @param objetivo O cargo ou habilidade que o profissional deseja alcançar.
     * @return Uma string JSON contendo a trilha de aprendizado sugerida.
     */
    public String gerarTrilha(String cargoAtual, String objetivo) {
        // String tradicional para compatibilidade com Java 8+
        String promptTemplateString =
                "Você é um especialista em desenvolvimento de carreira e requalificação profissional.\n" +
                        "Sua tarefa é criar uma trilha de estudos detalhada para um profissional que atualmente é \"{cargoAtual}\"\n" +
                        "e deseja se tornar um \"{objetivo}\".\n\n" +
                        "A resposta deve ser estritamente um objeto JSON (sem markdown) contendo uma lista de \"passos\".\n" +
                        "Cada passo deve ter os campos: \"titulo\", \"descricao\" e \"tipo\" (Curso, Artigo, Vídeo ou Projeto).\n\n" +
                        "Gere a trilha agora.";

        PromptTemplate template = new PromptTemplate(promptTemplateString);

        // CORREÇÃO: Usando HashMap em vez de Map.of (que exige Java 9+)
        Map<String, Object> variables = new HashMap<>();
        variables.put("cargoAtual", cargoAtual);
        variables.put("objetivo", objetivo);

        Prompt prompt = template.create(variables);

        // Chamada atualizada para a API do ChatModel
        ChatResponse response = chatModel.call(prompt);

        return response.getResult().getOutput().getContent();
    }
}