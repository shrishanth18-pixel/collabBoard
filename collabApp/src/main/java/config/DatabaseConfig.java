package config;

public final class DatabaseConfig {
    private DatabaseConfig() {}

    public static String url() {
        return first(System.getenv("POSTGRE_URL"), System.getenv("POSTGRES_URL"),
                System.getProperty("postgre_url"),
                "jdbc:postgresql://localhost:5432/collab");
    }

    public static String username() {
        return first(System.getenv("POSTGRE_USERNAME"), System.getenv("POSTGRES_USER"),
                System.getProperty("postgre_username"),
                "postgres");
    }

    public static String password() {
        return first(System.getenv("POSTGRE_PASSWORD"), System.getenv("POSTGRES_PASSWORD"),
                System.getProperty("postgre_password"),
                "");
    }

    private static String first(String... values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) return value.trim();
        }
        return "";
    }
}
