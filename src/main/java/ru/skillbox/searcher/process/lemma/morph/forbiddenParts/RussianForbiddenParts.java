package ru.skillbox.searcher.process.lemma.morph.forbiddenParts;

import java.util.Arrays;

public enum RussianForbiddenParts {
    МС ("МС"),
    МЕЖД ("МЕЖД"),
    ПРЕДЛ ("ПРЕДЛ"),
    СОЮЗ ("СОЮЗ");

    private String value;

    RussianForbiddenParts(String value) {
        this.value = value;
    }

    public static String[] getAsStringArray() {
        return Arrays.stream(values())
                .map(russianForbiddenParts -> russianForbiddenParts.value)
                .toArray(String[]::new);
    }
}
