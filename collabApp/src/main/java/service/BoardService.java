package service;

import java.util.List;

import model.BoardEntity;
import repository.BoardHibernateRepository;

public class BoardService {
    private final BoardHibernateRepository boardRepository;

    public BoardService(BoardHibernateRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public List<BoardEntity> getBoardsForUser(int userId) {
        return boardRepository.findBoardsForUser(userId);
    }
}
