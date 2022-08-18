package beans.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.skillbox.searcher.model.DbConnection;
import ru.skillbox.searcher.model.entity.enums.SiteStatus;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

@Repository
public class TestSiteRepository {

    @Autowired
    private DbConnection dbConnection;


    public void deleteByStatus(SiteStatus status) throws SQLException {
        String layout = "DELETE FROM site\n" +
                "WHERE status = '%s'";
        String query = String.format(layout, status);

        try (Statement statement = dbConnection.createStatement()) {
            statement.execute(query);
        }
    }

    public SiteStatus getStatusByUrl(URL url) throws SQLException {
        String query = "SELECT status FROM site WHERE url = '%s'";
        query = String.format(query, url.toString());

        String status = this.getValue(query).get(0);
        return SiteStatus.valueOf(status);
    }

    public long countSiteHavingLemmas() throws SQLException {
        String query = "SELECT COUNT(DISTINCT site_id)\n" +
                "FROM site INNER JOIN page\n" +
                "ON site.id = site_id\n" +
                "WHERE code/100 = 2";

        String value = this.getValue(query).get(0);
        return Long.parseLong(value);
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


}
