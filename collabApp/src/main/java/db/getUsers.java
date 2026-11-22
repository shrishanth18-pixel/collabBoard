package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class getUsers {

    // FIX (ARCH-1): Removed static mutable fields (mail_t, password_hash_t, username_t).
    // They were shared across ALL requests and ALL threads, causing race conditions
    // where concurrent users would overwrite each other's data.
    // Now all data is returned via the HashMap — no shared mutable state.

    /**
     * Looks up a user by email. Returns a HashMap with user_id, mail, username.
     *
     * FIX (BUG-3): The old code executed the query INSIDE the try-with-resources
     * header (ResultSet rs = stmt.executeQuery()), which runs BEFORE
     * stmt.setString(1, email). The parameter was never bound, so the query
     * always failed. Now we set the parameter first, then execute.
     */
    public static HashMap<String, String> selectFromUsers(String email, HttpServletRequest req, HttpServletResponse res) {
        HashMap<String, String> hs = new HashMap<>();
        try (Connection con = DB.conn();
             PreparedStatement stmt = con.prepareStatement(
                 "SELECT user_id, email, password_hash, username FROM users WHERE email = ?")) {

            // Set parameter BEFORE executing
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    hs.put("user_id", String.valueOf(rs.getInt("user_id")));
                    hs.put("mail", rs.getString("email"));
                    hs.put("password_hash", rs.getString("password_hash"));
                    hs.put("username", rs.getString("username"));
                }
            }
            
            return hs;

        } catch (Exception e) {
            e.printStackTrace();
            return hs;
        }
    }

    /**
     * Checks if an email exists in the users table. Returns the username if found.
     */
    public static String checkEmail(String mail, HttpServletResponse res) {
        String name = null;
        try (Connection con = DB.conn();
             PreparedStatement pt = con.prepareStatement("SELECT username FROM users WHERE email = ?")) {
            pt.setString(1, mail);

            try (ResultSet rs = pt.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString("username");
                }
            }
            return name;
        } catch (Exception e) {
            e.printStackTrace();
            return name;
        }
    }

    /**
     * Retrieves the stored password hash for a given email.
     *
     * FIX (SEC-2 / BUG-12): The old code searched for a row WHERE password_hash = ?
     * using the PLAINTEXT password. This would never find a match because BCrypt
     * hashes look nothing like the plaintext. The correct approach is to look up
     * the hash BY EMAIL, then let the caller do BCrypt.checkpw(plaintext, hash).
     *
     * FIX (LEAK-1): ResultSet is now in a try-with-resources block.
     */
    public static String verifyPassword(String email, HttpServletResponse res) {
        try (Connection con = DB.conn();
             PreparedStatement pt = con.prepareStatement(
                 "SELECT password_hash FROM users WHERE email = ?")) {
            pt.setString(1, email);

            try (ResultSet rs = pt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password_hash");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Looks up a user's role on a board and stores it in the session as "role".
     *
     * FIX (BUG-4): The old code called pt.executeQuery() BEFORE setting the
     * parameters with pt.setInt(). Now parameters are set first.
     *
     * FIX (AUTH-4): Consistently uses lowercase "role" as the session attribute key.
     * The old code used "Role" (capital R) here but other files read "role" (lowercase).
     */
    public static void verifyUserPermission(int userID, int boardId, HttpServletRequest req, HttpServletResponse res) {
        try (Connection con = DB.conn();
             PreparedStatement pt = con.prepareStatement(
                 "SELECT role FROM board_member WHERE users_id = ? AND board_id = ?")) {

            // Set parameters BEFORE executing
            pt.setInt(1, userID);
            pt.setInt(2, boardId);

            try (ResultSet rs = pt.executeQuery()) {
                HttpSession session = req.getSession(false);
                if (rs.next() && session != null) {
                    session.setAttribute("role", rs.getString("role"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the role of a user on a specific board as a String, or null if not a member.
     * This is a cleaner alternative to verifyUserPermission() that does not
     * rely on session side effects.
     */
    public static String getUserRole(int userId, int boardId) {
        try (Connection con = DB.conn();
             PreparedStatement pt = con.prepareStatement(
                 "SELECT role FROM board_member WHERE users_id = ? AND board_id = ?")) {
            pt.setInt(1, userId);
            pt.setInt(2, boardId);

            try (ResultSet rs = pt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Looks up a user_id by username. Returns -1 if not found.
     * Used by the tasks servlet to resolve a username to a user_id for the
     * assigned_to foreign key.
     */
    public static int getUserIdByUsername(String username) {
        try (Connection con = DB.conn();
             PreparedStatement pt = con.prepareStatement(
                 "SELECT user_id FROM users WHERE username = ?")) {
            pt.setString(1, username);

            try (ResultSet rs = pt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Returns the count of boards the given user is a member of.
     *
     * FIX (SQL-5): The old query counted ALL boards in the entire database
     * (SELECT COUNT(id) FROM board). Now it counts only the boards the
     * logged-in user belongs to via board_member.
     */
    public static int getNoOfBoards(int userId) {
        int noOfId = 0;
        try (Connection con = DB.conn();
             PreparedStatement pt = con.prepareStatement(
                 "SELECT COUNT(board_id) AS total FROM board_member WHERE users_id = ?")) {
            pt.setInt(1, userId);

            try (ResultSet rs = pt.executeQuery()) {
                if (rs.next()) {
                    noOfId = rs.getInt("total");
                }
            }
            return noOfId;
        } catch (Exception e) {
            e.printStackTrace();
            return noOfId;
        }
    }

    public static java.util.List<java.util.Map<String, Object>> getConnections(int userId) {
        java.util.List<java.util.Map<String, Object>> users = new java.util.ArrayList<>();
        String sql = "SELECT u.user_id,u.username,u.email FROM users u WHERE u.user_id IN " +
                "(SELECT CASE WHEN requester_id=? THEN addressee_id ELSE requester_id END FROM friendships " +
                "WHERE status='ACCEPTED' AND (requester_id=? OR addressee_id=?)) ORDER BY u.username";
        try (Connection con = DB.conn(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setInt(2, userId); ps.setInt(3, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, Object> user = new java.util.HashMap<>();
                    user.put("userId", rs.getInt("user_id"));
                    user.put("username", rs.getString("username"));
                    user.put("email", rs.getString("email"));
                    users.add(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public static boolean areConnected(int firstUserId, int secondUserId) {
        String sql = "SELECT 1 FROM friendships WHERE status='ACCEPTED' AND " +
                "((requester_id=? AND addressee_id=?) OR (requester_id=? AND addressee_id=?))";
        try (Connection con = DB.conn(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, firstUserId); ps.setInt(2, secondUserId);
            ps.setInt(3, secondUserId); ps.setInt(4, firstUserId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
