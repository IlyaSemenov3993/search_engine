package ru.skillbox.searcher.process.lemma.morph.morphology;

import org.apache.lucene.morphology.LetterDecoderEncoder;
import org.apache.lucene.morphology.english.EnglishLetterDecoderEncoder;
import org.apache.lucene.morphology.russian.RussianLetterDecoderEncoder;
import ru.skillbox.searcher.process.lemma.morph.forbiddenParts.RussianForbiddenParts;
import ru.skillbox.searcher.process.lemma.morph.forbiddenParts.EnglishForbiddenParts;

public enum LanguageEnum {
    RUS("/org/apache/lucene/morphology/russian/morph.info"
            , new RussianLetterDecoderEncoder()
            , RussianForbiddenParts.getAsStringArray()
    ),
    ENG("/org/apache/lucene/morphology/english/morph.info"
            , new EnglishLetterDecoderEncoder()
            , EnglishForbiddenParts.getAsStringArray());

    private final String pathToDict;
    private final LetterDecoderEncoder decoderEncoder;
    private final String[] forbiddenParts;

    LanguageEnum(String pathToDict, LetterDecoderEncoder decoderEncoder, String[] forbiddenParts) {
        this.pathToDict = pathToDict;
        this.decoderEncoder = decoderEncoder;
        this.forbiddenParts = forbiddenParts;
    }

    public String getPathToDict() {
        return pathToDict;
    }

    public LetterDecoderEncoder getDecoderEncoder() {
        return decoderEncoder;
    }

    public String[] getForbiddenParts() {
        return forbiddenParts;
    }
}
