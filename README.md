# Pathfinder AI - FIAP Global Solution

## 👨‍💻 Integrantes do Grupo
* **Fernando Pacheco** - RM555317
* **Guilherme Jardim** - RM556814

---

## 1. Visão Geral do Projeto

O **Pathfinder AI** é uma API RESTful de backend desenvolvida como parte da Global Solution da FIAP. O projeto utiliza **Java 17** e **Spring Boot 3** para criar uma plataforma robusta que gera trilhas de aprendizado (`Learning Paths`) personalizadas para requalificação profissional, utilizando o poder da Inteligência Artificial Generativa do Google Gemini.

O sistema foi projetado seguindo princípios de arquiteturas distribuídas e reativas, com um fluxo assíncrono para operações de longa duração e um sistema de **autenticação JWT nativo**, garantindo que a API seja segura, responsiva e escalável.

### Principais Tecnologias
- **Linguagem/Framework:** Java 17, Spring Boot 3
- **Persistência:** Spring Data JPA, Oracle Database
- **Mensageria:** Spring AMQP, RabbitMQ
- **IA Generativa:** Spring AI (com **Google Gemini**)
- **Segurança:** Spring Security 6 (com **autenticação JWT nativa**)
- **Build:** Apache Maven

---

## 2. Arquitetura da Solução

A arquitetura do Pathfinder AI foi desenhada para ser desacoplada, resiliente e segura.

### Fluxo da Aplicação
1.  **Registro e Login:**
    *   O usuário se registra na plataforma através do endpoint `POST /auth/register`.
    *   Em seguida, realiza o login via `POST /auth/login`, fornecendo suas credenciais. A API valida os dados e retorna um **Token JWT Bearer**.

2.  **Criação da Trilha de Aprendizado:**
    *   O cliente envia uma requisição `POST /api/v1/learning-paths`, incluindo o Token JWT no cabeçalho `Authorization`.
    *   O `SecurityFilter` valida o token, garantindo que a requisição seja autêntica e autorizada.
    *   O `LearningPathController` recebe a requisição e invoca o `LearningPathService`.
    *   O serviço chama uma **Stored Procedure** no Oracle para criar um registro da trilha com status `PENDENTE`.
    *   A API retorna imediatamente uma resposta `HTTP 202 Accepted`.

3.  **Processamento Assíncrono com IA:**
    *   O `LearningPathService` envia uma mensagem para uma fila do RabbitMQ.
    *   Um `Consumer` processa a mensagem, monta um prompt detalhado e o envia para a API do **Google Gemini** através do Spring AI.
    *   Após receber a resposta da IA, o Consumer atualiza o registro no banco de dados com o conteúdo gerado e altera o status para `CONCLUIDA`.

4.  **Consulta de Resultados:**
    *   O usuário pode consultar o status e o conteúdo de suas trilhas através do endpoint `GET /api/v1/learning-paths`, autenticando-se com o mesmo token JWT.

### Nota sobre Stored Procedures
As operações de escrita (inserção, atualização e exclusão) são realizadas exclusivamente através de **Stored Procedures** do Oracle. Essa abordagem centraliza a lógica de negócio no banco de dados, garantindo a integridade dos dados e o cumprimento das regras de negócio.

---

## 3. Pré-requisitos

Para compilar e executar o projeto localmente, você precisará de:
- **Java JDK 17** ou superior
- **Apache Maven 3.8+**
- **Docker** e **Docker Compose**
- Uma **API Key do Google Gemini**

---

## 4. Como Rodar (Setup)

### 4.1. Configuração do Ambiente

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/seu-usuario/pathfinder-ai.git
    cd pathfinder-ai
    ```

2.  **Inicie a Infraestrutura com Docker Compose:**
    O arquivo `docker-compose.yml` na raiz do projeto provisiona os containers do **Oracle Database** e do **RabbitMQ**.
    ```bash
    docker-compose up -d
    ```

3.  **Configure o `application.yml`:**
    -   Navegue até `src/main/resources/application.yml`.
    -   Insira sua chave da API do Google Gemini no campo `spring.ai.openai.api-key`.
        ```yaml
        spring:
          ai:
            openai:
              base-url: https://generativelanguage.googleapis.com/v1beta/openai
              api-key: SUA_CHAVE_API_GEMINI_AQUI
        ```

4.  **Execute o Script do Banco de Dados:**
    -   Conecte-se ao banco de dados Oracle e execute o script `gs_bd.sql` para criar as tabelas e as Stored Procedures.

### 4.2. Compilando e Executando

1.  **Compile o projeto:**
    ```bash
    mvn clean install
    ```

2.  **Execute a aplicação:**
    ```bash
    mvn spring-boot:run
    ```
    A API estará disponível em `http://localhost:8080`.

### 4.3. Como Rodar os Testes
Para executar a suíte de testes unitários e de integração, utilize o seguinte comando Maven:
```bash
mvn test
```

---

## 5. Documentação da API (Endpoints)

A API utiliza um sistema de autenticação JWT. Todos os endpoints de negócio requerem um token no cabeçalho `Authorization`.

**`Authorization: Bearer <seu-token-jwt>`**

### Endpoints de Autenticação
-   **`POST /auth/register`**: Registra um novo usuário.
    - **Exemplo de Request Body:**
      ```json
      {
        "nome": "Usuário Teste",
        "email": "teste@email.com",
        "senha": "senhaSegura123"
      }
      ```

-   **`POST /auth/login`**: Autentica um usuário e retorna um token JWT.
    - **Exemplo de Request Body:**
      ```json
      {
        "email": "teste@email.com",
        "senha": "senhaSegura123"
      }
      ```
    - **Exemplo de Response Body:**
      ```json
      {
        "token": "seu.jwt.token.aqui"
      }
      ```

### Endpoints de Negócio
-   **`POST /api/v1/learning-paths`**: Cria uma nova trilha de aprendizado (requer autenticação).
    - **Exemplo de Request Body:**
      ```json
      {
        "userId": 1,
        "cargoAtual": "Analista de Suporte",
        "tituloObjetivo": "Engenheiro de Machine Learning"
      }
      ```

-   **`GET /api/v1/learning-paths`**: Lista as trilhas do usuário autenticado.

-   **`GET /api/v1/learning-paths/{id}`**: Obtém detalhes de uma trilha específica.

A documentação completa, incluindo os DTOs de resposta, está disponível no **Swagger UI** em `http://localhost:8080/swagger-ui.html`.
