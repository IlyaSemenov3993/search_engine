package ru.skillbox.searcher.process.website.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URL;


public class Parser {
    private static final Logger logger = LogManager.getLogger("AppFile");

    @Autowired
    private ParseResponseFactory parseResponseFactory;

    @Value("${user.agent}")
    private String userAgent;

    @Value("${referrer}")
    private String referrer;

    @Value("${parse.timeout}")
    private int timeout;

    @Value("#{new Boolean('${need.parse.timeout}')}")
    private boolean needTimeout;


    public ParseResponse parseWebsite(URL url) throws IOException {
        try {
            Document response = this.getDocument(url);
            return parseResponseFactory.createParserResponse(response, url);
        } catch (HttpStatusException httpStatusException) {
            logger.info("Got {} response after request to {}", httpStatusException.getStatusCode(), httpStatusException.getUrl());
            return parseResponseFactory.createParserResponse(httpStatusException, url);
        }
    }

    public ParseResponse parsePage(URL url) throws IOException {
        try {
            Document response = this.getDocument(url);
            return parseResponseFactory.createPageParserResponse(response, url);
        } catch (HttpStatusException httpStatusException) {
            logger.info("Got {} response after request to {}", httpStatusException.getStatusCode(), httpStatusException.getUrl());
            return parseResponseFactory.createParserResponse(httpStatusException, url);
        }
    }


    private Document getDocument(URL url) throws IOException {
        Connection connection = Jsoup.connect(url.toString())
                .userAgent(userAgent)
                .referrer(referrer);

        if (needTimeout) {
            connection = connection.timeout(timeout);
        }
        return connection.get();
    }


}
