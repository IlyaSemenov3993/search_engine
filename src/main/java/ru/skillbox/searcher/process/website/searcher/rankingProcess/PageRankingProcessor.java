package ru.skillbox.searcher.process.website.searcher.rankingProcess;

import ru.skillbox.searcher.dto.PageDTO;
import ru.skillbox.searcher.service.search.dto.RankingPageDTO;
import ru.skillbox.searcher.view.Page;

import java.util.Collection;
import java.util.stream.Collectors;

public interface PageRankingProcessor {

    default Collection<RankingPageDTO> process(Collection<PageDTO> pageDTOCollection, Collection<String> lemmas, int limit) {
        Collection<RankingPageDTO> result = this.process(pageDTOCollection , lemmas);

        if (pageDTOCollection.size() <= limit) {
            return result;
        }

        return result.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    Collection<RankingPageDTO> process(Collection<PageDTO> pageDTOCollection, Collection<String> lemmas);
}
