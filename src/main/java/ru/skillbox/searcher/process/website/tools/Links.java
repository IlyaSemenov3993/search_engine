package ru.skillbox.searcher.process.website.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class Links {
    private static final Logger logger = LogManager.getLogger("AppFile");
    public static final String REQUIRED_PROTOCOL = "https";


    public static URL createURL(String url) {
        try {
            URL result = new URL(url);
            return prepareUrl(result);
        } catch (MalformedURLException e) {
            logger.error("URL creation error from String:{}", url);
            return null;
        }
    }

    public static String defineStringRootFromUrl(URL url) {
        String host = prepareHost(url);
        return String.format("%s://%s", REQUIRED_PROTOCOL, host);
    }

    public static String definePureStringRootFromUrl(URL url) {
        return String.format("%s://%s", url.getProtocol(), url.getHost());
    }


    public static String removeLastSlash(String url) {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    public static boolean isParsedLinkValid(String url, String requestUrl) {
        return isNotFile(url)
                && isChildLink(url, requestUrl);
    }

    public static boolean isChildLink(String child, String parent) {
        return child.startsWith(parent);
    }

    public static boolean isNotFile(String link) {
        String domain = getDomain(link);

        return Arrays.stream(ForbiddenDomains.values())
                .map(ForbiddenDomains::getValue)
                .noneMatch(forbiddenDomain -> domain.equalsIgnoreCase(forbiddenDomain));
    }

    public static String getDomain(String link) {
        int pointAt = link.lastIndexOf(".");

        if (pointAt < 0) {
            return "";
        }

        return link.substring(pointAt);
    }

    public static URL prepareUrl(String link) throws MalformedURLException {
        URL url = new URL(link);
        return prepareUrl(url);
    }

    public static URL prepareUrl(URL url) throws MalformedURLException {
        String host = prepareHost(url);
        String file = removeLastSlash(url.getFile());
        return new URL(REQUIRED_PROTOCOL, host, file);
    }

    private static String prepareHost(URL url) {
        String host = url.getHost();
        if (!host.startsWith("www.")) {
            host = "www." + host;
        }
        return host;
    }

}
