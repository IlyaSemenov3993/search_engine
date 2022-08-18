package ru.skillbox.searcher.model.nativeExecutors.insert;

import ru.skillbox.searcher.model.entity.LemmaEntity;

import java.util.UUID;

public class LemmaInsertExecutor extends InsertExecutor<LemmaEntity> {
    private static final String TABLE_NAME = "lemma";
    private static final String TABLE_COLUMNS = "";

    @Override
    protected String getSqlRow(LemmaEntity entity) {
        final String insertLayout = "('%s', '%s', '%s', %s)";

        UUID id = entity.getId();
        UUID pageId = entity.getPageId();
        String value = entity.getValue();
        String rank = Float.toString(entity.getRank());

        return String.format(insertLayout,
                id,
                pageId,
                value,
                rank);
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String getQueryTail() {
        return "";
    }

    @Override
    protected String getColumns() {
        return TABLE_COLUMNS;
    }
}
