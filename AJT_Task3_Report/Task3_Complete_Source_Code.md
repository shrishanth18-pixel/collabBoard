# AJT Task 3 — Complete Source Code Documentation

## Project

**Project:** CollabBoard  
**Submission format:** Servlet, JSP, Spring, Hibernate/JPA, JDBC, and Swing  
**Source root:** `collabApp/src`  
**Build configuration:** `collabApp/pom.xml`

This document maps the implementation to the assignment requirements and indexes every relevant source file in the working project. The source files below are the actual project files; they were not rewritten or replaced with pseudocode.

## AJT Requirement Mapping

| Requirement                      | Implementing files                                                                                                                                       |
| -------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Login UI and authentication      | `Workspace/login.java`, `jspFiles/login.jsp`, `DB.java`, `getUsers.java`                                                                                 |
| Registration UI and validation   | `Workspace/register.java`, `jspFiles/register.jsp`                                                                                                       |
| Dashboard and navigation         | `Workspace/dashboard.java`, `jspFiles/dashboard.jsp`                                                                                                     |
| Board management                 | `Workspace/project.java`, `Workspace/updateProject.java`, `Workspace/DeleteProjectServlet.java`, `jspFiles/createBoard.jsp`, `jspFiles/boardDetails.jsp` |
| Task management                  | `Workspace/tasks.java`, `Workspace/UpdateTask.java`, `Workspace/DeleteTaskServlet.java`, `jspFiles/boardDetails.jsp`, `jspFiles/taskTable.html`          |
| Authentication filter and logout | `Workspace/AuthFilter.java`, `Workspace/logout.java`                                                                                                     |
| Friend and invitation features   | `Workspace/FriendsServlet.java`, `Workspace/InviteServlet.java`, `jspFiles/friends.jsp`, `jspFiles/invitations.jsp`                                      |
| JSTL and EL                      | All JSP files, especially `dashboard.jsp`, `boardDetails.jsp`, `login.jsp`, `register.jsp`                                                               |
| Spring dependency injection      | `config/AppConfig.java`, `config/SpringContextListener.java`, `repository/BoardHibernateRepository.java`, `service/BoardService.java`                    |
| Hibernate entity and ORM         | `model/BoardEntity.java`, `config/AppConfig.java`, `repository/BoardHibernateRepository.java`                                                            |
| JDBC CRUD and SQL                | `db/DB.java`, `db/GetBoard.java`, `db/Update.java`, `db/Delete.java`, `schema-updates.sql`                                                               |
| Swing desktop application        | `desktop/CollabBoardSwingApp.java`                                                                                                                       |
| Validation and error handling    | Servlet parameter checks, JSP HTML/JavaScript validation, Swing `JOptionPane` validation                                                                 |
| Session management               | `Workspace/login.java`, `Workspace/logout.java`, `Workspace/AuthFilter.java`, protected servlet methods                                                  |

## Complete Relevant File Inventory

### Java source files

- `collabApp/src/main/java/Workspace/AuthFilter.java`
- `collabApp/src/main/java/Workspace/BoardDetails.java`
- `collabApp/src/main/java/Workspace/DeleteProjectServlet.java`
- `collabApp/src/main/java/Workspace/DeleteTaskServlet.java`
- `collabApp/src/main/java/Workspace/FriendsServlet.java`
- `collabApp/src/main/java/Workspace/InviteServlet.java`
- `collabApp/src/main/java/Workspace/UpdateTask.java`
- `collabApp/src/main/java/Workspace/dashboard.java`
- `collabApp/src/main/java/Workspace/deleteProject.java`
- `collabApp/src/main/java/Workspace/deleteTask.java`
- `collabApp/src/main/java/Workspace/login.java`
- `collabApp/src/main/java/Workspace/logout.java`
- `collabApp/src/main/java/Workspace/project.java`
- `collabApp/src/main/java/Workspace/register.java`
- `collabApp/src/main/java/Workspace/tasks.java`
- `collabApp/src/main/java/Workspace/updateProject.java`
- `collabApp/src/main/java/config/AppConfig.java`
- `collabApp/src/main/java/config/DatabaseConfig.java`
- `collabApp/src/main/java/config/SpringContextListener.java`
- `collabApp/src/main/java/db/DB.java`
- `collabApp/src/main/java/db/Delete.java`
- `collabApp/src/main/java/db/GetBoard.java`
- `collabApp/src/main/java/db/Update.java`
- `collabApp/src/main/java/db/enums.java`
- `collabApp/src/main/java/db/getUsers.java`
- `collabApp/src/main/java/desktop/CollabBoardSwingApp.java`
- `collabApp/src/main/java/model/BoardEntity.java`
- `collabApp/src/main/java/repository/BoardHibernateRepository.java`
- `collabApp/src/main/java/service/BoardService.java`

### JSP, HTML, configuration, SQL and test files

- `collabApp/src/main/webapp/WEB-INF/jspFiles/boardDetails.jsp`
- `collabApp/src/main/webapp/WEB-INF/jspFiles/createBoard.jsp`
- `collabApp/src/main/webapp/WEB-INF/jspFiles/dashboard.jsp`
- `collabApp/src/main/webapp/WEB-INF/jspFiles/friends.jsp`
- `collabApp/src/main/webapp/WEB-INF/jspFiles/invitations.jsp`
- `collabApp/src/main/webapp/WEB-INF/jspFiles/login.jsp`
- `collabApp/src/main/webapp/WEB-INF/jspFiles/register.jsp`
- `collabApp/src/main/webapp/WEB-INF/jspFiles/taskTable.html`
- `collabApp/src/main/webapp/WEB-INF/web.xml`
- `collabApp/src/main/resources/schema-updates.sql`
- `collabApp/src/test/java/config/DatabaseConfigTest.java`
- `collabApp/pom.xml`
- `collabApp/frontend/package.json`

## Source-code snapshots

The full source of each file is retained verbatim at the project-relative path listed above. The following sections reproduce the central implementation files from the working project.

### File: `collabApp/pom.xml`

**Purpose:** Maven WAR build, Java 17, PostgreSQL, Jakarta Servlet/JSTL, Spring, Hibernate, BCrypt, JUnit, and Swing launch configuration.  
**AJT requirement:** Project configuration and dependency management.

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>collabApp</artifactId>
    <version>1.0.0</version>
    <packaging>war</packaging>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>io.github.cdimascio</groupId>
            <artifactId>dotenv-java</artifactId>
            <version>3.0.0</version>
        </dependency>
        <dependency>
            <groupId>jakarta.servlet</groupId>
            <artifactId>jakarta.servlet-api</artifactId>
            <version>6.0.0</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.mindrot</groupId>
            <artifactId>jbcrypt</artifactId>
            <version>0.4</version>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>42.7.3</version>
        </dependency>
        <dependency>
            <groupId>org.glassfish.web</groupId>
            <artifactId>jakarta.servlet.jsp.jstl</artifactId>
            <version>3.0.1</version>
        </dependency>
        <dependency>
            <groupId>jakarta.servlet.jsp.jstl</groupId>
            <artifactId>jakarta.servlet.jsp.jstl-api</artifactId>
            <version>3.0.2</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>6.2.1</version>
        </dependency>
        <dependency>
            <groupId>org.hibernate.orm</groupId>
            <artifactId>hibernate-core</artifactId>
            <version>6.6.2.Final</version>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.11.4</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <version>3.4.0</version>
            </plugin>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>3.5.0</version>
                <configuration>
                    <mainClass>desktop.CollabBoardSwingApp</mainClass>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.5.2</version>
            </plugin>
        </plugins>
    </build>
</project>
```

### File: `collabApp/src/main/java/config/AppConfig.java`

**Purpose:** Spring configuration and dependency-injection bean definitions.  
**AJT requirement:** Spring, Hibernate SessionFactory, service/repository injection.

```java
package config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.springframework.context.annotation.Bean;

import model.BoardEntity;
import repository.BoardHibernateRepository;
import service.BoardService;

@org.springframework.context.annotation.Configuration
public class AppConfig {
    @Bean(destroyMethod = "close")
    public SessionFactory sessionFactory() {
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        configuration.setProperty("hibernate.connection.url", DatabaseConfig.url());
        configuration.setProperty("hibernate.connection.username", DatabaseConfig.username());
        configuration.setProperty("hibernate.connection.password", DatabaseConfig.password());
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        configuration.setProperty("hibernate.hbm2ddl.auto", "validate");
        configuration.setProperty("hibernate.show_sql", "false");
        configuration.addAnnotatedClass(BoardEntity.class);
        return configuration.buildSessionFactory(
                new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build());
    }

    @Bean
    public BoardHibernateRepository boardHibernateRepository(SessionFactory sessionFactory) {
        return new BoardHibernateRepository(sessionFactory);
    }

    @Bean
    public BoardService boardService(BoardHibernateRepository boardRepository) {
        return new BoardService(boardRepository);
    }
}
```
