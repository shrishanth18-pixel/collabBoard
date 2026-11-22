# CollabBoard

CollabBoard is a Jakarta Servlet and JSP web application with PostgreSQL persistence, Spring dependency injection, Hibernate board retrieval and a Java Swing desktop client.

## Requirements

- JDK 17 or later
- Apache Maven
- Apache Tomcat 11
- PostgreSQL

## Database setup

Create a PostgreSQL database named `collab`, then run:

```sql
\i src/main/resources/schema-updates.sql
```

The script creates the required PostgreSQL enums and application tables.

Configure the database with environment variables:

```sh
export POSTGRE_URL='jdbc:postgresql://localhost:5432/collab'
export POSTGRE_USERNAME='postgres'
export POSTGRE_PASSWORD='your-database-password'
```

Alternative environment names accepted by the application are `POSTGRES_URL`, `POSTGRES_USER`, and `POSTGRES_PASSWORD`.

Do not commit database passwords or `.env` files.

## Build

```sh
cd collabApp
mvn clean package
```

The generated WAR is:

```text
target/collabApp-1.0.0.war
```

Deploy it to Tomcat 11. When deployed under the `collabApp-1.0.0` context, open:

```text
http://localhost:8080/collabApp-1.0.0/
```

The root JSP forwards to the login servlet.

## Run the desktop application

```sh
POSTGRE_PASSWORD='your-database-password' mvn exec:java -Dexec.mainClass=desktop.CollabBoardSwingApp
```

## Test

```sh
mvn test
```

The frontend directory is not a separate running Node application; the web interface is implemented in JSP and inline CSS/JavaScript.
