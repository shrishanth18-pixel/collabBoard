package db;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class Update {

    public static boolean updateBoard(int boardId, String projectName, String status, String priority) {
        try (Connection con = DB.conn();
             PreparedStatement pt = con.prepareStatement(
                 "UPDATE board SET projectname = ?, status = ?::project_status, priority = ?::priority_order WHERE id = ?")) {
            pt.setString(1, projectName);
            pt.setString(2, "OnHold".equals(status) ? "On Hold" : status);
            pt.setString(3, priority);
            pt.setInt(4, boardId);
            return pt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean updateTask(int boardId, int taskId, String taskName, String description,
                                     String taskStatus, int deadlineDays, String priority, String assignedTo) {
        try (Connection con = DB.conn(); PreparedStatement pt = con.prepareStatement(
                "UPDATE task SET taskname=?, description=?, taskstatus=?::task_status, " +
                "deadline=CURRENT_TIMESTAMP + MAKE_INTERVAL(days => ?), priority=?::priority_order, assigned_to=? " +
                "WHERE taskid=? AND board_id=?")) {
            pt.setString(1, taskName);
            pt.setString(2, description);
            pt.setString(3, "InProgress".equals(taskStatus) ? "In Progress" : taskStatus);
            pt.setInt(4, deadlineDays);
            pt.setString(5, priority);
            pt.setString(6, assignedTo);
            pt.setInt(7, taskId);
            pt.setInt(8, boardId);
            return pt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
