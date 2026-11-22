package repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import model.BoardEntity;

public class BoardHibernateRepository {
    private final SessionFactory sessionFactory;

    public BoardHibernateRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<BoardEntity> findBoardsForUser(int userId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createNativeQuery(
                    "SELECT b.*, bm.role FROM board b JOIN board_member bm ON b.id = bm.board_id WHERE bm.users_id = :userId",
                    BoardEntity.class)
                    .setParameter("userId", userId)
                    .getResultList();
        }
    }
}
