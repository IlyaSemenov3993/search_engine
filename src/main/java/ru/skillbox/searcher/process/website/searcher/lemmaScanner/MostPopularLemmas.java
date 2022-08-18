package ru.skillbox.searcher.process.website.searcher.lemmaScanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.skillbox.searcher.model.repository.LemmaRepository;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Component
public class MostPopularLemmas {
    private static final Logger fileLogger = LogManager.getLogger("AppFile");

    @Autowired
    private LemmaRepository lemmaRepository;

    private Set<String> MOST_POPULAR_LEMMAS;

    public Set<String> getMostPopularLemmas() {
        return MOST_POPULAR_LEMMAS;
    }

    @PostConstruct
    private void init(){
        this.refreshSet();
    }

    @Scheduled(cron = "0 0 * * * *")
    public void refreshSet() {
        MOST_POPULAR_LEMMAS = new HashSet<>(lemmaRepository.getMostPopularValues());

        fileLogger.info("MostPopularLemmasSet refreshing is finished");
    }
}
