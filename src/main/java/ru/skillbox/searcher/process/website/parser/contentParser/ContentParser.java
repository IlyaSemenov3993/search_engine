package ru.skillbox.searcher.process.website.parser.contentParser;


import java.util.Map;

public interface ContentParser<E> {

    String parse (String content, E e);

    Map<E, String> parse(String content);
}
