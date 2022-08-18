package ru.skillbox.searcher.process.lemma.morph.morphology;

import java.util.Arrays;
import java.util.function.Predicate;

public class MorphFilter implements Predicate<String> {
    private final String[] FORBIDDEN_PARTS;

    public MorphFilter(String[] forbiddenParts) {
        this.FORBIDDEN_PARTS = forbiddenParts;
    }

    public boolean test(String wordInfo) {
       return Arrays.asList(FORBIDDEN_PARTS)
               .stream()
               .noneMatch(part -> wordInfo.contains(part));
    }

}
