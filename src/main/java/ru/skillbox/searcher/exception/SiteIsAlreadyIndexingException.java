package ru.skillbox.searcher.exception;

import java.net.URL;

public class SiteIsAlreadyIndexingException extends Exception {
    private URL url;

    public SiteIsAlreadyIndexingException(URL url) {
        super();
        this.url = url;
    }

    public URL getUrl() {
        return url;
    }
}
