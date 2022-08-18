package ru.skillbox.searcher.process.executors.indexing.insertion;

import org.springframework.beans.factory.annotation.Autowired;
import ru.skillbox.searcher.config.AppConfig;
import ru.skillbox.searcher.model.entity.LemmaEntity;
import ru.skillbox.searcher.model.entity.PageEntity;
import ru.skillbox.searcher.model.mapper.LemmaMapper;
import ru.skillbox.searcher.model.nativeExecutors.insert.LemmaInsertExecutor;
import ru.skillbox.searcher.model.nativeExecutors.insert.PageInsertExecutor;
import ru.skillbox.searcher.model.repository.SiteRepository;
import ru.skillbox.searcher.model.mapper.PageMapper;
import ru.skillbox.searcher.exception.manuallyInterruption.ManuallyInterruptedIndexingException;
import ru.skillbox.searcher.logs.ParseResponseMultiTablesLogger;
import ru.skillbox.searcher.process.website.parser.ParseResponse;
import ru.skillbox.searcher.process.website.tools.Links;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class ParseResponseInsertExecutor {

    @Autowired
    private AppConfig appConfig;
    @Autowired
    private ParseResponseMultiTablesLogger logger;
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private PageMapper pageMapper;
    @Autowired
    private LemmaMapper lemmaMapper;
    private PageInsertExecutor pageInsertExecutor;
    private LemmaInsertExecutor lemmaInsertExecutor;

    private UUID siteId;

    @PostConstruct
    public void initBeans() {
        this.pageInsertExecutor = appConfig.pageInsertExecutor();
        this.lemmaInsertExecutor = appConfig.lemmaInsertExecutor();
    }

    public boolean execute(Collection<ParseResponse> collection) throws SQLException, ManuallyInterruptedIndexingException {
        if (collection.isEmpty()) {
            return false;
        }
        this.defineSiteId(collection);
        return this.processInsertion(collection);
    }

    private boolean processInsertion(Collection<ParseResponse> collection) throws ManuallyInterruptedIndexingException {
        try {
            for (ParseResponse parseResponse : collection) {
                PageEntity pageEntity = this.createPageDAO(parseResponse);
                this.pageInsertExecutor.appendEntity(pageEntity);
                this.logger.increasePageCount();

                this.processEstimatedLemmas(parseResponse, pageEntity);
            }
            executeInsertion();

            this.logger.logSuccess();
            return true;

        } catch (Exception exception) {
            this.logger.logFail(exception);
            return false;
        }
    }


    private void processEstimatedLemmas(ParseResponse parseResponse, PageEntity pageEntity) throws SQLException {
        Map<String, Integer> lemmaMap = parseResponse.getEstimatedLemmas();
        for (String lemma : lemmaMap.keySet()) {

            LemmaEntity lemmaEntity = lemmaMapper.getEntity(pageEntity.getId(), lemma, lemmaMap.get(lemma));
            this.lemmaInsertExecutor.appendEntity(lemmaEntity);
            this.logger.increaseLemmaCount();

            if (isExecutorFull()) {
                this.executeInsertion();
            }
        }
    }

    private boolean isExecutorFull() {
        return lemmaInsertExecutor.isQueryFull();
    }

    private void executeInsertion() throws SQLException {
        pageInsertExecutor.executeQuery();
        lemmaInsertExecutor.executeQuery();
    }

    private PageEntity createPageDAO(ParseResponse parseResponse) {
        PageEntity pageEntity = pageMapper.getEntity(parseResponse);
        pageEntity.setSiteId(siteId);
        return pageEntity;
    }

    private void defineSiteId(Collection<ParseResponse> collection) {
        ParseResponse someParseResponse = collection.stream().findAny().get();

        URL url = someParseResponse.getUrl();
        String parentUrl = Links.defineStringRootFromUrl(url);

        this.siteId = siteRepository.getIdByUrl(parentUrl);
    }

}
