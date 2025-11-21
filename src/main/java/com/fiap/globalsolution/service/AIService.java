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

        String rawOutput = response.getResult().getOutput().getContent();
        return cleanJsonOutput(rawOutput);
    }

    /**
     * Limpa a saída da IA para garantir que apenas o JSON seja retornado,
     * removendo delimitadores de Markdown como ```json ou ```.
     *
     * @param output A string original retornada pela IA.
     * @return A string contendo apenas o JSON.
     */
    private String cleanJsonOutput(String output) {
        if (output == null || output.trim().isEmpty()) {
            return output;
        }

        String cleaned = output.trim();

        // Remove o início do bloco de código Markdown (ex: ```json ou ```)
        if (cleaned.startsWith("```")) {
            int firstNewLine = cleaned.indexOf('\n');
            if (firstNewLine != -1) {
                cleaned = cleaned.substring(firstNewLine + 1);
            } else {
                // Se começar com ``` mas não tiver quebra de linha, pode ser algo estranho,
                // mas vamos tentar remover os 3 primeiros caracteres ou procurar o JSON.
                // Melhor estratégia: encontrar o primeiro '{' ou '['.
            }
        }

        // Estratégia mais robusta: encontrar o primeiro '{' ou '[' e o último '}' ou ']'
        int firstBrace = cleaned.indexOf('{');
        int firstBracket = cleaned.indexOf('[');
        int start = -1;

        if (firstBrace != -1 && firstBracket != -1) {
            start = Math.min(firstBrace, firstBracket);
        } else if (firstBrace != -1) {
            start = firstBrace;
        } else if (firstBracket != -1) {
            start = firstBracket;
        }

        if (start != -1) {
            cleaned = cleaned.substring(start);
        }

        // Remove o final do bloco de código Markdown (```)
        int lastBrace = cleaned.lastIndexOf('}');
        int lastBracket = cleaned.lastIndexOf(']');
        int end = -1;

        if (lastBrace != -1 && lastBracket != -1) {
            end = Math.max(lastBrace, lastBracket);
        } else if (lastBrace != -1) {
            end = lastBrace;
        } else if (lastBracket != -1) {
            end = lastBracket;
        }

        if (end != -1) {
            cleaned = cleaned.substring(0, end + 1);
        }

        return cleaned;
    }
}
