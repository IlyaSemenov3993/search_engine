package ru.skillbox.searcher.model.nativeExecutors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.searcher.model.DbConnection;

import java.sql.SQLException;
import java.sql.Statement;

public abstract class QueryExecutor<E> {

    @Value("${limit.query.length}")
    private int LIMIT_QUERY_LEN;

    @Autowired
    private DbConnection dbConnection;

    protected StringBuilder builder;

    public void appendEntity(E entity) {
        this.appendSeparator();
        this.appendRow(entity);
    }

    private void appendRow(E entity) {
        if (builder == null) {
            builder = new StringBuilder(getQueryHead());
        }

        builder.append("\n");
        String row = getSqlRow(entity);
        builder.append(row);
    }

    @Transactional
    public void executeQuery() throws SQLException {
        if (builder == null) {
            return;
        }

        String queryTail = getQueryTail();
        builder.append(queryTail);

        try (Statement statement = dbConnection.connection().createStatement()) {
            statement.execute(builder.toString());
        }

        builder = null;
    }

    protected void appendSeparator() {
        if (builder != null) {
            String separator = getRowsSeparator();
            builder.append(separator);
        }
    }

    public boolean isQueryFull() {
        return builder != null && builder.length() > LIMIT_QUERY_LEN;
    }


    protected abstract String getSqlRow(E entity);

    protected abstract String getQueryHead();

    protected abstract String getRowsSeparator();

    protected abstract String getTableName();

    protected abstract String getQueryTail();
}
