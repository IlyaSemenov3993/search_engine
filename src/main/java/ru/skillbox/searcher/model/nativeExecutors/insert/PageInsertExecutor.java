package ru.skillbox.searcher.model.nativeExecutors.insert;

import ru.skillbox.searcher.model.entity.PageEntity;

import java.util.UUID;

public class PageInsertExecutor extends InsertExecutor<PageEntity> {
    private static final String TABLE_NAME = "page";
    private static final String TABLE_COLUMNS = "(id, site_id, path, code, content)";

    @Override
    protected String getSqlRow(PageEntity entity) {
        final String insertLayout = "('%s', '%s', '%s', %d, '%s' )";

        UUID id = entity.getId();
        UUID siteId = entity.getSiteId();
        String url = entity.getPath().replaceAll("'", "''");
        int responseCode = entity.getCode();
        String content = entity.getContent() == null ? null : entity.getContent().replace("'", "''");

        return String.format(insertLayout,
                id,
                siteId,
                url,
                responseCode,
                content);
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
