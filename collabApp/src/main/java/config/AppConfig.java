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
