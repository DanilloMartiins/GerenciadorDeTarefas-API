package br.com.dio.gerenciadortarefas.web.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record BoardResponse(
        UUID id,
        String name,
        LocalDateTime createdAt,
        List<TaskResponse> tasks
) {
}
