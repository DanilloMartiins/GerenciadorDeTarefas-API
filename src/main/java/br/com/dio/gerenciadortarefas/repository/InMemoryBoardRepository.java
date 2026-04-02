package br.com.dio.gerenciadortarefas.repository;

import br.com.dio.gerenciadortarefas.domain.Board;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryBoardRepository implements BoardRepository {
    private final ConcurrentMap<UUID, Board> boards = new ConcurrentHashMap<>();

    @Override
    public Board save(Board board) {
        boards.put(board.getId(), board);
        return board;
    }

    @Override
    public Optional<Board> findById(UUID boardId) {
        return Optional.ofNullable(boards.get(boardId));
    }

    @Override
    public List<Board> findAll() {
        return new ArrayList<>(boards.values());
    }
}
