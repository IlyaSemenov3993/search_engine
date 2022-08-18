package beans.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.skillbox.searcher.model.DbConnection;
import ru.skillbox.searcher.model.entity.enums.SiteStatus;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Repository
public class TestLemmaRepository {

    @Autowired
    private DbConnection dbConnection;


    public List<String> getValueByPageId(UUID pageId) throws SQLException {
        String query = "SELECT value\n" +
                "FROM lemma\n" +
                "WHERE page_id = '%s'";
        query = String.format(query, pageId);

        return this.getValue(query);
    }

    public List<String> getValueDistinct() throws SQLException {
        String query = "SELECT DISTINCT value FROM lemma";

        return this.getValue(query);
    }

    public List<String> getTestValues() throws SQLException {
        String query = "SELECT value FROM lemma\n" +
                "GROUP BY value\n" +
                "HAVING COUNT(*) < 10\n" +
                "ORDER BY COUNT(*) DESC\n" +
                "LIMIT 2";

        return this.getValue(query);
    }

    public List<String> getTestValuesBySiteUrl(URL url) throws SQLException {
        String query = "WITH subtable AS (\n" +
                "    SELECT id\n" +
                "    FROM page\n" +
                "    WHERE site_id IN (SELECT id FROM site WHERE url = '%s')\n" +
                "    AND code/100 = 2\n" +
                "    ORDER BY (SELECT COUNT(*) FROM lemma WHERE page_id = page.id))\n" +
                "\n" +
                "SELECT value \n" +
                "FROM lemma l \n" +
                "WHERE l.page_id = (SELECT id FROM subtable LIMIT 1)\n" +
                "ORDER BY (SELECT COUNT(*) FROM lemma subl \n" +
                "            INNER JOIN subtable s ON subl.page_id = s.id\n" +
                "            WHERE l.value = subl.value)\n" +
                "LIMIT 2";

        query = String.format(query, url.toString());
        return this.getValue(query);
    }

    public void deleteRowsByPageId(int countRows, UUID pageId) throws SQLException {
        String query = "DELETE FROM lemma\n" +
                "WHERE id IN (SELECT id " +
                "            FROM lemma \n" +
                "            WHERE page_id = '%s'\n" +
                "            LIMIT %d)";

        query = String.format(query, pageId, countRows);
        this.execute(query);
    }

    public String getLemmaExistingInAllSites() throws SQLException {
        String query = "SELECT value\n" +
                "FROM lemma INNER JOIN page\n" +
                "ON page_id = page.id \n" +
                "GROUP BY value\n" +
                "HAVING COUNT(DISTINCT site_id) > 1\n" +
                "AND COUNT(page_id) <= 20\n" +
                "LIMIT 1";

        query = String.format(query);
        return this.getValue(query).get(0);
    }

    private List<String> getValue(String query) throws SQLException {
        List<String> result = new LinkedList<>();

        try (Statement statement = dbConnection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                result.add(resultSet.getString(1));
            }
        }
        return result;
    }

    private void execute(String query) throws SQLException {
        try (Statement statement = dbConnection.createStatement()) {
            statement.execute(query);
        }
    }
}
