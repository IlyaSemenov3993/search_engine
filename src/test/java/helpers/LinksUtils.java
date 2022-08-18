package helpers;

import java.net.MalformedURLException;
import java.net.URL;

import static ru.skillbox.searcher.process.website.tools.Links.REQUIRED_PROTOCOL;

public class LinksUtils {

    public static URL defineUrlRootFromUrl(URL url) throws MalformedURLException {
        String host = prepareHost(url);
        return new URL(REQUIRED_PROTOCOL, host, "");
    }

    private static String prepareHost(URL url) {
        String host = url.getHost();
        if (!host.startsWith("www.")) {
            host = "www." + host;
        }
        return host;
    }
}
