# Guia de Integração Frontend (Pathfinder AI)

Este guia destina-se aos desenvolvedores Front-end e Mobile para facilitar a integração com a API do Pathfinder AI.

## 🛠 Guia Rápido de Consumo

### 1. URLs
*   **Base URL Local:** `http://localhost:8080`
*   **Base URL Produção:** (A definir)

### 2. Auth Flow
A autenticação é baseada em JWT (JSON Web Token).

*   **Passo 1: Registro (`POST /auth/register`)**
    *   Envia os dados do usuário para criar uma conta.
    *   **Payload:**
        ```json
        {
          "nome": "João Silva",
          "email": "joao@email.com",
          "senha": "senhaForte123"
        }
        ```
    *   **Retorno:** 201 Created (com UserResponse no corpo).

*   **Passo 2: Login (`POST /auth/login`)**
    *   Autentica o usuário e recebe o token.
    *   **Payload:**
        ```json
        {
          "email": "joao@email.com",
          "senha": "senhaForte123"
        }
        ```
    *   **Retorno:**
        ```json
        {
          "token": "eyJhbGciOiJIUzI1NiJ9...",
          "userId": 1,
          "nome": "João Silva"
        }
        ```

*   **Passo 3: Armazenar o Token**
    *   Salve o `token` (localStorage, AsyncStorage, SecureStore).
    *   Ele expira em 2 horas (default).

### 3. Headers
Para todas as requisições protegidas (quase todas, exceto auth), envie o header:
```http
Authorization: Bearer <SEU_TOKEN_AQUI>
```

---

## 📱 Telas e Endpoints (Mapeamento)

### 📌 Tela "Meus Objetivos" (Listagem)
*   **Endpoint:** `GET /api/v1/learning-paths`
*   **Descrição:** Lista todas as trilhas de aprendizagem do usuário logado.
*   **Paginação:** A API suporta paginação (Spring Pageable).
    *   Ex: `GET /api/v1/learning-paths?page=0&size=10&sort=id,desc`

### 📌 Tela "Novo Objetivo" (Criação)
*   **Endpoint:** `POST /api/v1/learning-paths`
*   **Payload:**
    ```json
    {
      "tituloObjetivo": "Aprender Java Spring Boot",
      "descricao": "Quero me tornar um desenvolvedor backend sênior.",
      "nivelAtual": "INICIANTE",
      "tempoDisponivel": "2 horas por dia"
    }
    ```
*   **⚠️ Aviso Crítico (Async):**
    *   Este endpoint retorna **202 Accepted**.
    *   A geração da trilha pela IA leva cerca de **10 a 15 segundos**.
    *   **Comportamento do App:** Exiba um loading ou uma mensagem "Gerando sua trilha personalizada...".
    *   **Polling:** O App deve consultar a lista (`GET`) periodicamente ou aguardar o usuário fazer "pull-to-refresh" para ver o status mudar de `PROCESSANDO` para `CONCLUIDA`.

### 📌 Tela "Detalhes da Trilha" (Visualização)
*   **Endpoint:** `GET /api/v1/learning-paths/{id}`
*   **Payload de Resposta:**
    O campo `dadosJsonIA` agora é um Objeto JSON estruturado, pronto para uso.
    ```json
    {
      "idTrilha": 10,
      "tituloObjetivo": "Aprender Java",
      "status": "CONCLUIDA",
      "dadosJsonIA": {
        "titulo": "Trilha Java Backend",
        "modulos": [
          {
            "titulo": "Introdução",
            "conteudo": "História do Java..."
          },
          {
            "titulo": "Sintaxe Básica",
            "conteudo": "Variáveis, Loops..."
          }
        ]
      }
    }
    ```

---

## 💻 Interfaces TypeScript

Copie e cole estas interfaces no seu projeto TypeScript.

```typescript
// Auth
export interface LoginRequest {
  email: string;
  senha: string;
}

export interface LoginResponse {
  token: string;
  userId: number;
  nome: string;
}

export interface RegisterRequest {
  nome: string;
  email: string;
  senha: string;
}

// Enums
export type StatusTrilha = 'PROCESSANDO' | 'CONCLUIDA' | 'ERRO';

// Learning Path
export interface LearningPathCreateRequest {
  tituloObjetivo: string;
  descricao?: string; // Opcional, dependendo da implementação
  nivelAtual?: string;
  tempoDisponivel?: string;
}

export interface LearningPathResponse {
  idTrilha: number;
  idPerfil: number;
  tituloObjetivo: string;
  status: StatusTrilha;
  dadosJsonIA: any; // Objeto JSON dinâmico da IA
}

export interface LearningPathDetailResponse extends LearningPathResponse {
  // Pode ter campos extras se necessário
}

// Exemplo de estrutura comum da IA (pode variar conforme o prompt)
export interface IAResponseStructure {
  titulo: string;
  descricao: string;
  modulos: IAModulo[];
}

export interface IAModulo {
  titulo: string;
  topicos: string[];
  duracaoEstimada: string;
}
```
