package br.com.dio.gerenciadortarefas.web;

import br.com.dio.gerenciadortarefas.service.BoardService;
import br.com.dio.gerenciadortarefas.service.CreateBoardCommand;
import br.com.dio.gerenciadortarefas.service.CreateTaskCommand;
import br.com.dio.gerenciadortarefas.service.MoveTaskCommand;
import br.com.dio.gerenciadortarefas.service.NotFoundException;
import br.com.dio.gerenciadortarefas.service.ValidationException;
import br.com.dio.gerenciadortarefas.web.dto.CreateBoardRequest;
import br.com.dio.gerenciadortarefas.web.dto.CreateTaskRequest;
import br.com.dio.gerenciadortarefas.web.dto.ErrorResponse;
import br.com.dio.gerenciadortarefas.web.dto.MoveTaskRequest;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ApiHandler implements HttpHandler {
    private final BoardService boardService;

    public ApiHandler(BoardService boardService) {
        this.boardService = Objects.requireNonNull(boardService, "boardService is required");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            List<String> segments = Arrays.stream(path.split("/"))
                    .filter(segment -> !segment.isBlank())
                    .toList();

            if (isHealthRoute(method, segments)) {
                writeJson(exchange, HttpURLConnection.HTTP_OK, "API online");
                return;
            }
            if ("POST".equals(method) && segments.equals(List.of("boards"))) {
                createBoard(exchange);
                return;
            }
            if ("GET".equals(method) && segments.equals(List.of("boards"))) {
                listBoards(exchange);
                return;
            }
            if ("GET".equals(method) && segments.size() == 2 && "boards".equals(segments.get(0))) {
                getBoardById(exchange, segments.get(1));
                return;
            }
            if ("POST".equals(method) && segments.size() == 3 && "boards".equals(segments.get(0)) && "tasks".equals(segments.get(2))) {
                createTask(exchange, segments.get(1));
                return;
            }
            if ("GET".equals(method) && segments.size() == 3 && "boards".equals(segments.get(0)) && "tasks".equals(segments.get(2))) {
                listTasks(exchange, segments.get(1));
                return;
            }
            if ("PATCH".equals(method) && segments.size() == 5
                    && "boards".equals(segments.get(0))
                    && "tasks".equals(segments.get(2))
                    && "status".equals(segments.get(4))) {
                moveTask(exchange, segments.get(1), segments.get(3));
                return;
            }
            if ("DELETE".equals(method) && segments.size() == 4
                    && "boards".equals(segments.get(0))
                    && "tasks".equals(segments.get(2))) {
                deleteTask(exchange, segments.get(1), segments.get(3));
                return;
            }

            writeError(exchange, HttpURLConnection.HTTP_NOT_FOUND, "Rota não encontrada.");
        } catch (ValidationException e) {
            writeError(exchange, HttpURLConnection.HTTP_BAD_REQUEST, e.getMessage());
        } catch (NotFoundException e) {
            writeError(exchange, HttpURLConnection.HTTP_NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            writeError(exchange, HttpURLConnection.HTTP_BAD_REQUEST, "Formato de UUID inválido.");
        } catch (Exception e) {
            writeError(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "Erro interno da API.");
        }
    }

    private boolean isHealthRoute(String method, List<String> segments) {
        return "GET".equals(method) && segments.equals(List.of("health"));
    }

    private void createBoard(HttpExchange exchange) throws IOException {
        CreateBoardRequest request = JsonUtil.fromBody(exchange.getRequestBody(), CreateBoardRequest.class);
        var board = boardService.createBoard(new CreateBoardCommand(request == null ? null : request.name()));
        writeJson(exchange, HttpURLConnection.HTTP_CREATED, ApiMapper.toBoardResponse(board));
    }

    private void listBoards(HttpExchange exchange) throws IOException {
        var boards = boardService.listBoards();
        writeJson(exchange, HttpURLConnection.HTTP_OK, ApiMapper.toBoardResponseList(boards));
    }

    private void getBoardById(HttpExchange exchange, String boardId) throws IOException {
        var board = boardService.findBoard(parseUuid(boardId));
        writeJson(exchange, HttpURLConnection.HTTP_OK, ApiMapper.toBoardResponse(board));
    }

    private void createTask(HttpExchange exchange, String boardId) throws IOException {
        CreateTaskRequest request = JsonUtil.fromBody(exchange.getRequestBody(), CreateTaskRequest.class);
        var task = boardService.addTask(
                parseUuid(boardId),
                new CreateTaskCommand(
                        request == null ? null : request.title(),
                        request == null ? null : request.description(),
                        request == null ? null : request.dueDate()
                )
        );
        writeJson(exchange, HttpURLConnection.HTTP_CREATED, ApiMapper.toTaskResponse(task));
    }

    private void listTasks(HttpExchange exchange, String boardId) throws IOException {
        var tasks = boardService.listTasks(parseUuid(boardId));
        writeJson(exchange, HttpURLConnection.HTTP_OK, ApiMapper.toTaskResponseList(tasks));
    }

    private void moveTask(HttpExchange exchange, String boardId, String taskId) throws IOException {
        MoveTaskRequest request = JsonUtil.fromBody(exchange.getRequestBody(), MoveTaskRequest.class);
        var task = boardService.moveTask(
                parseUuid(boardId),
                parseUuid(taskId),
                new MoveTaskCommand(request == null ? null : request.status())
        );
        writeJson(exchange, HttpURLConnection.HTTP_OK, ApiMapper.toTaskResponse(task));
    }

    private void deleteTask(HttpExchange exchange, String boardId, String taskId) throws IOException {
        boardService.removeTask(parseUuid(boardId), parseUuid(taskId));
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, -1);
    }

    private UUID parseUuid(String rawId) {
        return UUID.fromString(rawId);
    }

    private void writeJson(HttpExchange exchange, int statusCode, Object payload) throws IOException {
        byte[] responseBytes = JsonUtil.toJsonBytes(payload);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        exchange.getResponseBody().write(responseBytes);
    }

    private void writeError(HttpExchange exchange, int statusCode, String message) throws IOException {
        ErrorResponse payload = new ErrorResponse(message, LocalDateTime.now());
        byte[] responseBytes = JsonUtil.toJsonBytes(payload);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        exchange.getResponseBody().write(responseBytes);
    }
}
