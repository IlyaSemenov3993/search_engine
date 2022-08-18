package ru.skillbox.searcher.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.skillbox.searcher.logs.LogUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


@Component
public class DbConnection implements AutoCloseable {
    private static final Logger logger = LogManager.getLogger("DbFile");


    private Connection connection;

    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.user}")
    private String dbUser;

    @Value("${db.pass}")
    private String dbPass;

    public Connection connection() {
        if (this.connection == null) {
            this.open();
        }
        return this.connection;
    }

    public Statement createStatement() throws SQLException {
        return this.connection().createStatement();
    }

    @Override
    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

    private void open() {
        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
        } catch (SQLException sqlException) {
            logger.error("Failed connection to the database with parameters:\n" +
                    "url:{} user:{}", dbUrl, dbUser);
            logger.debug(LogUtils.getStackTrace(sqlException));
            throw new RuntimeException(sqlException.getCause());
        }
    }

}
