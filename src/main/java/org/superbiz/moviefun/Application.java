package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ServletRegistrationBean actionServletRegistration(ActionServlet actionServlet) {
        return new ServletRegistrationBean(actionServlet, "/moviefun/*");
    }

    @Bean
    public DatabaseServiceCredentials databaseServiceCredentials(@Value("${vcap.services}") String  vcapServices ) {
        return new DatabaseServiceCredentials(vcapServices);

    }

    @Bean
    public DataSource albumsDataSource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource albumsDataSource = new MysqlDataSource();
        albumsDataSource.setURL(serviceCredentials.jdbcUrl("albums-mysql"));
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDataSource(albumsDataSource);
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    public DataSource movieDataSource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource movieDataSource = new MysqlDataSource();
        movieDataSource.setURL(serviceCredentials.jdbcUrl("movies-mysql"));
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDataSource(movieDataSource);
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    public HibernateJpaVendorAdapter hibernateJpaVendorAdapter(){
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        return hibernateJpaVendorAdapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBeanForMovies(DataSource movieDataSource, HibernateJpaVendorAdapter hibernateJpaVendorAdapter){
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBeanForMovies = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBeanForMovies.setDataSource(movieDataSource);
        localContainerEntityManagerFactoryBeanForMovies.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        localContainerEntityManagerFactoryBeanForMovies.setPackagesToScan("org.superbiz.moviefun.movies");
        localContainerEntityManagerFactoryBeanForMovies.setPersistenceUnitName("movies-mysql");
        return localContainerEntityManagerFactoryBeanForMovies;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBeanForAlbums(DataSource albumsDataSource, HibernateJpaVendorAdapter hibernateJpaVendorAdapter){
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBeanForAlbums = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBeanForAlbums.setDataSource(albumsDataSource);
        localContainerEntityManagerFactoryBeanForAlbums.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        localContainerEntityManagerFactoryBeanForAlbums.setPackagesToScan("org.superbiz.moviefun.albums");
        localContainerEntityManagerFactoryBeanForAlbums.setPersistenceUnitName("albums-mysql");
        return localContainerEntityManagerFactoryBeanForAlbums;
    }

    @Bean
    public PlatformTransactionManager platformTransactionManagerForMovies(EntityManagerFactory localContainerEntityManagerFactoryBeanForMovies) {

        return new JpaTransactionManager(localContainerEntityManagerFactoryBeanForMovies);
    }

    @Bean
    public PlatformTransactionManager platformTransactionManagerForAlbums(EntityManagerFactory localContainerEntityManagerFactoryBeanForAlbums) {

        return new JpaTransactionManager(localContainerEntityManagerFactoryBeanForAlbums);
    }

    @Bean
    public TransactionTemplate transactionTemplateAlbum(PlatformTransactionManager platformTransactionManagerForAlbums){
        return  new TransactionTemplate(platformTransactionManagerForAlbums);
    }

    @Bean
    public TransactionTemplate transactionTemplateMovie(PlatformTransactionManager platformTransactionManagerForMovies){
        return  new TransactionTemplate(platformTransactionManagerForMovies);
    }
}
