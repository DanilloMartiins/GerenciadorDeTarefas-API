# GerenciadorDeTarefas API

Projeto Maven em Java (sem Spring Boot) para gerenciamento de boards e tarefas, usando arquitetura em camadas:

- `domain`: regras e entidades principais.
- `repository`: persistência em memória.
- `service`: regras de negócio e validações.
- `web`: API HTTP/JSON.

## Requisitos

- Java 17+
- Maven 3.9+

## Como executar

```bash
mvn clean test
mvn exec:java
```

A API sobe em `http://localhost:8080`.

## Endpoints

### Health

```http
GET /health
```

### Boards

```http
POST /boards
Content-Type: application/json

{
  "name": "Board Estudos"
}
```

```http
GET /boards
```

```http
GET /boards/{boardId}
```

### Tarefas

```http
POST /boards/{boardId}/tasks
Content-Type: application/json

{
  "title": "Implementar endpoint",
  "description": "Criar POST /boards",
  "dueDate": "2026-04-10"
}
```

```http
GET /boards/{boardId}/tasks
```

```http
PATCH /boards/{boardId}/tasks/{taskId}/status
Content-Type: application/json

{
  "status": "IN_PROGRESS"
}
```

```http
DELETE /boards/{boardId}/tasks/{taskId}
```

## Observações

- Status disponíveis: `TODO`, `IN_PROGRESS`, `DONE`.
- Erros de validação retornam `400`.
- Entidades não encontradas retornam `404`.
