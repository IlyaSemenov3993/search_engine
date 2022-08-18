package ru.skillbox.searcher.process.lemma.morph.forbiddenParts;

import java.util.Arrays;

public enum EnglishForbiddenParts {
    CONJ ("CONJ")
    //, PREP ("PREP")
    ;

    private String value;

    EnglishForbiddenParts(String value) {
        this.value = value;
    }

    public static String[] getAsStringArray() {
        return Arrays.stream(values())
                .map(englishForbiddenParts -> englishForbiddenParts.value)
                .toArray(String[]::new);
    }
}
