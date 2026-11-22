package Workspace;

import java.io.IOException;

import db.Delete;
import db.getUsers;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/task/delete")
public class DeleteTaskServlet extends HttpServlet {
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
            int userId = (int) session.getAttribute("user_id");
            String role = getUsers.getUserRole(userId, boardId);
            if (!AuthorizationUtils.canEditBoard(role)) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "You cannot delete this task");
                return;
            }
            if (!Delete.deleteTask(taskId, boardId)) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Task not found on this board");
                return;
            }
            res.sendRedirect(req.getContextPath() + "/board?id=" + boardId);
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid task or board ID");
        }
    }
}
