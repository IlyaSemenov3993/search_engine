package ru.skillbox.searcher.process.website.searcher.pageGrouper;

import ru.skillbox.searcher.config.SiteConfig;
import ru.skillbox.searcher.dto.PageDTO;
import ru.skillbox.searcher.service.search.dto.RankingPageDTO;

import java.util.Collection;
import java.util.Map;

public interface PageResultGrouper {

    Map<SiteConfig.Site, Collection<RankingPageDTO>> groupPageResult(Collection<RankingPageDTO> rankingPageDTOS, String link);

}
