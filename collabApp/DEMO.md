# Collab Board – AJT Submission Guide

## Run

1. Start PostgreSQL and ensure the `collab` database exists.
2. Set `postgre_url`, `postgre_username`, and `postgre_password` for the application environment.
3. Run `mvn clean package` from the project directory.
4. Deploy `target/collabApp-1.0.0.war` to Tomcat 11.
5. Open `/collabApp-1.0.0/register` if deployed under that context name.

## Implemented AJT Technologies

- **Servlets:** Authentication, dashboard, board, task, and logout request handling.
- **JSP:** Server-rendered registration, login, dashboard, board, and create-board views.
- **JSTL/EL:** Core tags and EL are used for conditional messages and board/task iteration.
- **HttpSession:** Login creates session attributes; logout invalidates the session; protected routes require `user_id`.
- **Spring Framework:** Annotation-based Spring context, constructor dependency injection, service, and repository components.
- **Hibernate ORM:** Board entity mapping and repository-native query executed through Hibernate `SessionFactory`.
- **JDBC:** Existing CRUD operations remain in DAO utility classes and use prepared statements.
- **CRUD:** Users, boards, tasks, and board memberships support relevant create/read/update/delete operations.

## Screenshot Checklist

1. Registration page.
2. Registration validation error.
3. Successful registration and login redirect.
4. Login page.
5. Invalid login message.
6. Authenticated dashboard showing the username and session user.
7. Unauthorized access to `/dashboard` before login.
8. Create-board form.
9. Successful board creation.
10. Database board record.
11. Board details page.
12. Create-task form.
13. Successful task creation.
14. Database task record.
15. Owner board update form and result.
16. Editor or owner task update/delete result.
17. Board delete confirmation and redirect.
18. Logout action.
19. Protected page access after logout.
20. PostgreSQL board/member/task tables.

## Demonstration Steps

1. Register a new user and show the password is stored hashed.
2. Log in and point out the session stores `user_id`, `username`, and `email`.
3. Copy the dashboard URL and open it after logout to demonstrate protection.
4. Create a board and verify it appears in the dashboard from PostgreSQL.
5. Open board details and create a task assigned to another registered username.
6. Update and delete the board or task as the OWNER/EDITOR.
7. Show a VIEWER being rejected by role checks.
8. In the project files, show the Spring constructor-injection configuration and Hibernate entity/repository.

## Database Schema Compatibility

The implementation uses the existing `collab` database names:

- `board_member.users_id`
- `task.assigned_to` as username text
- PostgreSQL enum values for status, priority, and roles

## Runtime Verification Status

- `mvn test` passed 3 JUnit tests.
- `mvn clean package` produced `target/collabApp-1.0.0.war` successfully.
- Tomcat 11 serves `/collabApp/register` and `/collabApp/login` with HTTP 200.
- Registration persisted a BCrypt password hash in PostgreSQL.
- Login created an authenticated session and allowed access to `/dashboard`.
- Logout invalidated the session and redirected protected access to `/login`.
- Board create, update, read, and cascade delete were verified live.
- Task create, update, and delete were verified live.
- Deleted-board access returned HTTP 403 because membership no longer exists.
- The Swing login application is running and initializes Spring and Hibernate successfully.

## Swing Run Command

```sh
POSTGRE_PASSWORD='<database-password>' mvn -f /absolute/path/to/collabApp/pom.xml exec:java -Dexec.mainClass=desktop.CollabBoardSwingApp
```

Do not place the real database password in source files or the POM.
