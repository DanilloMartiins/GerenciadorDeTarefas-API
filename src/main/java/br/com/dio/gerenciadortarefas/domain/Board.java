package br.com.dio.gerenciadortarefas.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Board {
    private final UUID id;
    private final String name;
    private final LocalDateTime createdAt;
    private final Map<UUID, Task> tasksById;

    public Board(UUID id, String name) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.name = Objects.requireNonNull(name, "name is required");
        this.createdAt = LocalDateTime.now();
        this.tasksById = new LinkedHashMap<>();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Collection<Task> getTasks() {
        return new ArrayList<>(tasksById.values());
    }

    public Task addTask(Task task) {
        tasksById.put(task.getId(), task);
        return task;
    }

    public Optional<Task> findTaskById(UUID taskId) {
        return Optional.ofNullable(tasksById.get(taskId));
    }

    public boolean removeTask(UUID taskId) {
        return tasksById.remove(taskId) != null;
    }
}
