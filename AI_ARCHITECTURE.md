# Arquitetura do Módulo de IA Generativa

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring AI](https://img.shields.io/badge/Spring_AI-0.8-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.12-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)
![Google Gemini](https://img.shields.io/badge/Google_Gemini-2.0-8E75B2?style=for-the-badge&logo=google-bard&logoColor=white)

---

> **⚠️ IMPORTANTE: LOCALIZAÇÃO DO CÓDIGO / INTEGRAÇÃO**
>
> A implementação da IA **NÃO** é um script externo ou isolado. Ela encontra-se totalmente integrada ao Backend principal (Spring Boot Java), localizada no diretório:
>
> `src/main/java/com/fiap/globalsolution/messaging`
>
> O código utiliza o **Spring AI** nativo para orquestrar a comunicação com o LLM dentro do ciclo de vida da aplicação Java, conforme exigido nos requisitos de integração.

---

## 🏗️ Arquitetura da Solução

O módulo de Inteligência Artificial foi desenhado para operar de forma assíncrona e resiliente, garantindo que a geração de conteúdo (que pode ser lenta) não bloqueie a experiência do usuário na API REST.

### Fluxo de Dados

1.  **Requisição API**: O cliente envia uma solicitação para criar uma trilha de aprendizado.
2.  **Enfileiramento (Producer)**: A Controller valida os dados e envia uma mensagem para a Fila do **RabbitMQ**.
3.  **Processamento Assíncrono (Consumer)**: O Consumer Java (`LearningPathConsumer`) lê a mensagem da fila.
4.  **Geração de Conteúdo (Spring AI)**: O Consumer invoca o **Google Gemini** através do Spring AI.
5.  **Persistência (Oracle)**: O Consumer processa a resposta e salva o resultado no banco de dados **Oracle**.

**Nota Arquitetural:** Todos os componentes (Producer, Consumer, Cliente AI) rodam dentro da mesma aplicação (JVM), não se tratando de um microsserviço separado.

## 🧠 Engenharia de Prompt (Prompt Engineering)

Para garantir a qualidade e a integridade dos dados gerados, aplicamos técnicas específicas de engenharia de prompt:

### 1. Persona (System Message)
Utilizamos uma "System Message" para configurar o modelo com a persona de um **Especialista de Carreira**. Isso garante que as sugestões de trilhas sejam profissionais, relevantes e pedagógicas.

### 2. Strict Output Formatting (JSON)
Para permitir que o Java consuma a resposta da IA sem erros, utilizamos uma técnica de **Strict Output Formatting**:
-   Forçamos o Gemini a responder **apenas** um objeto JSON válido.
-   Instruímos explicitamente o modelo a não usar blocos de código Markdown (como \`\`\`json) ou texto conversacional adicional.
-   Isso garante que o parser do Java consiga ler a resposta e mapeá-la diretamente para as entidades do sistema antes de salvar no Oracle.

## 🛠️ Tecnologias

*   **Spring AI**: Framework para integração Java com modelos de IA.
*   **Google Gemini**: Modelo Generativo (LLM) utilizado.
*   **RabbitMQ**: Middleware de mensageria para processamento assíncrono.
*   **Oracle Database**: Banco de dados relacional para persistência.

---
*Documentação gerada para a entrega da Global Solution - Disruptive Architectures.*
