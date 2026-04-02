package br.com.dio.gerenciadortarefas.service;

import br.com.dio.gerenciadortarefas.domain.Board;
import br.com.dio.gerenciadortarefas.domain.Task;
import br.com.dio.gerenciadortarefas.domain.TaskStatus;
import br.com.dio.gerenciadortarefas.repository.BoardRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BoardService {
    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = Objects.requireNonNull(boardRepository, "boardRepository is required");
    }

    public Board createBoard(CreateBoardCommand command) {
        if (command == null || isBlank(command.name())) {
            throw new ValidationException("Nome do board é obrigatório.");
        }

        // O serviço gera o identificador para evitar colisão entre clientes.
        Board board = new Board(UUID.randomUUID(), command.name().trim());
        return boardRepository.save(board);
    }

    public List<Board> listBoards() {
        return boardRepository.findAll().stream()
                .sorted(Comparator.comparing(Board::getCreatedAt))
                .toList();
    }

    public Board findBoard(UUID boardId) {
        return getBoardOrThrow(boardId);
    }

    public Task addTask(UUID boardId, CreateTaskCommand command) {
        Board board = getBoardOrThrow(boardId);

        if (command == null || isBlank(command.title())) {
            throw new ValidationException("Título da tarefa é obrigatório.");
        }

        // Nova tarefa sempre nasce em TODO (regra definida na entidade Task).
        Task task = new Task(UUID.randomUUID(), command.title().trim(), safeTrim(command.description()), command.dueDate());
        board.addTask(task);
        // Mesmo em memória, persiste explicitamente para manter a semântica de repositório.
        boardRepository.save(board);
        return task;
    }

    public List<Task> listTasks(UUID boardId) {
        Board board = getBoardOrThrow(boardId);
        return board.getTasks().stream()
                .sorted(Comparator.comparing(Task::getCreatedAt))
                .toList();
    }

    public Task moveTask(UUID boardId, UUID taskId, MoveTaskCommand command) {
        Board board = getBoardOrThrow(boardId);
        Task task = getTaskOrThrow(board, taskId);

        if (command == null || command.status() == null) {
            throw new ValidationException("Novo status da tarefa é obrigatório.");
        }

        // Centraliza a regra de transição para manter consistência entre chamadas.
        validateStatusTransition(task.getStatus(), command.status());
        task.changeStatus(command.status());
        boardRepository.save(board);
        return task;
    }

    public void removeTask(UUID boardId, UUID taskId) {
        Board board = getBoardOrThrow(boardId);
        boolean removed = board.removeTask(Objects.requireNonNull(taskId, "taskId is required"));
        if (!removed) {
            throw new NotFoundException("Tarefa não encontrada para remoção.");
        }
        boardRepository.save(board);
    }

    private Board getBoardOrThrow(UUID boardId) {
        UUID parsedBoardId = Objects.requireNonNull(boardId, "boardId is required");
        return boardRepository.findById(parsedBoardId)
                .orElseThrow(() -> new NotFoundException("Board não encontrado."));
    }

    private Task getTaskOrThrow(Board board, UUID taskId) {
        UUID parsedTaskId = Objects.requireNonNull(taskId, "taskId is required");
        return board.findTaskById(parsedTaskId)
                .orElseThrow(() -> new NotFoundException("Tarefa não encontrada."));
    }

    private void validateStatusTransition(TaskStatus currentStatus, TaskStatus nextStatus) {
        if (currentStatus == nextStatus) {
            throw new ValidationException("A tarefa já está no status informado.");
        }
    }

    private String safeTrim(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
