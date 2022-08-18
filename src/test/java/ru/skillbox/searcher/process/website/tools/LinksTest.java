package ru.skillbox.searcher.process.website.tools;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static ru.skillbox.searcher.process.website.tools.Links.*;

public class LinksTest {


    @Test
    public void createURLTest() throws MalformedURLException {
        String link = "http://www.playboy.com";
        URL expected = new URL("https://www.playboy.com");
        URL actual = createURL(link);
        assertEquals(expected, actual);

        link = "http://www.playboy.com/";
        actual = createURL(link);
        assertEquals(expected, actual);

        link = "http://playboy.com/";
        actual = createURL(link);
        assertEquals(expected, actual);

        link = "www.playboy.com/";
        actual = createURL(link);
        assertNull(actual);
    }


    @Test
    public void defineStringRootFromUrlTest() throws MalformedURLException {
        URL url = new URL("http://www.playboy.com");
        String expected = "https://www.playboy.com";
        String actual = defineStringRootFromUrl(url);
        assertEquals(expected, actual);

        url = new URL("http://www.playboy.com/");
        actual = defineStringRootFromUrl(url);
        assertEquals(expected, actual);

        url = new URL("http://www.playboy.com/link.html");
        actual = defineStringRootFromUrl(url);
        assertEquals(expected, actual);
    }

    @Test
    public void definePureStringRootFromUrlTest() throws MalformedURLException {
        URL url = new URL("http://www.playboy.com");
        String expected = "http://www.playboy.com";
        String actual = definePureStringRootFromUrl(url);
        assertEquals(expected, actual);

        url = new URL("https://www.playboy.com");
        expected = "https://www.playboy.com";
        actual = definePureStringRootFromUrl(url);
        assertEquals(expected, actual);

        url = new URL("https://playboy.com");
        expected = "https://playboy.com";
        actual = definePureStringRootFromUrl(url);
        assertEquals(expected, actual);

        url = new URL("http://playboy.com/someLink");
        expected = "http://playboy.com";
        actual = definePureStringRootFromUrl(url);
        assertEquals(expected, actual);
    }

    @Test
    public void removeLastSlashTest() {
        String link = "http://www.playboy.com/";
        String expected = "http://www.playboy.com";
        String actual = Links.removeLastSlash(link);
        assertEquals(expected, actual);

        actual = Links.removeLastSlash(expected);
        assertEquals(expected, actual);
    }

    @Test
    public void isChildLinkTest() {
        String parent = "http://www.playboy.com";
        String child = "http://www.playboy.com/someUrl.html";
        assertTrue(isChildLink(child, parent));

        child = "http://www.skillbox.com/someUrl.html";
        assertFalse(isChildLink(child, parent));
    }

    @Test
    public void isNotFileTest() {
        String link = "http://www.playboy.com/someUrl.html";
        boolean actual = isNotFile(link);
        assertTrue(actual);

        link = "http://www.playboy.com/someUrl.jpg";
        actual = isNotFile(link);
        assertFalse(actual);

        link = "/someUrl.jpg";
        actual = isNotFile(link);
        assertFalse(actual);

        link = "/someUrl.JPG";
        actual = isNotFile(link);
        assertFalse(actual);
    }

    @Test
    public void getDomainTest() {
        String link = "http://www.playboy.com/someUrl.html";
        String expected = ".html";
        String actual = getDomain(link);
        assertEquals(expected, actual);

        link = "/someUrl.jpg";
        expected = ".jpg";
        actual = getDomain(link);
        assertEquals(expected, actual);

        link = "/someUrl,jpg";
        expected = "";
        actual = getDomain(link);
        assertEquals(expected, actual);
    }

    @Test
    public void prepareUrlTest() throws MalformedURLException {
        String link = "https://www.playback.ru";
        URL expected = new URL(link);
        URL actual = prepareUrl(link);
        assertEquals(expected, actual);

        link = "https://playback.ru/";
        actual = prepareUrl(link);
        assertEquals(expected, actual);

        link = "http://www.playback.ru/";
        actual = prepareUrl(link);
        assertEquals(expected, actual);
    }
}
