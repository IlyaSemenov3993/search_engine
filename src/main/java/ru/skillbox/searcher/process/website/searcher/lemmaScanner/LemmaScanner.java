package ru.skillbox.searcher.process.website.searcher.lemmaScanner;

import java.util.Collection;

public interface LemmaScanner {

    Collection<String> getLemmas(String input);
}
