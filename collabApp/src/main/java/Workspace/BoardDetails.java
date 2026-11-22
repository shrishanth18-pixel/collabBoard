package Workspace;

import java.io.IOException;

import db.GetBoard;
import db.getUsers;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/board")
public class BoardDetails extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        try {
            int boardId = Integer.parseInt(req.getParameter("id"));
            int userId = (int) session.getAttribute("user_id");
            String role = getUsers.getUserRole(userId, boardId);
            if (role == null) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not a member of this board");
                return;
            }
            req.setAttribute("board", GetBoard.getBoardById(boardId));
            req.setAttribute("tasks", GetBoard.getTasksForBoard(boardId));
            req.setAttribute("boardRole", role);
            java.util.List<java.util.Map<String, Object>> connections = getUsers.getConnections(userId);
            java.util.Map<String, Object> self = new java.util.HashMap<>();
            self.put("username", session.getAttribute("username"));
            connections.add(0, self);
            req.setAttribute("connections", connections);
            req.setAttribute("members", db.GetBoard.getBoardMembers(boardId));
            req.setAttribute("pendingInvitations", db.GetBoard.getBoardInvitations(boardId));
            req.getRequestDispatcher("/WEB-INF/jspFiles/boardDetails.jsp").forward(req, res);
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid board ID");
        }
    }
}
