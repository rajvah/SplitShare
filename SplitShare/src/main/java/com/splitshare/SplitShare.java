package com.splitshare;

import com.config.SplitShareConfiguration;
import com.dao.split.SplitDao;
import com.dao.user.UserDao;
import com.google.common.collect.ImmutableList;
import com.models.split.Splits;
import com.models.user.Users;
import com.resource.split.SplitResource;
import com.resource.user.UserResource;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.SessionFactoryFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.sql.Connection;
import java.util.EnumSet;

/**
 * @author Harshit Rajvaidya
 */
public class SplitShare extends Application<SplitShareConfiguration> {

    public static void main(String[] args) throws Exception {
        new SplitShare().run(args);
    }

    @Override
    public void run(SplitShareConfiguration splitShareConfiguration, Environment environment) throws Exception {

        UserDao userModelDao = new UserDao(hibernateBundle.getSessionFactory());
        SplitDao splitDao = new SplitDao(hibernateBundle.getSessionFactory());
        final UserResource resource = new UserResource(userModelDao, splitDao);
        final SplitResource splitResource = new SplitResource(splitDao, userModelDao);
        runMigration(splitShareConfiguration, environment);
        environment.jersey().register(resource);
        environment.jersey().register(splitResource);

        Cors.insecure(environment);


    }

    HibernateBundle<SplitShareConfiguration> hibernateBundle = new HibernateBundle<SplitShareConfiguration>
            (ImmutableList.of(Users.class, Splits.class), new SessionFactoryFactory()) {
        @Override
        public PooledDataSourceFactory getDataSourceFactory(SplitShareConfiguration splitShareConfiguration) {
            return splitShareConfiguration.getDataSourceFactory();
        }
    };

    @Override
    public void initialize(Bootstrap<SplitShareConfiguration> bootstrap) {
        bootstrap.addBundle(hibernateBundle);
    }

    private void runMigration(SplitShareConfiguration splitShareConfiguration, Environment environment) {

        System.out.println("Running schema migration");
        ManagedDataSource dataSource = createMigrationDataSource(splitShareConfiguration, environment);

        try (Connection connection = dataSource.getConnection()) {
            JdbcConnection conn = new JdbcConnection(connection);

            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(conn);
            Liquibase liquibase = new Liquibase("migrations.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update("");

            System.out.println("Migration completed!");
        }
        catch (Exception ex) {
            throw new IllegalStateException("Unable to migrate database", ex);
        }
        finally {
            try {
                dataSource.stop();
            }
            catch (Exception ex) {
                System.out.println("Unable to stop data source used to execute schema migration"+ ex.getMessage());
            }
        }
    }

    private ManagedDataSource createMigrationDataSource(SplitShareConfiguration splitShareConfiguration, Environment environment) {

        DataSourceFactory dataSourceFactory = splitShareConfiguration.getDataSourceFactory();
        try{
            return dataSourceFactory.build(environment.metrics(), "migration-ds");
        } catch (Exception e){
            throw new IllegalStateException("Unable to initialize data source for schema migration", e);
        }

    }


}
