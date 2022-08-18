package ru.skillbox.searcher.process.lemma.morph;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.morphology.LuceneMorphology;
import ru.skillbox.searcher.exception.IncorrectWordException;
import ru.skillbox.searcher.logs.LogUtils;
import ru.skillbox.searcher.process.lemma.morph.morphology.MorphFilteringMorphology;
import ru.skillbox.searcher.process.lemma.morph.morphology.LanguageEnum;

import java.util.*;
import java.util.function.Consumer;

import static ru.skillbox.searcher.process.lemma.morph.morphology.LanguageEnum.ENG;
import static ru.skillbox.searcher.process.lemma.morph.morphology.LanguageEnum.RUS;

public class MultiMorphology {
    private static final Logger appLogger = LogManager.getLogger("AppFile");
    private static final Logger lemmaLogger = LogManager.getLogger("LemmaFile");

    private static final Map<LanguageEnum, LuceneMorphology> morphMap = new HashMap<>();
    private static final Consumer<LanguageEnum> fillMorphMap = languageEnum -> {
        try {
            morphMap.put(languageEnum, new MorphFilteringMorphology(languageEnum));
        } catch (Exception exception) {
            appLogger.error("Can't create LuceneMorphology instance of {} language" +
                    " with cause:{}", languageEnum.name(), exception.getClass().getName());
            appLogger.debug(LogUtils.getStackTrace(exception));
            throw new RuntimeException(exception.getCause());
        }
    };

    static {
        Arrays.stream(LanguageEnum.values())
                .forEach(fillMorphMap);
    }

    public static List<String> getNormalForms(String word) {
        List<String> result = new ArrayList<>();
        word = word.toLowerCase();

        try {
            LuceneMorphology luceneMorphology = defineLucineMorphology(word);
            result = luceneMorphology.getNormalForms(word);
        } catch (IncorrectWordException e) {
            result.add(word);
        }

        return result;
    }

    private static LuceneMorphology defineLucineMorphology(String word) throws IncorrectWordException {
        LanguageEnum languageEnum = defineLanguage(word);
        return morphMap.get(languageEnum);
    }

    private static LanguageEnum defineLanguage(String word) throws IncorrectWordException {
        if (word.matches("[а-я]+")) {
            return RUS;
        } else if (word.matches("[a-z]+")) {
            return ENG;
        }

        lemmaLogger.info("Can't define language at word: {}", word);
        throw new IncorrectWordException(word);
    }

}
