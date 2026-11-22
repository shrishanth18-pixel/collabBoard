package Workspace;

import java.security.SecureRandom;
import java.util.Base64;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class SecurityUtils {
    private static final SecureRandom RNG = new SecureRandom();

    private SecurityUtils() {}

    public static String ensureCsrfToken(HttpSession session) {
        Object existing = session.getAttribute("csrfToken");
        if (existing instanceof String && !((String) existing).isBlank()) {
            return (String) existing;
        }
        byte[] tokenBytes = new byte[24];
        RNG.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        session.setAttribute("csrfToken", token);
        return token;
    }

    public static boolean isValidCsrf(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        String expected = (String) session.getAttribute("csrfToken");
        String provided = request.getParameter("_csrf");
        return expected != null && provided != null && expected.equals(provided);
    }
}