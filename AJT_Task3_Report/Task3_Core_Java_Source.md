# Task 3 — Core Java Source

## Database and Spring Configuration

### `collabApp/src/main/java/config/DatabaseConfig.java`

```java
package config;

import java.io.InputStream;
import java.util.Properties;

public final class DatabaseConfig {
    private DatabaseConfig() {}

    public static String url() {
        return first(System.getenv("POSTGRE_URL"), System.getenv("POSTGRES_URL"),
                System.getProperty("postgre_url"), property("postgre_url"),
                "jdbc:postgresql://localhost:5432/collab");
    }

    public static String username() {
        return first(System.getenv("POSTGRE_USERNAME"), System.getenv("POSTGRES_USER"),
                System.getProperty("postgre_username"), property("postgre_username"),
                "postgres");
    }

    public static String password() {
        return first(System.getenv("POSTGRE_PASSWORD"), System.getenv("POSTGRES_PASSWORD"),
                System.getProperty("postgre_password"), property("postgre_password"), "");
    }

    private static String first(String... values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) return value.trim();
        }
        return "";
    }

    private static String property(String key) {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream(".env")) {
            if (input == null) return null;
            Properties properties = new Properties();
            properties.load(input);
            return properties.getProperty(key);
        } catch (Exception ex) {
            return null;
        }
    }
}
```

### `collabApp/src/main/java/config/SpringContextListener.java`

```java
package config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@WebListener
public class SpringContextListener implements ServletContextListener {
    private AnnotationConfigApplicationContext context;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        event.getServletContext().setAttribute("springContext", context);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (context != null) context.close();
    }
}
```

## Hibernate Entity, Repository and Service

### `collabApp/src/main/java/model/BoardEntity.java`

```java
package model;

import java.sql.Timestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "board")
public class BoardEntity {
    @Id
    private Integer id;

    @Column(name = "projectname")
    private String projectName;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status;

    @Column(name = "priority")
    private String priority;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "deadline")
    private Timestamp deadline;

    @Transient
    private String role;

    public Integer getId() { return id; }
    public String getProjectName() { return projectName; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getPriority() { return priority; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getDeadline() { return deadline; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
```

### `collabApp/src/main/java/repository/BoardHibernateRepository.java`

```java
package repository;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import model.BoardEntity;

public class BoardHibernateRepository {
    private final SessionFactory sessionFactory;

    public BoardHibernateRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<BoardEntity> findBoardsForUser(int userId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createNativeQuery(
                    "SELECT b.*, bm.role FROM board b JOIN board_member bm ON b.id = bm.board_id WHERE bm.users_id = :userId",
                    BoardEntity.class)
                    .setParameter("userId", userId)
                    .getResultList();
        }
    }
}
```

### `collabApp/src/main/java/service/BoardService.java`

```java
package service;

import java.util.List;
import model.BoardEntity;
import repository.BoardHibernateRepository;

public class BoardService {
    private final BoardHibernateRepository boardRepository;

    public BoardService(BoardHibernateRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public List<BoardEntity> getBoardsForUser(int userId) {
        return boardRepository.findBoardsForUser(userId);
    }
}
```

## Authentication Servlets

### `collabApp/src/main/java/Workspace/login.java`

```java
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

@WebServlet("/login")
public class login extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jspFiles/login.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String email = req.getParameter("email") == null ? "" : req.getParameter("email").trim().toLowerCase();
        String password = req.getParameter("password");

        if (email.isEmpty() || password == null || password.isEmpty()) {
            req.setAttribute("error", "Email and password are required.");
            req.getRequestDispatcher("/WEB-INF/jspFiles/login.jsp").forward(req, res);
            return;
        }

        String storedHash = getUsers.verifyPassword(email, res);
        if (storedHash != null && BCrypt.checkpw(password, storedHash)) {
            HashMap<String, String> userData = getUsers.selectFromUsers(email, req, res);
            HttpSession session = req.getSession();
            session.setMaxInactiveInterval(1800);
            String userIdStr = userData.get("user_id");
            if (userIdStr != null) session.setAttribute("user_id", Integer.valueOf(userIdStr));
            session.setAttribute("email", email);
            session.setAttribute("username", userData.get("username"));
            res.sendRedirect(req.getContextPath() + "/dashboard");
        } else {
            req.setAttribute("error", "Invalid email or password.");
            req.getRequestDispatcher("/WEB-INF/jspFiles/login.jsp").forward(req, res);
        }
    }
}
```

### `collabApp/src/main/java/Workspace/logout.java`

```java
package Workspace;

import java.io.IOException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/logout")
public class logout extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();
        res.sendRedirect(req.getContextPath() + "/login");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        doGet(req, res);
    }
}
```

### `collabApp/src/main/java/Workspace/AuthFilter.java`

```java
package Workspace;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter(urlPatterns = {"/dashboard", "/project", "/project/*", "/board", "/task/*", "/logout"})
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user_id") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        chain.doFilter(request, response);
    }
}
```
