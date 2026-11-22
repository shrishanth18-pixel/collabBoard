package Workspace;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import db.DB;
import db.enums;
import db.getUsers;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * FIX (BUG-2): The old class did NOT extend HttpServlet.
 * Without extends HttpServlet, the @WebServlet annotation is ignored and Tomcat
 * never routes requests to doPost(). The commented-out @Override was a clue.
 */
@WebServlet("/project/task")
public class tasks extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(tasks.class.getName());

    private static String[] firstNonNullArray(String[]... candidates) {
        for (String[] candidate : candidates) {
            if (candidate != null) {
                return candidate;
            }
        }
        return null;
    }

    /**
     * Creates a new task and assigns it to a user.
     *
     * FIX (SEC-3): Added session validation. The old code had no auth check.
     *
     * FIX (SEC-5): Added null checks for all request parameters.
     *
     * FIX (SQL-2): Now includes board_id when inserting a task (was missing,
     * so tasks were orphaned with no board association).
     *
     * The assigned_to column is a FK to users.user_id (int), but the frontend
     * sends a username (String). We resolve it to a user_id using
     * getUsers.getUserIdByUsername().
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // Check authentication
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            String boardIdParam = req.getParameter("board_id");
            if (boardIdParam == null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Board ID is required");
                return;
            }

            int boardId = Integer.parseInt(boardIdParam);
            int currentUserId = (int) session.getAttribute("user_id");
            String role = getUsers.getUserRole(currentUserId, boardId);
            if (!AuthorizationUtils.canEditBoard(role)) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Viewers cannot create tasks");
                return;
            }

            String[] names = firstNonNullArray(req.getParameterValues("taskName"), req.getParameterValues("Task Name"));
            String[] statuses = firstNonNullArray(req.getParameterValues("taskStatus"), req.getParameterValues("Task status"));
            String[] deadlines = firstNonNullArray(req.getParameterValues("deadlineDays"), req.getParameterValues("deadline"));
            String[] priorities = firstNonNullArray(req.getParameterValues("taskPriority"), req.getParameterValues("Task priority"));
            String[] usernames = firstNonNullArray(req.getParameterValues("assignee"), req.getParameterValues("Name"));
            String[] descriptions = req.getParameterValues("description");

            if (names == null || statuses == null || deadlines == null || priorities == null || usernames == null
                    || descriptions == null || names.length == 0 || names.length != statuses.length
                    || names.length != deadlines.length || names.length != priorities.length
                    || names.length != usernames.length || names.length != descriptions.length) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Each task must contain all required fields");
                return;
            }

            int createdCount = 0;
            for (int i = 0; i < names.length; i++) {
                String taskName = names[i] == null ? "" : names[i].trim();
                String username = usernames[i] == null ? "" : usernames[i].trim();
                String description = descriptions[i] == null ? "" : descriptions[i].trim();
                if (taskName.length() < 2) {
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Task title must contain at least 2 characters");
                    return;
                }
                if (description.length() < 3) {
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Task description must contain at least 3 characters");
                    return;
                }
                if (username.isEmpty()) {
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST, "A task assignee is required");
                    return;
                }

                int deadlineDays = Integer.parseInt(deadlines[i]);
                if (deadlineDays < 1 || deadlineDays > 365) {
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Task deadlines must be from 1 to 365 days");
                    return;
                }

                int assignedToId = getUsers.getUserIdByUsername(username);
                if (assignedToId == -1) {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND, "User '" + username + "' not found");
                    return;
                }
                if (assignedToId != currentUserId && !getUsers.areConnected(currentUserId, assignedToId)) {
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST, "You can only tag accepted connections");
                    return;
                }

                enums.task_status taskStatus = enums.task_status.valueOf(statuses[i].replace(" ", "").replace("-", ""));
                enums.priority taskPriority = enums.priority.valueOf(priorities[i]);
                if (DB.insertToTask(taskName, description, taskStatus, deadlineDays, taskPriority, boardId, username)) {
                    createdCount++;
                }
            }

            if (createdCount == names.length) {
                res.sendRedirect(req.getContextPath() + "/board?id=" + boardId);
            } else {
                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Only " + createdCount + " of " + names.length + " tasks were created");
            }

        } catch (IllegalArgumentException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid status, priority, or deadline value");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Task creation failed", e);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred");
        }
    }
}
