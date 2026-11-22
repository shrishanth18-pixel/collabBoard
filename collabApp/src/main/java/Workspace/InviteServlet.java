package Workspace;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import db.DB;
import db.getUsers;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/invite")
public class InviteServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        int inviterId = (int) session.getAttribute("user_id");
        String action = req.getParameter("action");
        try (Connection con = DB.conn()) {
            if ("send".equals(action)) {
                int boardId = Integer.parseInt(req.getParameter("boardId"));
                String role = req.getParameter("role");
                String username = req.getParameter("username");
                if (!AuthorizationUtils.isOwner(getUsers.getUserRole(inviterId, boardId))) {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN, "Only the board owner can send invitations");
                    return;
                }
                int inviteeId = getUsers.getUserIdByUsername(username == null ? "" : username.trim().replace("@", ""));
                if (inviteeId == -1 || inviteeId == inviterId) {
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Enter a valid username");
                    return;
                }
                if (!"EDITOR".equals(role) && !"VIEWER".equals(role)) role = "EDITOR";
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO board_invitations(board_id, inviter_id, invitee_id, role, status) VALUES(?, ?, ?, ?::user_role, 'PENDING') " +
                        "ON CONFLICT(board_id, invitee_id) DO UPDATE SET role=EXCLUDED.role,status='PENDING'")) {
                    ps.setInt(1, boardId); ps.setInt(2, inviterId); ps.setInt(3, inviteeId); ps.setString(4, role);
                    ps.executeUpdate();
                }
                res.sendRedirect(req.getContextPath() + "/board?id=" + boardId);
            } else if ("accept".equals(action)) {
                int invitationId = Integer.parseInt(req.getParameter("invitationId"));
                con.setAutoCommit(false);
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT board_id,invitee_id,role FROM board_invitations WHERE invitation_id=? AND invitee_id=? AND status='PENDING'")) {
                    ps.setInt(1, invitationId); ps.setInt(2, inviterId);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) { con.rollback(); res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invitation not found"); return; }
                        int boardId = rs.getInt("board_id");
                        String role = rs.getString("role");
                        try (PreparedStatement member = con.prepareStatement(
                                "INSERT INTO board_member(users_id,board_id,role) VALUES(?,?,?::user_role) ON CONFLICT DO NOTHING")) {
                            member.setInt(1, inviterId); member.setInt(2, boardId); member.setString(3, role); member.executeUpdate();
                        }
                        try (PreparedStatement done = con.prepareStatement("UPDATE board_invitations SET status='ACCEPTED' WHERE invitation_id=?")) {
                            done.setInt(1, invitationId); done.executeUpdate();
                        }
                        con.commit();
                        res.sendRedirect(req.getContextPath() + "/board?id=" + boardId);
                    }
                }
            } else {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid invitation action");
            }
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid board or invitation ID");
        } catch (Exception e) {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invitation action failed");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, jakarta.servlet.ServletException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        int userId = (int) session.getAttribute("user_id");
        java.util.List<java.util.Map<String, Object>> invitations = new java.util.ArrayList<>();
        try (Connection con = DB.conn(); PreparedStatement ps = con.prepareStatement(
                "SELECT bi.invitation_id,b.projectname,u.username,bi.role FROM board_invitations bi " +
                "JOIN board b ON b.id=bi.board_id JOIN users u ON u.user_id=bi.inviter_id " +
                "WHERE bi.invitee_id=? AND bi.status='PENDING' ORDER BY bi.created_at DESC")) {
            ps.setInt(1, userId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) invitations.add(java.util.Map.of(
                        "invitationId", rs.getInt("invitation_id"),
                        "boardName", rs.getString("projectname"),
                        "inviter", rs.getString("username"),
                        "role", rs.getString("role")));
            }
            req.setAttribute("invitations", invitations);
            req.getRequestDispatcher("/WEB-INF/jspFiles/invitations.jsp").forward(req, res);
        } catch (Exception e) {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not load invitations");
        }
    }
}
