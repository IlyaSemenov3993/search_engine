package ru.skillbox.searcher.process.lemma;

import ru.skillbox.searcher.process.lemma.morph.MultiMorphology;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Lemmatizator {

    public static Map<String, Integer> processTextWithCount(String text) {
        Map<String, Long> rawWords = countWords(text);
        Map<String, Integer> result = new HashMap<>();

        rawWords.keySet().stream()
                .forEach(word -> addLemmas(result, word, rawWords.get(word)));

        return result;
    }

    public static Collection<String> processText(String text) {
        String[] words = text.split("[^a-zA-Zа-яА-Я]");

        return Arrays.stream(words)
                .filter(w -> !w.isEmpty())
                .flatMap(s -> MultiMorphology.getNormalForms(s).stream())
                .collect(Collectors.toSet());
    }

    private static Map<String, Long> countWords(String text) {
        String[] words = text.split("[^a-zA-Zа-яА-Я]");

        return Arrays.stream(words)
                .filter(w -> !w.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));
    }


    private static void addLemmas(Map<String, Integer> map, String originalWord, long count) {
        Consumer<String> addLemma = lemmaWord ->
                map.merge(lemmaWord, (int) count, Integer::sum);

        MultiMorphology.getNormalForms(originalWord)
                .stream()
                .forEach(addLemma);
    }
}
