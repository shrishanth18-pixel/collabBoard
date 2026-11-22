package Workspace;

import java.io.IOException;

import db.Delete;
import db.getUsers;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/project/delete")
public class DeleteProjectServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        try {
            int boardId = Integer.parseInt(req.getParameter("boardId"));
            int userId = (int) session.getAttribute("user_id");
            if (!AuthorizationUtils.isOwner(getUsers.getUserRole(userId, boardId))) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Only the board owner can delete this board");
                return;
            }
            Delete.deleteBoard(boardId);
            res.sendRedirect(req.getContextPath() + "/dashboard");
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid board ID");
        }
    }
}
