package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetBoard {

 
    public static List<Map<String, Object>> getBoardsForUser(int userId) {
        List<Map<String, Object>> boards = new ArrayList<>();
        String sql = "SELECT b.id, b.projectname, b.description, b.status, b.priority, " +
                     "b.created_at, b.deadline, bm.role " +
                     "FROM board b " +
                     "JOIN board_member bm ON b.id = bm.board_id " +
                     "WHERE bm.users_id = ?";
        try (Connection con = DB.conn();
             PreparedStatement pt = con.prepareStatement(sql)) {
            pt.setInt(1, userId);

            try (ResultSet rs = pt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> board = new HashMap<>();
                    board.put("id", rs.getInt("id"));
                    board.put("projectname", rs.getString("projectname"));
                    board.put("description", rs.getString("description"));
                    board.put("status", rs.getString("status"));
                    board.put("priority", rs.getString("priority"));
                    board.put("created_at", rs.getTimestamp("created_at"));
                    board.put("deadline", rs.getTimestamp("deadline"));
                    board.put("role", rs.getString("role"));
                    boards.add(board);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return boards;
    }

    public static Map<String, Object> getBoardById(int boardId) {
        String sql = "SELECT id, projectname, description, status, priority, created_at, deadline FROM board WHERE id = ?";
        try (Connection con = DB.conn();
             PreparedStatement pt = con.prepareStatement(sql)) {
            pt.setInt(1, boardId);

            try (ResultSet rs = pt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> board = new HashMap<>();
                    board.put("id", rs.getInt("id"));
                    board.put("projectname", rs.getString("projectname"));
                    board.put("description", rs.getString("description"));
                    board.put("status", rs.getString("status"));
                    board.put("priority", rs.getString("priority"));
                    board.put("created_at", rs.getTimestamp("created_at"));
                    board.put("deadline", rs.getTimestamp("deadline"));
                    return board;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static java.util.List<java.util.Map<String, Object>> getTasksForBoard(int boardId) {
        java.util.List<java.util.Map<String, Object>> tasks = new java.util.ArrayList<>();
        String sql = "SELECT taskid, taskname, description, taskstatus, deadline, priority, assigned_to FROM task WHERE board_id = ? ORDER BY taskid";
        try (Connection con = DB.conn(); PreparedStatement pt = con.prepareStatement(sql)) {
            pt.setInt(1, boardId);
            try (ResultSet rs = pt.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, Object> task = new java.util.HashMap<>();
                    task.put("taskid", rs.getInt("taskid"));
                    task.put("taskname", rs.getString("taskname"));
                    task.put("description", rs.getString("description"));
                    task.put("taskstatus", rs.getString("taskstatus"));
                    task.put("deadline", rs.getTimestamp("deadline"));
                    task.put("priority", rs.getString("priority"));
                    task.put("assigned_to", rs.getString("assigned_to"));
                    tasks.add(task);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public static java.util.List<java.util.Map<String, Object>> getBoardMembers(int boardId) {
        java.util.List<java.util.Map<String, Object>> members = new java.util.ArrayList<>();
        String sql = "SELECT u.user_id,u.username,u.email,bm.role FROM board_member bm JOIN users u ON u.user_id=bm.users_id WHERE bm.board_id=? ORDER BY u.username";
        try (Connection con = DB.conn(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, boardId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, Object> member = new java.util.HashMap<>();
                    member.put("userId", rs.getInt("user_id"));
                    member.put("username", rs.getString("username"));
                    member.put("email", rs.getString("email"));
                    member.put("role", rs.getString("role"));
                    members.add(member);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return members;
    }

    public static java.util.List<java.util.Map<String, Object>> getBoardInvitations(int boardId) {
        java.util.List<java.util.Map<String, Object>> invitations = new java.util.ArrayList<>();
        String sql = "SELECT bi.invitation_id,u.username,u.email,bi.role FROM board_invitations bi JOIN users u ON u.user_id=bi.invitee_id WHERE bi.board_id=? AND bi.status='PENDING' ORDER BY u.username";
        try (Connection con = DB.conn(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, boardId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, Object> invitation = new java.util.HashMap<>();
                    invitation.put("invitationId", rs.getInt("invitation_id"));
                    invitation.put("username", rs.getString("username"));
                    invitation.put("email", rs.getString("email"));
                    invitation.put("role", rs.getString("role"));
                    invitations.add(invitation);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return invitations;
    }

    public static java.util.Map<String, Object> getTaskById(int boardId, int taskId) {
        String sql = "SELECT taskid,taskname,description,taskstatus,deadline,priority,assigned_to,board_id FROM task WHERE taskid=? AND board_id=?";
        try (Connection con = DB.conn(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, taskId); ps.setInt(2, boardId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.util.Map<String, Object> task = new java.util.HashMap<>();
                    task.put("taskid", rs.getInt("taskid"));
                    task.put("taskname", rs.getString("taskname"));
                    task.put("description", rs.getString("description"));
                    task.put("taskstatus", rs.getString("taskstatus"));
                    task.put("deadline", rs.getTimestamp("deadline"));
                    task.put("priority", rs.getString("priority"));
                    task.put("assigned_to", rs.getString("assigned_to"));
                    task.put("board_id", rs.getInt("board_id"));
                    return task;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
}
