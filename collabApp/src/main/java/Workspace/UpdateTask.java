package Workspace;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import db.Update;
import db.getUsers;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/task/update")
public class UpdateTask extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(UpdateTask.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            int taskId = Integer.parseInt(req.getParameter("taskId"));
            int boardId = Integer.parseInt(req.getParameter("boardId"));
            String taskName = req.getParameter("taskName");
            String taskStatus = req.getParameter("taskStatus");
            String priority = req.getParameter("priority");
            String description = req.getParameter("description");
            String deadlineParam = req.getParameter("deadlineDays");
            String assignedTo = req.getParameter("assignedTo");

            if (taskName == null || taskName.trim().isEmpty() || description == null || description.trim().length() < 3
                    || taskStatus == null || priority == null || deadlineParam == null || assignedTo == null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Task title, description, status, priority, deadline, and assignee are required");
                return;
            }

            int deadlineDays = Integer.parseInt(deadlineParam);
            int userId = (int) session.getAttribute("user_id");
            String role = getUsers.getUserRole(userId, boardId);
            if (!AuthorizationUtils.canEditBoard(role)) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Only board owners and editors can update tasks");
                return;
            }

            int assignedId = getUsers.getUserIdByUsername(assignedTo.trim());
            if (deadlineDays < 1 || deadlineDays > 365) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Deadline must be from 1 to 365 days");
                return;
            }
            if (assignedId == -1) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Assignee not found");
                return;
            }
            if (assignedId != userId && !getUsers.areConnected(userId, assignedId)) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "You can only assign accepted connections");
                return;
            }

            java.util.Map<String, Object> existingTask = db.GetBoard.getTaskById(boardId, taskId);
            if (existingTask == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Task not found on this board");
                return;
            }

            String sanitizedTaskName = taskName.trim();
            String sanitizedTaskStatus = taskStatus.replace(" ", "").replace("-", "");
            boolean updated = Update.updateTask(boardId, taskId, sanitizedTaskName, description.trim(),
                    sanitizedTaskStatus, deadlineDays, priority, assignedTo.trim());
            if (updated) {
                res.sendRedirect(req.getContextPath() + "/board?id=" + boardId);
            } else {
                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update task");
            }
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid task or board ID");
        } catch (IllegalArgumentException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid task status or priority");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Task update failed", e);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        }
    }
}