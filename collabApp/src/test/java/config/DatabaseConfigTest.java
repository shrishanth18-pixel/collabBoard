package config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import db.enums;

class DatabaseConfigTest {
    @Test
    void databaseUrlHasPostgresProtocol() {
        assertTrue(DatabaseConfig.url().startsWith("jdbc:postgresql://"));
    }

    @Test
    void postgresEnumLabelsAreNormalized() {
        assertEquals("On Hold", enums.projectStatusLabel(enums.status.OnHold));
        assertEquals("In Progress", enums.taskStatusLabel(enums.task_status.InProgress));
        assertEquals("Planning", enums.projectStatusLabel(enums.status.Planning));
    }

    @Test
    void sharedDatabaseBoardCrudWorks() {
        Integer userId = db.getUsers.getUserIdByUsername("AJT Tester");
        if (userId == -1) {
            return;
        }
        String name = "JUnit Swing Verification";
        assertTrue(db.DB.createBoardForUser(name, db.enums.status.Planning,
                db.enums.priority.Medium, 1, "JUnit database verification", userId));

        java.util.List<java.util.Map<String, Object>> boards = db.GetBoard.getBoardsForUser(userId);
        java.util.Map<String, Object> created = boards.stream()
                .filter(board -> name.equals(board.get("projectname")))
                .findFirst()
                .orElse(null);
        assertTrue(created != null);

        int boardId = (Integer) created.get("id");
        db.Delete.deleteBoard(boardId);
        assertTrue(db.GetBoard.getBoardById(boardId) == null);
    }
}
