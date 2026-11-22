package Workspace;

import java.io.IOException;

import org.mindrot.jbcrypt.BCrypt;

import db.DB;
import db.getUsers;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class register extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        SecurityUtils.ensureCsrfToken(req.getSession());
        req.getRequestDispatcher("/WEB-INF/jspFiles/register.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String name = req.getParameter("fullName");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        name = name == null ? "" : name.trim();
        email = email == null ? "" : email.trim().toLowerCase();

        if (name.isEmpty() || email.isEmpty() || password == null || confirmPassword == null) {
            req.setAttribute("error", "Full name, email, and password are required.");
            req.getRequestDispatcher("/WEB-INF/jspFiles/register.jsp").forward(req, res);
            return;
        }
        if (name.length() < 2 || name.length() > 50) {
            req.setAttribute("error", "Full name must contain between 2 and 50 characters.");
            req.getRequestDispatcher("/WEB-INF/jspFiles/register.jsp").forward(req, res);
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            req.setAttribute("error", "Enter a valid email address.");
            req.getRequestDispatcher("/WEB-INF/jspFiles/register.jsp").forward(req, res);
            return;
        }
        if (password.length() < 8) {
            req.setAttribute("error", "Password must be at least 8 characters.");
            req.getRequestDispatcher("/WEB-INF/jspFiles/register.jsp").forward(req, res);
            return;
        }
        if (!password.equals(confirmPassword)) {
            req.setAttribute("error", "Passwords do not match.");
            req.getRequestDispatcher("/WEB-INF/jspFiles/register.jsp").forward(req, res);
            return;
        }
        if (getUsers.checkEmail(email, res) != null) {
            req.setAttribute("error", "An account already exists with this email.");
            req.getRequestDispatcher("/WEB-INF/jspFiles/register.jsp").forward(req, res);
            return;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        if (DB.insertUser(name, email, hashedPassword, res)) {
            res.sendRedirect(req.getContextPath() + "/login");
        } else {
            req.setAttribute("error", "Registration failed. Please try again.");
            req.getRequestDispatcher("/WEB-INF/jspFiles/register.jsp").forward(req, res);
        }
    }
}
