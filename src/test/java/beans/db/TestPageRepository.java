package beans.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.skillbox.searcher.model.DbConnection;
import ru.skillbox.searcher.model.entity.PageEntity;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class TestPageRepository {

    @Autowired
    private DbConnection dbConnection;


    public Optional<PageEntity> getRandom() throws SQLException {
        String query = "SELECT *\n" +
                "FROM page\n" +
                "WHERE code/100 = 2\n" +
                "ORDER BY random()\n" +
                "LIMIT 1";

        return getEntityList(query).stream().findFirst();
    }

    public Collection<PageEntity> getByAllLemmaValueInAndPositiveCode(Collection<String> lemmaValues) throws SQLException {
        String queryLayout = "SELECT DISTINCT page.*\n" +
                "FROM page INNER JOIN lemma\n" +
                "ON page.id = page_id\n" +
                "WHERE value IN %s\n" +
                "AND code/100 = 2";

        return getEntityList(queryLayout, lemmaValues);
    }

    public PageEntity getRandomBySiteUrl(URL url) throws SQLException {
        return this.getRandomBySiteUrl(url.toString());
    }

    public PageEntity getRandomBySiteUrl(String url) throws SQLException {
        String query = "SELECT page.* \n" +
                "FROM page INNER JOIN site \n" +
                "ON site_id = site.id\n" +
                "WHERE site.url = '%s'\n" +
                "ORDER BY RANDOM()\n" +
                "LIMIT 1";

        query = String.format(query , url);
        return getEntityList(query).stream().findFirst().get();
    }

    private List<PageEntity> getEntityList(String queryLayout, Collection<String> values) throws SQLException {
        String value = values.stream()
                .map(lemma -> String.format("'%s'", lemma))
                .collect(Collectors.joining(",", "(", ")"));
        String query = String.format(queryLayout, value);
        return getEntityList(query);
    }


    private List<PageEntity> getEntityList(String query) throws SQLException {
        List<PageEntity> result = new LinkedList<>();

        try (Statement statement = dbConnection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                PageEntity pageEntity = new PageEntity();

                pageEntity.setId(UUID.fromString(resultSet.getString(1)));
                pageEntity.setSiteId(UUID.fromString(resultSet.getString(2)));
                pageEntity.setCode(resultSet.getInt(3));
                pageEntity.setContent(resultSet.getString(4));
                pageEntity.setPath(resultSet.getString(5));

                result.add(pageEntity);
            }
        }
        return result;
    }


}
