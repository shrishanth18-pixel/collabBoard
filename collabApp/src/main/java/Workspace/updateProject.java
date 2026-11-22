package Workspace;

import java.io.IOException;

import db.Update;
import db.getUsers;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/project/update")
public class updateProject extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        try {
            int boardId = Integer.parseInt(req.getParameter("boardId"));
            String name = req.getParameter("projectName");
            String status = req.getParameter("status");
            String priority = req.getParameter("priority");
            int userId = (int) session.getAttribute("user_id");
                if (AuthorizationUtils.isOwner(getUsers.getUserRole(userId, boardId))
                    && name != null && name.trim().length() >= 2
                    && status != null && priority != null
                    && Update.updateBoard(boardId, name.trim(), status, priority)) {
                res.sendRedirect(req.getContextPath() + "/board?id=" + boardId);
            } else {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Only the board owner can update project details");
            }
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid board ID");
        }
    }
}
