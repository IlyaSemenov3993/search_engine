package beans.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Component
public class UrlUtils {

    @Value("${index.url}")
    private String URL_KEY;
    @Value("${search.query}")
    private String QUERY_KEY;
    @Value("${search.site}")
    private String SITE_KEY;
    @Value("${search.limit}")
    private String LIMIT_KEY;
    @Value("${search.offset}")
    private String OFFSET_KEY;

    public String getIndexPageUrl(String url) {
        final String layout = "/indexPage?url=%s";
        return String.format(layout, url);
    }

    public String getIndexSiteUrl(String url) {
        final String layout = "/indexSite?url=%s";
        return String.format(layout, url);
    }

    public String getTestUri(String query, String site, int offset, int limit) {
        final String uri = "/search" +
                "/?" + QUERY_KEY + "=%s" +
                "&" + SITE_KEY + "=%s" +
                "&" + OFFSET_KEY + "=%d" +
                "&" + LIMIT_KEY + "=%d";

        return String.format(uri, query, site, offset, limit);
    }

    public String getTestUri(String query,  int offset, int limit) {
        final String uri = "/search" +
                "/?" + QUERY_KEY + "=%s" +
                "&" + OFFSET_KEY + "=%d" +
                "&" + LIMIT_KEY + "=%d";

        return String.format(uri, query, offset, limit);
    }

    public Map<String, String> getSearchInputParams(String query, String site, int limit) {
        Map<String, String> map = new HashMap<>();
        map.put(QUERY_KEY, query);
        map.put(SITE_KEY, site);
        map.put(LIMIT_KEY, Integer.toString(limit));
        return map;
    }

    public Map<String, String> getIndexInputParams(String url) {
        Map<String, String> map = new HashMap<>();
        map.put(URL_KEY, url);
        return map;
    }
}
