# Pathfinder AI - Módulo de Inteligência Artificial Generativa

## 📘 Visão Geral
Este documento detalha a arquitetura técnica, as estratégias de engenharia de prompt e os mecanismos de integração do módulo de **Inteligência Artificial Generativa** da solução **Pathfinder AI**. Este módulo é o núcleo inteligente da plataforma, responsável por analisar perfis profissionais e gerar trilhas de aprendizado (Learning Paths) personalizadas para Upskilling e Reskilling.

A solução foi projetada para atender aos requisitos da Global Solution de **Disruptive Architectures: IoT, IoB & Generative IA**, focando em uma implementação robusta, assíncrona e totalmente integrada ao ecossistema Java Enterprise.

---

## 🏗️ Arquitetura da IA

A implementação da IA não opera de forma isolada; ela é parte de um fluxo distribuído e **não bloqueante** que garante escalabilidade e resiliência. A arquitetura segue o padrão de **Event-Driven Architecture (EDA)**.

A implementação técnica encontra-se principalmente no pacote: `src/main/java/com/fiap/globalsolution/messaging`.

### Fluxo de Processamento (Pipeline Assíncrono)

1.  **Solicitação (API REST):**
    *   O usuário solicita uma nova trilha via API (`POST /api/v1/learning-paths`).
    *   A aplicação valida a requisição, persiste o registro inicial no **Oracle Database** com status `PENDENTE` e publica um evento na fila do **RabbitMQ**.
    *   **Resposta Imediata:** A API retorna `HTTP 202 Accepted` ao cliente, liberando a thread de processamento HTTP.

2.  **Consumo e Orquestração (Messaging):**
    *   O componente `LearningPathConsumer` (localizado em `src/main/java/com/fiap/globalsolution/messaging`) escuta a fila.
    *   Ao receber a mensagem, ele aciona o `AIService` para iniciar a interação com o modelo generativo.

3.  **Geração de Conteúdo (Spring AI + Gemini):**
    *   O `AIService` constrói o prompt e envia a requisição para a API do **Google Gemini** (modelo `gemini-2.0-flash`) utilizando o framework **Spring AI**.
    *   A comunicação é feita através de uma interface OpenAI-compatible (`/v1beta/openai`), configurada para máxima compatibilidade e desempenho.

4.  **Persistência (Integração com Oracle):**
    *   A resposta da IA (um JSON estruturado) é processada e persistida no banco de dados através de **Stored Procedures**, atualizando o status da trilha para `CONCLUIDA`.

---

## 🧠 Prompt Engineering

Para garantir que o modelo atue como um componente de software determinístico e confiável, utilizamos técnicas avançadas de Prompt Engineering, focadas em **Few-Shot Prompting** e **Strict Output Formatting**.

### Estratégia de Design do Prompt

O prompt não é uma simples pergunta; é um conjunto de instruções de sistema projetado para:

1.  **Adoção de Persona:** Instruímos explicitamente o modelo a atuar como um **Especialista Sênior em Carreira e Educação Corporativa**. Isso calibra o tom, o vocabulário e a qualidade das recomendações.
2.  **Restrição de Formato (JSON Enforcement):** Para garantir a integração perfeita com o Backend Java, o prompt proíbe qualquer texto conversacional ("Claro, aqui está...") ou formatação Markdown (code fences). A saída é forçada a ser um **JSON puro e válido**.

### Exemplo de Estrutura do Prompt

```text
Atue como um especialista em carreira.
Gere uma trilha de aprendizado para um [Cargo Atual] que deseja se tornar [Cargo Objetivo].

REGRAS RÍGIDAS DE SAÍDA:
1. Retorne APENAS um objeto JSON.
2. NÃO use markdown (```json).
3. O JSON deve seguir exatamente este schema:
{
  "titulo": "Nome da Trilha",
  "descricao": "Visão geral",
  "passos": [
    { "titulo": "...", "tipo": "CURSO", "descricao": "..." }
  ]
}
```

### Sanitização e Robustez
Apesar das instruções rígidas, implementamos uma camada de defesa no código (`AIService`) que sanitiza a resposta da IA. Um algoritmo de Regex remove potenciais blocos de markdown ou espaços em branco indesejados antes de tentar a desserialização, garantindo que o sistema não falhe devido a "alucinações de formato" do modelo.

---

## 🛠️ Tecnologias Utilizadas

O módulo de IA é construído sobre uma stack tecnológica moderna, alinhada com os padrões de mercado:

*   **Spring AI:** Framework que simplifica a integração com LLMs, oferecendo abstrações para clientes de chat, prompts e parsers de saída.
*   **Google Gemini API:** O motor de inteligência por trás da solução, escolhido por sua capacidade de raciocínio lógico e ampla janela de contexto.
*   **RabbitMQ:** Broker de mensageria essencial para o desacoplamento entre a interface do usuário e o processamento pesado da IA.
*   **Oracle Database & PL/SQL:** Utilizado para a persistência segura e performática dos dados gerados, via Stored Procedures.

---

## 🔗 Integração

A IA no **Pathfinder AI** não é um script Python solto ou uma função serverless isolada. Ela é um cidadão de primeira classe da aplicação Java Spring Boot.

*   **Contexto de Segurança:** O processamento da IA respeita as regras de negócio e a segurança dos dados do usuário.
*   **Gerenciamento de Transações:** A persistência dos resultados da IA participa das transações do Spring, assegurando consistência atômica.
*   **Manutenibilidade:** O código da IA reside junto ao código de domínio (`src/main/java/.../service` e `messaging`), facilitando testes, refatoração e evolução contínua.

Essa abordagem demonstra uma arquitetura madura, onde a IA Generativa é utilizada como um **serviço de infraestrutura** integrado, potencializando o valor de negócio sem comprometer a estabilidade do sistema.
