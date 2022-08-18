package ru.skillbox.searcher.process.website.parser.contentAnalyser;

import java.util.Map;

public interface ContentAnalyser<String, E> {

    Map<java.lang.String, Integer> process(String content);
}
