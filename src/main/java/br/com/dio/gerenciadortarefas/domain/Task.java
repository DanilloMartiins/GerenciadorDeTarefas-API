package br.com.dio.gerenciadortarefas.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Task {
    private final UUID id;
    private final String title;
    private final String description;
    private final LocalDate dueDate;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private TaskStatus status;

    public Task(UUID id, String title, String description, LocalDate dueDate) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.title = Objects.requireNonNull(title, "title is required");
        this.description = description;
        this.dueDate = dueDate;
        this.status = TaskStatus.TODO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void changeStatus(TaskStatus status) {
        this.status = Objects.requireNonNull(status, "status is required");
        this.updatedAt = LocalDateTime.now();
    }
}
