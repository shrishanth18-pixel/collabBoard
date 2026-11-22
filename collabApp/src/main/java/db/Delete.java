package db;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class Delete {

    public static boolean deleteBoard(int boardId) {
        try (Connection con = DB.conn();
             PreparedStatement pt = con.prepareStatement("DELETE FROM board WHERE id = ?")) {
            pt.setInt(1, boardId);
            return pt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteTask(int taskId, int boardId) {
        try (Connection con = DB.conn();
             PreparedStatement pt = con.prepareStatement("DELETE FROM task WHERE taskid=? AND board_id=?")) {
            pt.setInt(1, taskId);
            pt.setInt(2, boardId);
            return pt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
