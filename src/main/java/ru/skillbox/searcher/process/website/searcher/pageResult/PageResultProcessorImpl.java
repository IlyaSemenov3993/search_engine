package ru.skillbox.searcher.process.website.searcher.pageResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.skillbox.searcher.config.SiteConfig;
import ru.skillbox.searcher.model.repository.LemmaRepository;
import ru.skillbox.searcher.service.search.dto.RankingPageDTO;
import ru.skillbox.searcher.view.Page;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class PageResultProcessorImpl implements PageResultProcessor {

    @Autowired
    LemmaRepository lemmaRepository;

    @Override
    public Collection<Page> process(Map<SiteConfig.Site, Collection<RankingPageDTO>> pageMap, Collection<String> stringCollection) {
        Function<SiteConfig.Site , Stream<Page>> pageMapper = site ->
                pageMap.get(site)
                        .stream()
                        .map(rankingPageDTO -> new Page(site , rankingPageDTO , stringCollection));

        return pageMap.keySet().stream()
                .flatMap(pageMapper)
                .collect(Collectors.toList());
    }




}
