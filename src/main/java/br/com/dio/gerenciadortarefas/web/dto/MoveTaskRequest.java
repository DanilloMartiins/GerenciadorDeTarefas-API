package br.com.dio.gerenciadortarefas.web.dto;

import br.com.dio.gerenciadortarefas.domain.TaskStatus;

public record MoveTaskRequest(TaskStatus status) {
}
