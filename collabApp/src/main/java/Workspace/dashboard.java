package Workspace;

import java.io.IOException;

import db.getUsers;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * FIX (ARCH-4): Changed @WebServlet("dashboard") to @WebServlet("/dashboard").
 * Without the leading /, the URL mapping is relative to the context path and
 * may not resolve correctly.
 */
@WebServlet("/dashboard")
public class dashboard extends HttpServlet {

    /**
     * FIX (SEC-3): Added session validation. The old code allowed unauthenticated
     * access to the dashboard.
     *
     * FIX (SQL-5): getNoOfBoards now takes userId and counts only the boards the
     * logged-in user belongs to (was counting ALL boards globally).
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // Check authentication
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            int userId = (int) session.getAttribute("user_id");
            int total = getUsers.getNoOfBoards(userId);
            req.setAttribute("noOfBoards", total);
            try {
                org.springframework.context.annotation.AnnotationConfigApplicationContext springContext =
                        (org.springframework.context.annotation.AnnotationConfigApplicationContext)
                                req.getServletContext().getAttribute("springContext");
                service.BoardService boardService = springContext.getBean(service.BoardService.class);
                java.util.List<model.BoardEntity> hibernateBoards = boardService.getBoardsForUser(userId);
                java.util.List<java.util.Map<String, Object>> boards = new java.util.ArrayList<>();
                for (model.BoardEntity board : hibernateBoards) {
                    java.util.Map<String, Object> row = new java.util.HashMap<>();
                    row.put("id", board.getId());
                    row.put("projectname", board.getProjectName());
                    row.put("description", board.getDescription());
                    row.put("status", board.getStatus());
                    row.put("priority", board.getPriority());
                    row.put("role", db.getUsers.getUserRole(userId, board.getId()));
                    boards.add(row);
                }
                req.setAttribute("boards", boards);
            } catch (Exception hibernateError) {
                req.setAttribute("boards", db.GetBoard.getBoardsForUser(userId));
            }
            RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/jspFiles/dashboard.jsp");
            rd.forward(req, res);
        } catch (Exception e) {
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred");
        }
    }
}
