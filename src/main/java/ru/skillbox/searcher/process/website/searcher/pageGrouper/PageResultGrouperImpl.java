package ru.skillbox.searcher.process.website.searcher.pageGrouper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.skillbox.searcher.config.SiteConfig;
import ru.skillbox.searcher.logs.LogUtils;
import ru.skillbox.searcher.model.repository.SiteRepository;
import ru.skillbox.searcher.service.search.dto.RankingPageDTO;

import java.net.MalformedURLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class PageResultGrouperImpl implements PageResultGrouper {
    private static final Logger logger = LogManager.getLogger("AppFile");

    @Autowired
    private SiteConfig siteConfig;

    @Autowired
    private SiteRepository siteRepository;

    @Override
    public Map<SiteConfig.Site, Collection<RankingPageDTO>> groupPageResult(Collection<RankingPageDTO> rankingPageDTOS, String link) {
        if (link != null) {
            return this.groupWithLink(rankingPageDTOS, link);
        } else {
            return this.group(rankingPageDTOS);
        }
    }

    private Map<SiteConfig.Site, Collection<RankingPageDTO>> groupWithLink(Collection<RankingPageDTO> rankingPageDTOS, String link) {
        Map<SiteConfig.Site, Collection<RankingPageDTO>> result = new HashMap<>();

        SiteConfig.Site site = this.getSiteByLink(link);
        result.put(site, rankingPageDTOS);

        return result;
    }

    private Map<SiteConfig.Site, Collection<RankingPageDTO>> group(Collection<RankingPageDTO> pageDTOCollection) {
        Map<UUID, List<RankingPageDTO>> idMap = pageDTOCollection.stream()
                .collect(Collectors.groupingBy(rankingPageDTO ->
                        rankingPageDTO.getPageDTO().getSiteId()));

        return idMap.keySet().stream()
                .collect(Collectors.toMap(
                        key -> getSiteById(key),
                        key -> idMap.get(key)
                ));
    }


    private SiteConfig.Site getSiteByLink(String link) {
        try {
            return siteConfig.getSiteByStringUrl(link);
        } catch (MalformedURLException malformedURLException) {
            logger.error(LogUtils.getStackTrace(malformedURLException));
            throw new RuntimeException(malformedURLException);
        }
    }

    private SiteConfig.Site getSiteById(UUID siteId) {
        String url = siteRepository.findById(siteId).get().getUrl();

        try {
            return siteConfig.getSiteByStringUrl(url);
        } catch (MalformedURLException malformedURLException) {
            logger.error(LogUtils.getStackTrace(malformedURLException));
            throw new RuntimeException(malformedURLException);
        }
    }


}
