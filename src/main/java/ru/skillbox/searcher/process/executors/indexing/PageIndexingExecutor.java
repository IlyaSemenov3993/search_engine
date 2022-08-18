package ru.skillbox.searcher.process.executors.indexing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.searcher.config.AppConfig;
import ru.skillbox.searcher.exception.ParseWebsiteException;
import ru.skillbox.searcher.exception.manuallyInterruption.ManuallyInterruptedIndexingException;
import ru.skillbox.searcher.logs.LogUtils;
import ru.skillbox.searcher.model.repository.PageRepository;
import ru.skillbox.searcher.model.repository.SiteRepository;
import ru.skillbox.searcher.process.executors.indexing.insertion.ParseResponseInserter;
import ru.skillbox.searcher.process.executors.indexing.parse.ParseExecutor;
import ru.skillbox.searcher.process.shutdown.ShutdownableHolder;
import ru.skillbox.searcher.process.website.parser.ParseResponse;

import java.net.URL;
import java.util.*;

@Component
public class PageIndexingExecutor {
    private static final Logger logger = LogManager.getLogger("AppFile");

    @Autowired
    private AppConfig appConfig;
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private ShutdownableHolder shutdownableHolder;
    @Autowired
    private PageRepository pageRepository;
    private ParseExecutor parseExecutor;
    private ParseResponseInserter parseResponseInserter;


    public boolean indexPage(URL url) {
        try {
            this.defineParseExecutor(url);
            ParseResponse parseResponse = this.parseExecutor.execute(url);

            this.defineResponseInserter(url);
            return this.execute(parseResponse);

        } catch (Exception exception) {
            this.processIndexingException(exception, url);
            return false;
        } finally {
            this.shutdownableHolder.removePage(url);
        }
    }


    @Transactional
    private boolean execute(ParseResponse parseResponse) throws Exception {
        String sitePath = parseResponse.getRootUrl();
        String pagePath = parseResponse.getRelativeUrl();
        pageRepository.deleteByPath(sitePath, pagePath);

        Collection<ParseResponse> parseResponseCollection = (parseResponse == null) ?
                new ArrayList<>() : Collections.singleton(parseResponse);
        return parseResponseInserter.execute(parseResponseCollection);
    }

    private void defineParseExecutor(URL url) {
        this.parseExecutor = appConfig.parseExecutor();
        this.shutdownableHolder.addShutdownable(url, parseExecutor);
    }

    private void defineResponseInserter(URL url) {
        this.parseResponseInserter = appConfig.parseResponseInserter();
        this.shutdownableHolder.addShutdownable(url, parseResponseInserter);
    }

    private void processIndexingException(Exception exception, URL url) {
        if (exception instanceof ParseWebsiteException) {
            logger.info("URL:{} returns 0 links cause of:{}", url, exception.getMessage());
            logger.debug(LogUtils.getStackTrace(exception));
        } else if (exception instanceof ManuallyInterruptedIndexingException) {
            logger.info("Indexing {} was manually interrupted", url);
        } else {
            logger.error("Can't index page with URL: {}", url);
            logger.error(LogUtils.getStackTrace(exception));
            throw new RuntimeException(exception.getCause());
        }
    }


}
