package br.com.dio.gerenciadortarefas.service;

import br.com.dio.gerenciadortarefas.domain.TaskStatus;
import br.com.dio.gerenciadortarefas.repository.InMemoryBoardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoardServiceTest {
    private BoardService service;

    @BeforeEach
    void setUp() {
        service = new BoardService(new InMemoryBoardRepository());
    }

    @Test
    void shouldCreateBoardWithValidName() {
        var board = service.createBoard(new CreateBoardCommand("Estudos DIO"));

        assertNotNull(board.getId());
        assertEquals("Estudos DIO", board.getName());
    }

    @Test
    void shouldAddTaskAndListTasks() {
        var board = service.createBoard(new CreateBoardCommand("Board"));

        service.addTask(board.getId(), new CreateTaskCommand("Implementar API", "Camadas sem Spring", LocalDate.now().plusDays(2)));
        var tasks = service.listTasks(board.getId());

        assertEquals(1, tasks.size());
        assertEquals(TaskStatus.TODO, tasks.get(0).getStatus());
    }

    @Test
    void shouldMoveTaskStatus() {
        var board = service.createBoard(new CreateBoardCommand("Board"));
        var task = service.addTask(board.getId(), new CreateTaskCommand("Criar endpoint", "POST /boards", null));

        var updated = service.moveTask(board.getId(), task.getId(), new MoveTaskCommand(TaskStatus.IN_PROGRESS));

        assertEquals(TaskStatus.IN_PROGRESS, updated.getStatus());
    }

    @Test
    void shouldFailWhenMovingToSameStatus() {
        var board = service.createBoard(new CreateBoardCommand("Board"));
        var task = service.addTask(board.getId(), new CreateTaskCommand("Criar endpoint", null, null));

        var ex = assertThrows(ValidationException.class,
                () -> service.moveTask(board.getId(), task.getId(), new MoveTaskCommand(TaskStatus.TODO)));

        assertFalse(ex.getMessage().isBlank());
    }

    @Test
    void shouldRemoveTask() {
        var board = service.createBoard(new CreateBoardCommand("Board"));
        var task = service.addTask(board.getId(), new CreateTaskCommand("Criar endpoint", null, null));

        service.removeTask(board.getId(), task.getId());

        assertEquals(0, service.listTasks(board.getId()).size());
    }

    @Test
    void shouldFailWhenBoardDoesNotExist() {
        assertThrows(NotFoundException.class,
                () -> service.listTasks(java.util.UUID.randomUUID()));
    }
}
