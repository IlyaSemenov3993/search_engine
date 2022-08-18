package helpers;

import ru.skillbox.searcher.config.SiteConfig;

import java.net.MalformedURLException;
import java.net.URL;

public class SiteUtils {

    public static SiteConfig.Site testSite;
    public static URL testUrl;

    static {
        try {
            testSite = initTestSite();
            testUrl = testSite.getUrl();
        } catch (MalformedURLException malformedURLException) {
            throw new RuntimeException(malformedURLException);
        }
    }


    private static SiteConfig.Site initTestSite() throws MalformedURLException {
        String url = "https://www.vendcrimea.ru/";
        String name = "www.vendcrimea.ru";
        return new SiteConfig.Site(url, name);
    }


}
