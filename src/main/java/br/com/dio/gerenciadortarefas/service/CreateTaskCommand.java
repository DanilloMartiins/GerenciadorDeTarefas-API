package br.com.dio.gerenciadortarefas.service;

import java.time.LocalDate;

public record CreateTaskCommand(String title, String description, LocalDate dueDate) {
}
