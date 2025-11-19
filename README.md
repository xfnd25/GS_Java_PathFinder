# Pathfinder AI - FIAP Global Solution

## 1. Visão Geral do Projeto

O **Pathfinder AI** é uma API RESTful de backend desenvolvida como parte da Global Solution da FIAP. O projeto utiliza **Java 17** e **Spring Boot 3** para criar uma plataforma robusta que gera trilhas de aprendizado (`Learning Paths`) personalizadas para requalificação profissional, utilizando o poder da Inteligência Artificial Generativa.

O sistema foi projetado seguindo princípios de arquiteturas distribuídas e reativas, com um fluxo assíncrono para operações de longa duração, garantindo que a API permaneça responsiva e escalável.

### Principais Tecnologias
- **Linguagem/Framework:** Java 17, Spring Boot 3
- **Persistência:** Spring Data JPA, Oracle Database
- **Mensageria:** Spring AMQP, RabbitMQ
- **IA Generativa:** Spring AI (com OpenAI)
- **Segurança:** Spring Security 6 (OAuth2 Resource Server - JWT)
- **Build:** Apache Maven

---

## 2. Arquitetura da Solução

A arquitetura do Pathfinder AI foi desenhada para ser desacoplada e resiliente.

![Arquitetura Simplificada](https://i.imgur.com/diagram.png) <!-- Imagem de exemplo -->

O fluxo principal para a criação de uma trilha de aprendizado é o seguinte:
1.  O cliente (aplicativo mobile/web) envia uma requisição `POST /api/v1/learning-paths` com um token JWT válido, contendo o perfil do usuário e seu objetivo de carreira.
2.  O `LearningPathController` recebe a requisição, valida os dados e chama o `LearningPathService`.
3.  O `LearningPathService` invoca uma **Stored Procedure** no Oracle (`PKG_PERFIS_E_TRILHAS.PR_INSERIR_TRILHA`) para criar um registro inicial da trilha com o status `PENDENTE`.
4.  O Controller retorna imediatamente uma resposta `HTTP 202 Accepted`, informando que a solicitação foi aceita para processamento.
5.  O `LearningPathProducer` publica uma mensagem contendo os detalhes da solicitação em uma fila do RabbitMQ.
6.  O `LearningPathConsumer` escuta a fila, consome a mensagem e aciona o `AIService`.
7.  O `AIService` monta um prompt detalhado e o envia para a API da OpenAI através do Spring AI.
8.  Após receber a trilha gerada pela IA, o Consumer atualiza o registro no banco de dados com o conteúdo JSON e altera o status para `CONCLUIDA` (ou `ERRO` em caso de falha).

---

## 3. Pré-requisitos

Para compilar e executar o projeto localmente, você precisará de:
- **Java JDK 17** ou superior
- **Apache Maven 3.8+**
- **Docker** e **Docker Compose** (recomendado para instanciar Oracle e RabbitMQ)
- Uma **API Key da OpenAI**
- Um **servidor de identidade** (como Keycloak) para gerar os tokens JWT.

---

## 4. Setup e Configuração

### 4.1. Configuração do Ambiente

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/seu-usuario/pathfinder-ai.git
    cd pathfinder-ai
    ```

2.  **Banco de Dados Oracle:**
    - Garanta que você tenha uma instância do Oracle DB acessível.
    - Execute o script `gs_bd.sql` (disponibilizado no contexto do projeto) para criar as tabelas (`USUARIO`, `PERFIL`, `TRILHA_APRENDIZAGEM`) e o package com as Stored Procedures (`PKG_PERFIS_E_TRILHAS`).

3.  **Configure o `application.yml`:**
    - Navegue até `src/main/resources/application.yml`.
    - Atualize as seções com suas credenciais e endpoints:
      - `spring.datasource`: URL, usuário, senha e schema do seu banco Oracle.
      - `spring.rabbitmq`: Endereço do seu servidor RabbitMQ.
      - `spring.security.oauth2.resourceserver.jwt.issuer-uri`: A URI do seu provedor de identidade.
      - `spring.ai.openai.api-key`: Sua chave secreta da API da OpenAI.

### 4.2. Compilando o Projeto

Use o Maven para compilar e instalar as dependências:
```bash
mvn clean install
```

### 4.3. Executando a Aplicação
```bash
mvn spring-boot:run
```
A API estará disponível em `http://localhost:8080`.

---

## 5. Documentação da API

A API está protegida e requer um token JWT no cabeçalho `Authorization`.

**`Authorization: Bearer <seu-token-jwt>`**

### 5.1. Iniciar Geração de Trilha de Aprendizado

- **Endpoint:** `POST /api/v1/learning-paths`
- **Descrição:** Inicia o processo assíncrono de criação de uma trilha de aprendizado.
- **Resposta de Sucesso:** `202 Accepted`
- **Corpo da Requisição (`application/json`):**
  ```json
  {
    "userId": 1,
    "cargoAtual": "Desenvolvedor Java Junior",
    "tituloObjetivo": "Arquiteto de Soluções Cloud"
  }
  ```

### 5.2. Listar Trilhas de Aprendizado

- **Endpoint:** `GET /api/v1/learning-paths`
- **Descrição:** Retorna uma lista paginada de todas as trilhas de aprendizado. O resultado é cacheado.
- **Resposta de Sucesso:** `200 OK`
- **Parâmetros de Query (Opcionais):**
  - `page`: Número da página (padrão: `0`).
  - `size`: Tamanho da página (padrão: `10`).
  - `sort`: Campo para ordenação (ex: `id,desc`).
- **Exemplo de Resposta (`application/json`):**
  ```json
  {
    "content": [
      {
        "idTrilha": 1,
        "idPerfil": 1,
        "tituloObjetivo": "Arquiteto de Soluções Cloud",
        "status": "CONCLUIDA",
        "dadosJsonIA": "{\\\"trilha\\\":[...]}"
      }
    ],
    "pageable": { ... },
    "totalPages": 1,
    "totalElements": 1,
    "last": true,
    "first": true,
    "numberOfElements": 1,
    "empty": false
  }
  ```

---

## 6. Detalhes da Implementação

### Estrutura dos Pacotes
- **`config`**: Configurações de Beans do Spring (Segurança, RabbitMQ, Cache, I18n).
- **`controller`**: Camada de entrada da API (REST Controllers).
- **`domain`**: Entidades JPA que mapeiam o banco de dados.
- **`dto`**: _Data Transfer Objects_ para desacoplar a API das entidades de domínio.
- **`exception`**: Classes de exceção personalizadas e um handler global (`@RestControllerAdvice`).
- **`messaging`**: Classes `Producer` e `Consumer` para interação com o RabbitMQ.
- **`repository`**: Interfaces do Spring Data JPA, incluindo as chamadas para as Stored Procedures com `@Procedure`.
- **`service`**: Contém a lógica de negócio principal da aplicação.

### Integração com Oracle Stored Procedures

Para atender ao requisito de negócio, as operações de `INSERT` críticas não utilizam o método `save()` do JPA diretamente. Em vez disso, mapeamos as procedures do package `PKG_PERFIS_E_TRILHAS` nos repositórios, garantindo que a lógica de negócio do banco de dados seja respeitada.

**Exemplo (`TrilhaAprendizagemRepository.java`):**
```java
@Procedure(procedureName = "PKG_PERFIS_E_TRILHAS.PR_INSERIR_TRILHA")
Long prInserirTrilha(
    @Param("p_id_usuario") Long idUsuario,
    @Param("p_titulo_objetivo") String tituloObjetivo
);
```
