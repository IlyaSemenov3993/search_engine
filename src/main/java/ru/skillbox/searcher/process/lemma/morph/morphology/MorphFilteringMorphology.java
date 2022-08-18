package ru.skillbox.searcher.process.lemma.morph.morphology;

import java.io.IOException;

public class MorphFilteringMorphology extends FilteringMorphology{

    public MorphFilteringMorphology(LanguageEnum languageEnum) throws IOException {
        super(languageEnum , new MorphFilter(languageEnum.getForbiddenParts()));
    }

}
