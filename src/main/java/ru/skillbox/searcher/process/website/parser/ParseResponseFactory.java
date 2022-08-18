package ru.skillbox.searcher.process.website.parser;

import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.skillbox.searcher.process.website.parser.contentAnalyser.ContentAnalyser;
import ru.skillbox.searcher.process.website.tools.Links;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ParseResponseFactory {
    @Autowired
    ContentAnalyser contentAnalyser;

    public ParseResponse createParserResponse(Document document, URL url) {
        int statusCode = document.connection().response().statusCode();
        String content = document.toString();
        Set<URL> innerLinks = getInnerLinks(document);
        Map<String, Integer> estimatedLemmas = this.contentAnalyser.process(content);

        return ParseResponse.newBuilder()
                .setResponseCode(statusCode)
                .setUrl(url)
                .setContent(content)
                .setInnerLinks(innerLinks)
                .setEstimatedLemmas(estimatedLemmas)
                .build();
    }

    public ParseResponse createPageParserResponse(Document document, URL url) {
        int statusCode = document.connection().response().statusCode();
        String content = document.toString();
        Map<String, Integer> estimatedLemmas = this.contentAnalyser.process(content);

        return ParseResponse.newBuilder()
                .setResponseCode(statusCode)
                .setUrl(url)
                .setContent(content)
                .setEstimatedLemmas(estimatedLemmas)
                .build();
    }

    public ParseResponse createParserResponse(HttpStatusException httpStatusException, URL url) {
        int statusCode = httpStatusException.getStatusCode();
        Set<URL> innerLinks = new HashSet<>();
        Map<String, Integer> estimatedLemmas = new HashMap<>();

        return ParseResponse.newBuilder()
                .setResponseCode(statusCode)
                .setUrl(url)
                .setContent(null)
                .setInnerLinks(innerLinks)
                .setEstimatedLemmas(estimatedLemmas)
                .build();
    }


    private Set<URL> getInnerLinks(Document document) {
        URL requestUrl = document.connection().request().url();
        String url = Links.definePureStringRootFromUrl(requestUrl);

        return document.select("a")
                .stream()
                .map(element -> element.absUrl("href"))
                .filter(link -> Links.isParsedLinkValid(link, url))
                .map(Links::createURL)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }


}
