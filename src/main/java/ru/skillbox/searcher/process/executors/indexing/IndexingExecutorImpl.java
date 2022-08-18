package ru.skillbox.searcher.process.executors.indexing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.searcher.config.AppConfig;
import ru.skillbox.searcher.config.SiteConfig;
import ru.skillbox.searcher.logs.LogUtils;
import ru.skillbox.searcher.model.entity.SiteEntity;
import ru.skillbox.searcher.model.entity.enums.SiteStatus;
import ru.skillbox.searcher.model.repository.PageRepository;
import ru.skillbox.searcher.process.executors.indexing.insertion.ParseResponseInserter;
import ru.skillbox.searcher.model.repository.SiteRepository;
import ru.skillbox.searcher.model.mapper.SiteMapper;
import ru.skillbox.searcher.exception.manuallyInterruption.ManuallyInterruptedIndexingException;
import ru.skillbox.searcher.exception.SiteIsAlreadyIndexingException;
import ru.skillbox.searcher.process.shutdown.ShutdownableHolder;
import ru.skillbox.searcher.process.website.parser.ParseResponse;
import ru.skillbox.searcher.process.website.parser.RecursiveScanner;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

public class IndexingExecutorImpl implements IndexingExecutor {
    private static final Logger logger = LogManager.getLogger("AppFile");

    @Autowired
    private AppConfig appConfig;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private ShutdownableHolder shutdownableHolder;
    @Autowired
    private SiteMapper siteMapper;
    private ParseResponseInserter parseResponseInserter;
    private RecursiveScanner recursiveScanner;
    private SiteConfig.Site site;

    public boolean execute(SiteConfig.Site site) {
        this.site = site;
        SiteEntity siteEntity = null;

        try {
            this.checkSite();
            siteEntity = this.defineSiteEntity(site);

            this.defineRecursiveScanner(site);
            Collection<ParseResponse> parserResponseSet = recursiveScanner.execute(site);

            this.defineResponseInserter(site);
            boolean isIndexingCompleted = this.execute(parserResponseSet, siteEntity.getId());

            this.processIndexingResult(isIndexingCompleted, siteEntity);
            return isIndexingCompleted;

        } catch (Exception exception) {
            this.processIndexingException(siteEntity, exception);
            return false;
        } finally {
            this.shutdownableHolder.removeSite(this.site);
        }
    }

    private void defineResponseInserter(SiteConfig.Site site) {
        this.parseResponseInserter = appConfig.parseResponseInserter();
        this.shutdownableHolder.addShutdownable(site, parseResponseInserter);
    }

    private void defineRecursiveScanner(SiteConfig.Site site) {
        this.recursiveScanner = appConfig.recursiveScanner();
        this.shutdownableHolder.addShutdownable(site, recursiveScanner);
    }


    @Transactional
    private boolean execute(Collection<ParseResponse> parseResponses, UUID siteId) throws Exception {
        pageRepository.deleteBySiteId(siteId);
        return parseResponseInserter.execute(parseResponses);
    }

    private void checkSite() throws SiteIsAlreadyIndexingException {
        if (!shutdownableHolder.addSite(site)) {
            throw new SiteIsAlreadyIndexingException(site.getUrl());
        }
    }

    private SiteEntity defineSiteEntity(SiteConfig.Site site) {
        SiteEntity siteEntity;
        Optional<SiteEntity> optional = siteRepository.getByUrl(site);

        if (optional.isEmpty()) {
            siteEntity = siteMapper.getEntity(site);
            siteEntity.setId(UUID.randomUUID());
        } else {
            siteEntity = optional.get();
        }

        this.changeSiteStatus(siteEntity, SiteStatus.INDEXING, null);
        return siteEntity;
    }


    private void changeSiteStatus(SiteEntity siteEntity, SiteStatus status, String statusError) {
        siteEntity.setStatus(status);
        siteEntity.setStatusTime(LocalDateTime.now());
        siteEntity.setLastError(statusError);
        siteRepository.save(siteEntity);
    }

    private void processIndexingResult(boolean isIndexingCompleted, SiteEntity siteEntity) {
        SiteStatus finalStatus = isIndexingCompleted ? SiteStatus.INDEXED : SiteStatus.FAILED;
        String errorMessage = isIndexingCompleted ? null : "Indexing failed";
        this.changeSiteStatus(siteEntity, finalStatus, errorMessage);
    }

    private void processIndexingException(SiteEntity siteEntity, Exception exception) {
        if (exception instanceof SiteIsAlreadyIndexingException) {
            URL url = ((SiteIsAlreadyIndexingException) exception).getUrl();
            logger.info("Site {} is already indexing.", url);
        } else {
            this.changeSiteStatus(siteEntity, SiteStatus.FAILED, exception.getMessage());

            if (exception instanceof ManuallyInterruptedIndexingException) {
                logger.info("Indexing {} was manually interrupted", siteEntity.getUrl());
            } else {
                logger.error(LogUtils.getStackTrace(exception));
                throw new RuntimeException(exception.getCause());
            }
        }
    }

}
