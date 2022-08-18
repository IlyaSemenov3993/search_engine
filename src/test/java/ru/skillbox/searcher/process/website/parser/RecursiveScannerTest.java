package ru.skillbox.searcher.process.website.parser;

import org.jsoup.Jsoup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.skillbox.searcher.config.AppConfig;
import ru.skillbox.searcher.config.SiteConfig;

import java.net.MalformedURLException;
import java.util.Collection;

import static helpers.SiteUtils.testSite;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class RecursiveScannerTest {

    @Autowired
    private RecursiveScanner recursiveScanner;

    @Test
    public void executeTest1() {
        Collection<ParseResponse> parserResponseSet = recursiveScanner.execute(testSite);
        assertFalse(parserResponseSet.isEmpty());

        int parserResponseCount = parserResponseSet.size();
        long stringCount = parserResponseSet.stream()
                .map(ParseResponse::getUrl)
                .distinct()
                .count();
        assertEquals(parserResponseCount, stringCount);
    }

//    @Test
//    public void executeTest2() {
//        String url = "https://volochek.life/";
//        RecursiveScanner recursiveScanner = new RecursiveScanner();
//        RecursiveScannerTask recursiveScannerTask = appConfig.recursiveScannerTask(url);
//
//        Collection<ParseResponse> parserResponseSet = recursiveScanner.execute(recursiveScannerTask);
//        boolean actual = !parserResponseSet.isEmpty();
//        assertTrue(actual);
//
//        int parserResponseCount = parserResponseSet.size();
//        long stringCount = parserResponseSet.stream()
//                .map(ParseResponse::getUrl)
//                .distinct()
//                .count();
//        assertEquals(parserResponseCount, stringCount);
//    }
//
//    @Test
//    public void executeTest3() {
//        String url = "http://radiomv.ru/";
//        RecursiveScanner recursiveScanner = new RecursiveScanner();
//        RecursiveScannerTask recursiveScannerTask = new RecursiveScannerTask(url);
//
//        Collection<ParseResponse> parserResponseSet = recursiveScanner.execute(recursiveScannerTask);
//        boolean actual = !parserResponseSet.isEmpty();
//        assertTrue(actual);
//
//        int parserResponseCount = parserResponseSet.size();
//        long stringCount = parserResponseSet.stream()
//                .map(ParseResponse::getUrl)
//                .distinct()
//                .count();
//        assertEquals(parserResponseCount, stringCount);
//    }
//
//    @Test
//    public void executeTest4() {
//        String url = "https://ipfran.ru/";
//        RecursiveScanner recursiveScanner = new RecursiveScanner();
//        RecursiveScannerTask recursiveScannerTask = new RecursiveScannerTask(url);
//
//        Collection<ParseResponse> parserResponseSet = recursiveScanner.execute(recursiveScannerTask);
//        boolean expected = this.isLinkActive(url);
//        boolean actual = !parserResponseSet.isEmpty();
//        assertEquals(expected, actual);
//
//        int parserResponseCount = parserResponseSet.size();
//        long stringCount = parserResponseSet.stream()
//                .map(ParseResponse::getUrl)
//                .distinct()
//                .count();
//        assertEquals(parserResponseCount, stringCount);
//    }
//
//    @Test
//    public void executeTest5() {
//        String url = "https://dombulgakova.ru/";
//        RecursiveScanner recursiveScanner = new RecursiveScanner();
//        RecursiveScannerTask recursiveScannerTask = new RecursiveScannerTask(url);
//
//        Collection<ParseResponse> parserResponseSet = recursiveScanner.execute(recursiveScannerTask);
//        boolean expected = this.isLinkActive(url);
//        boolean actual = !parserResponseSet.isEmpty();
//        assertEquals(expected, actual);
//
//        int parserResponseCount = parserResponseSet.size();
//        long stringCount = parserResponseSet.stream()
//                .map(ParseResponse::getUrl)
//                .distinct()
//                .count();
//        assertEquals(parserResponseCount, stringCount);
//    }
//
//    public void executeTest6() {
//        String url = "https://nikoartgallery.com/";
//        RecursiveScanner recursiveScanner = new RecursiveScanner();
//        RecursiveScannerTask recursiveScannerTask = new RecursiveScannerTask(url);
//
//        Collection<ParseResponse> parserResponseSet = recursiveScanner.execute(recursiveScannerTask);
//        boolean expected = this.isLinkActive(url);
//        boolean actual = !parserResponseSet.isEmpty();
//        assertEquals(expected, actual);
//
//        int parserResponseCount = parserResponseSet.size();
//        long stringCount = parserResponseSet.stream()
//                .map(ParseResponse::getUrl)
//                .distinct()
//                .count();
//        assertEquals(parserResponseCount, stringCount);
//    }
//
//    @Test
//    public void executeTest7() {
//        String url = "https://www.svetlovka.ru/";
//        RecursiveScanner recursiveScanner = new RecursiveScanner();
//        RecursiveScannerTask recursiveScannerTask = new RecursiveScannerTask(url);
//
//        Collection<ParseResponse> parserResponseSet = recursiveScanner.execute(recursiveScannerTask);
//        boolean expected = this.isLinkActive(url);
//        boolean actual = !parserResponseSet.isEmpty();
//        assertEquals(expected, actual);
//
//        int parserResponseCount = parserResponseSet.size();
//        long stringCount = parserResponseSet.stream()
//                .map(ParseResponse::getUrl)
//                .distinct()
//                .count();
//        assertEquals(parserResponseCount, stringCount);
//    }
//
//    @Test
//    public void executeTest8() {
//        String url = "https://www.lutherancathedral.ru/";
//        RecursiveScanner recursiveScanner = new RecursiveScanner();
//        RecursiveScannerTask recursiveScannerTask = new RecursiveScannerTask(url);
//
//        Collection<ParseResponse> parserResponseSet = recursiveScanner.execute(recursiveScannerTask);
//        boolean expected = this.isLinkActive(url);
//        boolean actual = !parserResponseSet.isEmpty();
//        assertEquals(expected, actual);
//
//        int parserResponseCount = parserResponseSet.size();
//        long stringCount = parserResponseSet.stream()
//                .map(ParseResponse::getUrl)
//                .distinct()
//                .count();
//        assertEquals(parserResponseCount, stringCount);
//    }

    private boolean isLinkActive(String url) {
        final String userAgent = "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; " +
                "rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6";
        final String referrer = "http://www.google.com";

        try {
            Jsoup.connect(url)
                    .userAgent(userAgent)
                    .referrer(referrer)
                    .execute();

            return true;
        } catch (Exception exception) {
            return false;
        }
    }

}
