package ru.skillbox.searcher.process.website.parser;

import ru.skillbox.searcher.process.website.tools.Links;

import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class ParseResponse {

    private URL url;
    private int responseCode;
    private String content;
    private Set<URL> innerLinks;
    private Map<String, Integer> estimatedLemmas;

    public static Builder newBuilder() {
        return new ParseResponse().new Builder();
    }

    public URL getUrl() {
        return url;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getContent() {
        return content;
    }

    public Set<URL> getInnerLinks() {
        return innerLinks;
    }

    public Map<String, Integer> getEstimatedLemmas() {
        return estimatedLemmas;
    }

    public Stream<URL> getInnerLinksStream() {
        return this.innerLinks.stream();
    }

    public boolean isRedirected() {
        return this.responseCode / 100 == 3;
    }

    public String getRelativeUrl() {
        return url.getFile();
    }

    public String getRootUrl() {
        return Links.defineStringRootFromUrl(this.url);
    }

    public void setInnerLinks(Set<URL> innerLinks) {
        this.innerLinks = innerLinks;
    }

    public class Builder {

        private Builder() {
            // private constructor
        }

        public Builder setUrl(URL url) {
            ParseResponse.this.url = url;
            return this;
        }

        public Builder setResponseCode(int responseCode) {
            ParseResponse.this.responseCode = responseCode;
            return this;
        }

        public Builder setContent(String content) {
            ParseResponse.this.content = content;
            return this;
        }

        public Builder setInnerLinks(Set<URL> innerLinks) {
            ParseResponse.this.innerLinks = innerLinks;
            return this;
        }

        public Builder setEstimatedLemmas(Map<String, Integer> lemmaMap) {
            ParseResponse.this.estimatedLemmas = lemmaMap;
            return this;
        }


        public ParseResponse build() {
            return ParseResponse.this;
        }

    }
}
