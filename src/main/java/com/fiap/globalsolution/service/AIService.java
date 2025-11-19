package com.fiap.globalsolution.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AIService {

    // Na versão 1.0.0-M1, usamos ChatModel em vez de ChatClient antigo
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
        String promptTemplateString = """
            Você é um especialista em desenvolvimento de carreira e requalificação profissional.
            Sua tarefa é criar uma trilha de estudos detalhada para um profissional que atualmente é "{cargoAtual}"
            e deseja se tornar um "{objetivo}".

            A resposta deve ser um objeto JSON contendo uma lista de "passos".
            Cada passo deve ter os seguintes campos:
            - "titulo": Um título curto para o passo de estudo.
            - "descricao": Uma explicação detalhada do que deve ser estudado neste passo.
            - "tipo": O tipo de conteúdo (ex: "Artigo", "Vídeo", "Curso", "Projeto Prático").

            Exemplo de formato de resposta:
            {
              "trilha": [
                {
                  "titulo": "Fundamentos de Python",
                  "descricao": "Revisar os conceitos básicos de Python, incluindo tipos de dados, laços e funções.",
                  "tipo": "Curso"
                },
                {
                  "titulo": "Análise de Dados com Pandas",
                  "descricao": "Aprender a manipular e analisar dados usando a biblioteca Pandas.",
                  "tipo": "Projeto Prático"
                }
              ]
            }

            Por favor, gere a trilha de estudos para o perfil fornecido.
            """;

        PromptTemplate template = new PromptTemplate(promptTemplateString);
        Prompt prompt = template.create(Map.of("cargoAtual", cargoAtual, "objetivo", objetivo));

        // Chamada atualizada para a API do ChatModel
        ChatResponse response = chatModel.call(prompt);

        return response.getResult().getOutput().getContent();
    }
}