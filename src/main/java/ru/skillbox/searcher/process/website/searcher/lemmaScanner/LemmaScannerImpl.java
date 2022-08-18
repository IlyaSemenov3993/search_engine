package ru.skillbox.searcher.process.website.searcher.lemmaScanner;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.skillbox.searcher.process.lemma.Lemmatizator;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class LemmaScannerImpl implements LemmaScanner {

    @Autowired
    MostPopularLemmas mostPopularLemmas;

    @Override
    public Collection<String> getLemmas(String input) {
        Predicate<String> filterPopularWords = this.getPredicate();
        Collection<String> lemmas = Lemmatizator.processText(input);

        Collection<String> result = lemmas.stream()
                .filter(filterPopularWords)
                .collect(Collectors.toSet());

        if (result.isEmpty()) {
            result = lemmas;
        }

        return result;
    }

    private Predicate<String> getPredicate() {
        return string ->
                !mostPopularLemmas.getMostPopularLemmas().contains(string);
    }
}
