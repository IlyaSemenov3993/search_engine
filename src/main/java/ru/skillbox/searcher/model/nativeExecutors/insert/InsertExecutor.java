package ru.skillbox.searcher.model.nativeExecutors.insert;

import ru.skillbox.searcher.model.nativeExecutors.QueryExecutor;

public abstract class InsertExecutor<E> extends QueryExecutor<E> {
    private static final String OPERATION_NAME = "Insert";
    private static final String SEPARATOR = ",";
    private static final String QUERY_HEAD_LAYOUT = "INSERT INTO %s %s VALUES";


    protected String getQueryHead() {
        return String.format(QUERY_HEAD_LAYOUT , getTableName() , getColumns());
    }

    protected String getRowsSeparator() {
        return SEPARATOR;
    }

    protected String getOperationName() {
        return OPERATION_NAME;
    }

    protected abstract String getColumns();
}
