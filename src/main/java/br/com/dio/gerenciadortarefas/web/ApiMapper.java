package br.com.dio.gerenciadortarefas.web;

import br.com.dio.gerenciadortarefas.domain.Board;
import br.com.dio.gerenciadortarefas.domain.Task;
import br.com.dio.gerenciadortarefas.web.dto.BoardResponse;
import br.com.dio.gerenciadortarefas.web.dto.TaskResponse;

import java.util.List;

public final class ApiMapper {
    private ApiMapper() {
    }

    public static BoardResponse toBoardResponse(Board board) {
        return new BoardResponse(
                board.getId(),
                board.getName(),
                board.getCreatedAt(),
                board.getTasks().stream().map(ApiMapper::toTaskResponse).toList()
        );
    }

    public static TaskResponse toTaskResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    public static List<BoardResponse> toBoardResponseList(List<Board> boards) {
        return boards.stream().map(ApiMapper::toBoardResponse).toList();
    }

    public static List<TaskResponse> toTaskResponseList(List<Task> tasks) {
        return tasks.stream().map(ApiMapper::toTaskResponse).toList();
    }
}
