Pathfinder AI - Módulo de Inteligência Artificial Generativa
Este documento detalha a arquitetura, engenharia e integração do módulo de Inteligência Artificial Generativa da solução Pathfinder AI. Este módulo é responsável por gerar trilhas de aprendizado personalizadas (Upskilling/Reskilling) utilizando modelos de linguagem de última geração.

Arquitetura da IA
A arquitetura do módulo de IA foi projetada para ser assíncrona, resiliente e escalável, evitando gargalos na API principal e garantindo uma experiência de usuário fluida mesmo durante operações computacionalmente intensivas.

Fluxo de Processamento
Solicitação na API (Trigger):

O usuário solicita uma nova trilha através da API Java (POST /api/v1/learning-paths), informando seu cargo atual e objetivo profissional.
A API valida a requisição, persiste o estado inicial no Oracle Database como PENDENTE e publica uma mensagem na exchange learning-paths-exchange do RabbitMQ.
A API retorna imediatamente um status 202 Accepted, liberando o cliente enquanto o processamento ocorre em background.
Processamento Assíncrono (Consumer):

Um componente LearningPathConsumer (Listener) escuta a fila e captura a mensagem de solicitação.
Este consumidor é responsável por orquestrar a interação com o modelo de IA, isolando a lógica pesada do fluxo HTTP.
Geração de Conteúdo (Spring AI + Gemini):

O consumidor invoca o AIService, que constrói um prompt estruturado e realiza a chamada à API do Google Gemini utilizando o Spring AI.
O Spring AI gerencia a comunicação, timeouts e parsing da resposta.
Persistência e Finalização:

A resposta JSON gerada pelo Gemini é higienizada e validada.
O resultado é persistido no banco de dados Oracle através de uma Stored Procedure, atualizando o status da trilha para CONCLUIDA.
Esta abordagem desacoplada permite que o sistema processe picos de solicitações sem degradar a performance da API REST, garantindo robustez e alta disponibilidade.

Prompt Engineering
Para garantir que o modelo de linguagem (LLM) atue como um componente determinístico do sistema e não apenas como um chatbot conversacional, aplicamos técnicas rigorosas de Prompt Engineering.

Estratégia de Prompting
Utilizamos uma combinação de Persona Adoption (Adoção de Persona) com Strict Output Formatting (Formatação Rigorosa de Saída).

Persona: O modelo é instruído a assumir o papel de um "especialista sênior em desenvolvimento de carreira e requalificação profissional", garantindo que o tom e a qualidade do conteúdo sejam adequados ao contexto corporativo.
Restrição de Formato (JSON): Para assegurar a interoperabilidade com o backend Java, o prompt impõe uma restrição forte: a saída deve ser estritamente um objeto JSON.
Exemplo de Instrução (Code Snippet)
O prompt é construído dinamicamente no AIService:

String promptTemplateString =
    "Você é um especialista em desenvolvimento de carreira e requalificação profissional.\n" +
    "Sua tarefa é criar uma trilha de estudos detalhada para um profissional que atualmente é \"{cargoAtual}\"\n" +
    "e deseja se tornar um \"{objetivo}\".\n\n" +
    "A resposta deve ser estritamente um objeto JSON (sem markdown) contendo uma lista de \"passos\".\n" +
    "Cada passo deve ter os campos: \"titulo\", \"descricao\" e \"tipo\" (Curso, Artigo, Vídeo ou Projeto).\n\n" +
    "Gere a trilha agora.";
Sanitização de Saída (Post-Processing)
Mesmo com instruções claras, LLMs podem ocasionalmente incluir "fences" de markdown (ex: ```json). Implementamos uma camada de sanitização (cleanJsonOutput) que utiliza regex e manipulação de strings para extrair o payload JSON válido antes da desserialização, garantindo que o sistema seja imune a variações na formatação da resposta do modelo.

Tecnologias
O ecossistema de IA é sustentado por uma stack moderna e robusta:

Spring AI: Framework que abstrai a complexidade de integração com LLMs, oferecendo uma interface fluida e idiomática para desenvolvedores Java.
Google Gemini API: Modelo de IA generativa de alta performance, escolhido por sua capacidade de raciocínio complexo e grande janela de contexto.
RabbitMQ: Broker de mensageria que viabiliza a arquitetura orientada a eventos, essencial para o desacoplamento entre a API e o motor de IA.
Jackson: Utilizado para o processamento e validação da estrutura JSON retornada pela IA.
Integração
A Inteligência Artificial no Pathfinder AI não é um script isolado ou um "add-on" externo; ela é um cidadão de primeira classe dentro da arquitetura Java Enterprise.

Segurança Unificada: O fluxo de IA respeita o contexto de segurança da aplicação.
Transacionalidade: As operações de persistência do resultado da IA participam das transações gerenciadas pelo Spring, garantindo consistência de dados.
Monitoramento: Logs e métricas do processamento da IA são centralizados, permitindo rastreabilidade completa desde o clique do usuário até a geração da trilha.
Esta integração profunda assegura que a solução seja manutenível, testável e pronta para ambientes de produção corporativos.
