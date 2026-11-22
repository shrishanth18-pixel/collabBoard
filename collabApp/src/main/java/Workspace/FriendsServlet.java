package Workspace;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import db.DB;
import db.getUsers;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/friends")
public class FriendsServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int requesterId = (int) session.getAttribute("user_id");
        String action = req.getParameter("action");
        String username = req.getParameter("username");

        try (Connection con = DB.conn()) {
            if ("send".equals(action) && username != null) {
                int addresseeId = getUsers.getUserIdByUsername(username.trim());
                if (addresseeId == -1 || addresseeId == requesterId) {
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Enter a valid different username");
                    return;
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO friendships(requester_id, addressee_id, status) VALUES(?, ?, 'PENDING') " +
                        "ON CONFLICT(requester_id, addressee_id) DO UPDATE SET status='PENDING'")) {
                    ps.setInt(1, requesterId);
                    ps.setInt(2, addresseeId);
                    ps.executeUpdate();
                }
                res.sendRedirect(req.getContextPath() + "/friends");
            } else if ("accept".equals(action)) {
                int friendshipId = Integer.parseInt(req.getParameter("friendshipId"));
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE friendships SET status='ACCEPTED' WHERE friendship_id=? AND addressee_id=? AND status='PENDING'")) {
                    ps.setInt(1, friendshipId);
                    ps.setInt(2, requesterId);
                    ps.executeUpdate();
                }
                res.sendRedirect(req.getContextPath() + "/friends");
            } else {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid friend action");
            }
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid friendship ID");
        } catch (Exception e) {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Friend action failed");
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
        java.util.List<java.util.Map<String, Object>> pending = new java.util.ArrayList<>();
        java.util.List<java.util.Map<String, Object>> connections = new java.util.ArrayList<>();
        try (Connection con = DB.conn()) {
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT f.friendship_id,u.username,u.email FROM friendships f JOIN users u ON u.user_id=f.requester_id " +
                    "WHERE f.addressee_id=? AND f.status='PENDING'")) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) pending.add(java.util.Map.of("friendshipId", rs.getInt("friendship_id"), "username", rs.getString("username"), "email", rs.getString("email")));
                }
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT u.user_id,u.username,u.email FROM users u WHERE u.user_id IN " +
                    "(SELECT CASE WHEN requester_id=? THEN addressee_id ELSE requester_id END FROM friendships WHERE status='ACCEPTED' AND (requester_id=? OR addressee_id=?))")) {
                ps.setInt(1, userId); ps.setInt(2, userId); ps.setInt(3, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) connections.add(java.util.Map.of("userId", rs.getInt("user_id"), "username", rs.getString("username"), "email", rs.getString("email")));
                }
            }
            req.setAttribute("pendingFriends", pending);
            req.setAttribute("friends", connections);
            req.getRequestDispatcher("/WEB-INF/jspFiles/friends.jsp").forward(req, res);
        } catch (Exception e) {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not load friends");
        }
    }
}
