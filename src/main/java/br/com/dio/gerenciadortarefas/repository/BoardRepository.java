package br.com.dio.gerenciadortarefas.repository;

import br.com.dio.gerenciadortarefas.domain.Board;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BoardRepository {
    Board save(Board board);

    Optional<Board> findById(UUID boardId);

    List<Board> findAll();
}
