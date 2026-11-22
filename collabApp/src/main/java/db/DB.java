package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import config.DatabaseConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class DB {


    public static Connection conn() throws SQLException {
        try {
            String dbHost = DatabaseConfig.url();
            String dbUser = DatabaseConfig.username();
            String dbPass = DatabaseConfig.password();
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(dbHost, dbUser, dbPass);
            return conn;
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(DB.class.getName()).log(java.util.logging.Level.SEVERE, "Database connection failed", e);
            throw new SQLException("Failed to connect", e);
        }
    }

    public static boolean insertUser(String username, String email, String password, HttpServletResponse res) {
        try {
            String insertToUser = "INSERT INTO users(username, email, password_hash) VALUES(?, ?, ?)";
            try (Connection con = conn();
                 PreparedStatement ps = con.prepareStatement(insertToUser)) {
                ps.setString(1, username);
                ps.setString(2, email);
                ps.setString(3, password);

                int bl = ps.executeUpdate();
                return bl > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean insertToBoard(String projectName, enums.status projectStatus,
                                        enums.priority projectPriority, int months,
                                        String description, HttpServletResponse res,
                                        HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) return false;
        int userId = (int) session.getAttribute("user_id");
        return createBoardForUser(projectName, projectStatus, projectPriority, months, description, userId);
    }

    public static boolean insertToTask(String taskName, String description, enums.task_status taskStatus,
                                       int deadlineDays, enums.priority taskPriority,
                                       int boardId, String assignedTo) {
        try {
            String insertTask = "INSERT INTO task(taskname, description, taskstatus, deadline, priority, board_id, assigned_to) " +
                                "VALUES(?, ?, ?::task_status, CURRENT_TIMESTAMP + MAKE_INTERVAL(days => ?), ?::priority_order, ?, ?)";
            try (Connection con = conn(); PreparedStatement ps = con.prepareStatement(insertTask)) {
                ps.setString(1, taskName);
                ps.setString(2, description);
                ps.setString(3, enums.taskStatusLabel(taskStatus));
                ps.setInt(4, deadlineDays);
                ps.setString(5, taskPriority.name());
                ps.setInt(6, boardId);
                ps.setString(7, assignedTo);
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean insertToTask(String taskName, enums.task_status taskStatus,
                                       int deadlineDays, enums.priority taskPriority,
                                       int boardId, String assignedTo) {
        return insertToTask(taskName, "No description provided", taskStatus, deadlineDays, taskPriority, boardId, assignedTo);
    }

   
    public static void board_member(int boardID, int userId, HttpServletRequest req, HttpServletResponse res) {
        try (Connection con = conn();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT role FROM board_member WHERE board_id = ? AND users_id = ?")) {
            ps.setInt(1, boardID);
            ps.setInt(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    HttpSession session = req.getSession();
                    session.setAttribute("role", rs.getString("role"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean createBoardForUser(String projectName, enums.status projectStatus,
                                             enums.priority projectPriority, int months,
                                             String description, int userId) {
        try (Connection con = conn()) {
            con.setAutoCommit(false);
            String insertBoard = "INSERT INTO board(projectName, status, priority, deadline, description, created_by) " +
                                 "VALUES(?, ?::project_status, ?::priority_order, CURRENT_TIMESTAMP + MAKE_INTERVAL(months => ?), ?, ?)";
            int boardId;
            try (PreparedStatement ps = con.prepareStatement(insertBoard, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, projectName);
                ps.setString(2, enums.projectStatusLabel(projectStatus));
                ps.setString(3, projectPriority.name());
                ps.setInt(4, months);
                ps.setString(5, description);
                ps.setInt(6, userId);
                if (ps.executeUpdate() == 0) { con.rollback(); return false; }
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) { con.rollback(); return false; }
                    boardId = keys.getInt(1);
                }
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO board_member(users_id, board_id, role) VALUES(?, ?, ?::user_role)")) {
                ps.setInt(1, userId); ps.setInt(2, boardId); ps.setString(3, enums.Role.OWNER.name());
                ps.executeUpdate();
            }
            con.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
