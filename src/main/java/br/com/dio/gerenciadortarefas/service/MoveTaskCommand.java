package br.com.dio.gerenciadortarefas.service;

import br.com.dio.gerenciadortarefas.domain.TaskStatus;

public record MoveTaskCommand(TaskStatus status) {
}
