package ru.skillbox.searcher.process.website.searcher.pageResult;


import ru.skillbox.searcher.config.SiteConfig;
import ru.skillbox.searcher.dto.PageDTO;
import ru.skillbox.searcher.service.search.dto.RankingPageDTO;
import ru.skillbox.searcher.view.Page;

import java.util.Collection;
import java.util.Map;



public interface PageResultProcessor {

    Collection<Page> process(Map<SiteConfig.Site, Collection<RankingPageDTO>> pageMap, Collection<String> stringCollection);


}
