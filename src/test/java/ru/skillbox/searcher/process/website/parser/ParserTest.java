package ru.skillbox.searcher.process.website.parser;


import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.skillbox.searcher.config.AppConfig;
import ru.skillbox.searcher.process.website.tools.Links;

import java.net.URL;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class ParserTest {

    @Autowired
    private Parser parser;

    @Test
    public void parseWebsiteTestPositive1() throws Exception {
        String link = "https://skillbox.ru";
        URL url = Links.prepareUrl(link);
        ParseResponse parseResponse = parser.parseWebsite(url);

        int expectedResponseCode = 200;
        int actualResponseCode = parseResponse.getResponseCode();
        Assertions.assertEquals(expectedResponseCode, actualResponseCode);

        URL actualUrl = parseResponse.getUrl();
        assertEquals(url, actualUrl);

        Map<String, Integer> lemmaMap = parseResponse.getEstimatedLemmas();
        assertFalse(lemmaMap.isEmpty());
    }

    @Test
    public void parseWebsiteTestPositive2() throws Exception {
        String link = "https://skillbox.ru";
        URL url = Links.prepareUrl(link);
        ParseResponse parseResponse = parser.parsePage(url);

        int expectedResponseCode = 200;
        int actualResponseCode = parseResponse.getResponseCode();
        Assertions.assertEquals(expectedResponseCode, actualResponseCode);

        URL actualUrl = parseResponse.getUrl();
        assertEquals(url, actualUrl);

        assertNull(parseResponse.getInnerLinks());

        Map<String, Integer> lemmaMap = parseResponse.getEstimatedLemmas();
        assertFalse(lemmaMap.isEmpty());
    }

}
