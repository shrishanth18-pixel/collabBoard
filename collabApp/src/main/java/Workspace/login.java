package Workspace;

import java.io.IOException;
import java.util.HashMap;

import org.mindrot.jbcrypt.BCrypt;

import db.getUsers;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * FIX (BUG-1): The old class did NOT extend HttpServlet.
 * Without extends HttpServlet, Tomcat ignores the @WebServlet annotation entirely
 * and the doGet/doPost methods are never invoked by the container.
 */
@WebServlet("/login")
public class login extends HttpServlet {

    /**
     * FIX (BUG-11): The old code called req.getRequestDispatcher(...) but never
     * called .forward(req, res). getRequestDispatcher() only creates the object —
     * without .forward(), the JSP is never rendered and the response is empty.
     *
     * Also fixed the JSP path: it should be relative to the webapp root
     * (/WEB-INF/...), not /webapp/WEB-INF/...
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        SecurityUtils.ensureCsrfToken(req.getSession());
        req.getRequestDispatcher("/WEB-INF/jspFiles/login.jsp").forward(req, res);
    }

    /**
     * FIX (BUG-12): The entire login flow was broken:
     *
     * 1. verifyPassword() searched WHERE password_hash = ? using the PLAINTEXT
     *    password. BCrypt hashes never equal the plaintext, so it always returned "".
     * 2. BCrypt.checkpw(password, "") throws IllegalArgumentException on empty hash.
     * 3. Lines after the if-block (checkEmail, sendRedirect, out.println) ran
     *    UNCONDITIONALLY — even when the password didn't match.
     * 4. sendRedirect + out.println causes IllegalStateException.
     *
     * Fixed flow:
     * 1. Validate inputs are not null/empty
     * 2. Look up stored hash BY EMAIL (not by plaintext matching)
     * 3. BCrypt.checkpw(plaintext, storedHash) to verify
     * 4. On success: set session attributes and redirect
     * 5. On failure: send 401 error
     *
     * FIX (AUTH-2/AUTH-3): Session now consistently stores "user_id" (Integer),
     * "email" (String), and "username" (String).
     *
     * FIX (AUTH-1): Reduced session timeout from 5 days (432000s) to 30 min (1800s).
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String email = req.getParameter("email") == null ? "" : req.getParameter("email").trim().toLowerCase();
        String password = req.getParameter("password");

        if (email.isEmpty() || password == null || password.isEmpty()) {
            req.setAttribute("error", "Email and password are required.");
            req.getRequestDispatcher("/WEB-INF/jspFiles/login.jsp").forward(req, res);
            return;
        }

        // Look up the stored BCrypt hash for this email
        String storedHash = getUsers.verifyPassword(email, res);

        if (storedHash != null && BCrypt.checkpw(password, storedHash)) {
            // Password matches — create authenticated session
            HashMap<String, String> userData = getUsers.selectFromUsers(email, req, res);
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession session = req.getSession(true);
            session.setMaxInactiveInterval(1800); // 30 minutes

            String userIdStr = userData.get("user_id");
            if (userIdStr != null) {
                session.setAttribute("user_id", Integer.valueOf(userIdStr));
            }
            session.setAttribute("email", email);
            session.setAttribute("username", userData.get("username"));
                SecurityUtils.ensureCsrfToken(session);

            res.sendRedirect(req.getContextPath() + "/dashboard");
        } else {
            // Authentication failed
            req.setAttribute("error", "Invalid email or password.");
            req.getRequestDispatcher("/WEB-INF/jspFiles/login.jsp").forward(req, res);
        }
    }
}
