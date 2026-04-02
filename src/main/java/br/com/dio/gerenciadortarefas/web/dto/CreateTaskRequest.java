package br.com.dio.gerenciadortarefas.web.dto;

import java.time.LocalDate;

public record CreateTaskRequest(String title, String description, LocalDate dueDate) {
}
