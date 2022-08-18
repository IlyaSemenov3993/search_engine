package ru.skillbox.searcher.service.search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.skillbox.searcher.config.SiteConfig;
import ru.skillbox.searcher.exception.searchProcess.*;
import ru.skillbox.searcher.logs.LogUtils;
import ru.skillbox.searcher.model.entity.PageEntity;
import ru.skillbox.searcher.model.mapper.PageMapper;
import ru.skillbox.searcher.model.repository.LemmaRepository;
import ru.skillbox.searcher.model.repository.SiteRepository;
import ru.skillbox.searcher.dto.PageDTO;
import ru.skillbox.searcher.process.shutdown.ShutdownableHolder;
import ru.skillbox.searcher.process.website.searcher.pageGrouper.PageResultGrouper;
import ru.skillbox.searcher.process.website.searcher.rankingProcess.PageRankingProcessor;
import ru.skillbox.searcher.service.search.dto.RankingPageDTO;
import ru.skillbox.searcher.service.search.response.SearchResponse;
import ru.skillbox.searcher.exception.searchProcess.NoSuitablePageException;
import ru.skillbox.searcher.process.website.searcher.lemmaScanner.LemmaScanner;
import ru.skillbox.searcher.process.website.searcher.pageResult.PageResultProcessor;
import ru.skillbox.searcher.process.website.searcher.pageSelector.PageSelector;
import ru.skillbox.searcher.view.Page;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    private static final Logger logger = LogManager.getLogger("AppFile");

    @Value("${search.default.limit}")
    private int DEFAULT_LIMIT;
    @Value("${search.query}")
    private String QUERY_KEY;
    @Value("${search.site}")
    private String SITE_KEY;
    @Value("${search.limit}")
    private String LIMIT_KEY;

    @Autowired
    private LemmaScanner lemmaScanner;

    @Autowired
    private LemmaRepository lemmaRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private PageSelector pageSelector;

    @Autowired
    private PageResultProcessor pageResultProcessor;

    @Autowired
    private PageRankingProcessor pageRankingProcessor;

    @Autowired
    private ShutdownableHolder shutdownableHolder;

    @Autowired
    private SiteConfig siteConfig;

    @Autowired
    private PageMapper pageMapper;

    @Autowired
    private PageResultGrouper pageResultGrouper;


    @Override
    public SearchResponse search(Map<String, String> requestParams) {
        int limit = this.defineLimit(requestParams);
        String query = requestParams.get(QUERY_KEY);
        String site = requestParams.get(SITE_KEY);

        try {
            Collection<Page> pages = this.searchPages(query, site, limit);
            return new SearchResponse(pages);
        } catch (SearchProcessException e) {
            logger.info("Searching failed cause of {}", e.getMessage());
            logger.debug(LogUtils.getStackTrace(e));
            return new SearchResponse(e.getMessage());
        }
    }


    private Collection<Page> searchPages(String request, String link, int limit) throws SearchProcessException {
        this.checkInput(request, link);

        Collection<String> stringCollection = this.getLemmas(request);
        Collection<String> orderedString = this.getOrderedString(stringCollection);
        Collection<PageDTO> pageDTOCollection = this.selectPages(orderedString, link);

        Collection<RankingPageDTO> rankingPageDTOS = pageRankingProcessor.process(pageDTOCollection , orderedString , limit);
        Map<SiteConfig.Site, Collection<RankingPageDTO>> pageDTOMap = this.pageResultGrouper.groupPageResult(rankingPageDTOS, link);

        return pageResultProcessor.process(pageDTOMap, orderedString);
    }

    private void checkInput(String request, String link) throws SearchProcessException {
        if (request == null || request.isEmpty()) {
            throw new RequestMissingException();
        }

        if (shutdownableHolder.isIndexing()) {
            throw new IsAlreadyIndexingException();
        }

        if (link != null) {
            this.checkInputLink(link);
        }
    }

    private void checkInputLink(String link) throws SiteNotDefinedException {
        SiteConfig.Site site = null;
        try {
            site = siteConfig.getSiteByStringUrl(link);
        } catch (MalformedURLException e) {
            logger.error("URL creation error from String:{}", link);
        }

        if (site == null) {
            throw new SiteNotDefinedException();
        }
    }


    private Collection<String> getLemmas(String request) throws LemmaNotDefinedException {
        Collection<String> result = lemmaScanner.getLemmas(request);

        if (result == null || result.isEmpty()) {
            throw new LemmaNotDefinedException();
        }

        return result;
    }

    private Collection<String> getOrderedString(Collection<String> stringCollection) throws NoSuitablePageException {
        Collection<String> result = lemmaRepository.getValueInOrderByCount(stringCollection);

        if (result == null ||
                result.size() != stringCollection.size()) {
            throw new NoSuitablePageException();
        }

        return result;
    }

    private Collection<PageDTO> selectPages(Collection<String> orderedString, String link) throws NoSuitablePageException {
        Collection<PageEntity> pageEntityCollection;

        if (link == null) {
            pageEntityCollection = pageSelector.selectPages(orderedString);
        } else {
            UUID siteId = this.getSiteIdByLink(link);
            pageEntityCollection = pageSelector.selectPages(orderedString, siteId);
        }

        if (pageEntityCollection == null || pageEntityCollection.isEmpty()) {
            throw new NoSuitablePageException();
        }

        return pageEntityCollection.stream()
                .map(pageMapper::getDTO)
                .collect(Collectors.toList());
    }

    private int defineLimit(Map<String, String> requestParams) {
        String limitValue = requestParams.get(LIMIT_KEY);
        try {
            int limit = Integer.parseInt(limitValue);

            if (limit < DEFAULT_LIMIT && limit > 0) {
                return limit;
            }
        } catch (NumberFormatException exception) {
            logger.error("Wrong limit value actual:{} but expected integer", limitValue);
        }

        return DEFAULT_LIMIT;
    }

    private UUID getSiteIdByLink(String link) {
        try {
            SiteConfig.Site site = siteConfig.getSiteByStringUrl(link);
            return siteRepository.getIdByUrl(site.getUrl());
        } catch (MalformedURLException malformedURLException) {
            logger.error(LogUtils.getStackTrace(malformedURLException));
            throw new RuntimeException(malformedURLException);
        }
    }

}
