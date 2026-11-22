package Workspace;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import db.DB;
import db.Delete;
import db.enums;
import db.getUsers;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/project")
public class project extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(project.class.getName());

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value;
            }
        }
        return null;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // Check authentication
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            String projectName = firstNonBlank(req.getParameter("projectName"), req.getParameter("project name"));
            String description = req.getParameter("description");
            String statusParam = firstNonBlank(req.getParameter("projectStatus"), req.getParameter("project status"));
            String priorityParam = firstNonBlank(req.getParameter("projectPriority"), req.getParameter("project priority"));
            String monthsParam = req.getParameter("months");

            if (projectName == null || projectName.trim().length() < 2 || description == null || description.trim().length() < 3) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Project name and description are too short");
                return;
            }

            if (statusParam == null || priorityParam == null || monthsParam == null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "All fields are required");
                return;
            }

            enums.status projectStatus = enums.status.valueOf(statusParam);
            enums.priority projectPriority = enums.priority.valueOf(priorityParam);
            int deadlineMonth = Integer.parseInt(monthsParam);

            if (deadlineMonth < 1 || deadlineMonth > 36) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Deadline must be between 1 and 36 months");
                return;
            }

            boolean created = DB.insertToBoard(projectName, projectStatus, projectPriority,
                                               deadlineMonth, description, res, req);
            if (created) {
                res.sendRedirect(req.getContextPath() + "/dashboard");
            } else {
                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create project");
            }

        } catch (IllegalArgumentException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid status, priority, or deadline value");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Project creation failed", e);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred");
        }
    }


    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not logged in");
            return;
        }

        try {
            String boardIdParam = req.getParameter("board_id");
            if (boardIdParam == null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Board ID is required");
                return;
            }

            int boardId = Integer.parseInt(boardIdParam);
            int userId = (int) session.getAttribute("user_id");

            String role = getUsers.getUserRole(userId, boardId);
            if (AuthorizationUtils.isOwner(role)) {
                Delete.deleteBoard(boardId);
                res.setContentType("text/plain;charset=UTF-8");
                res.getWriter().println("Project deleted!");
            } else {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Only the board OWNER can delete a project");
            }

        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid board ID");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Project deletion failed", e);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws jakarta.servlet.ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jspFiles/createBoard.jsp").forward(req, res);
    }
}
