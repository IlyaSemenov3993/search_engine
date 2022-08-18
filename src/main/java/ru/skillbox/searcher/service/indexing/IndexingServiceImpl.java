package ru.skillbox.searcher.service.indexing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.skillbox.searcher.config.AppConfig;
import ru.skillbox.searcher.config.SiteConfig;
import ru.skillbox.searcher.model.repository.SiteRepository;
import ru.skillbox.searcher.logs.LogUtils;
import ru.skillbox.searcher.process.executors.indexing.IndexingExecutor;
import ru.skillbox.searcher.process.executors.indexing.PageIndexingExecutor;
import ru.skillbox.searcher.process.shutdown.ShutdownableHolder;
import ru.skillbox.searcher.process.website.tools.Links;
import ru.skillbox.searcher.service.cash.CashHolder;
import ru.skillbox.searcher.service.indexing.response.IndexingResponse;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Predicate;

@Service
public class IndexingServiceImpl implements IndexingService {
    public static final String INDEXING_ALREADY_STARTED = "Индексация уже запущена";
    public static final String SITE_IS_INDEXING_ALREADY_STARTED = "Индексация сайта уже запущена";
    public static final String PAGE_IS_INDEXING_ALREADY_STARTED = "Индексация страницы уже запущена";
    public static final String INDEXING_NOT_STARTED = "Индексация не запущена";
    public static final String STOP_INDEXING_ERROR_MESSAGE = "Остановить индексацию не удалось";
    public static final String INDEX_SITE_ERROR_MESSAGE = "Индексация сайта не удалась";
    public static final String INDEX_SITES_ERROR_MESSAGE = "Индексация сайтов не удалась";
    public static final String INDEX_PAGE_ERROR_MESSAGE = "Индексация страницы не удалась";
    public static final String INDEX_PAGE_OUT_OF_SCOPE_MESSAGE = "Данная страница находится за пределами " + "сайтов, указанных в конфигурационном файле";
    public static final String INCORRECT_URL_INPUT_MESSAGE = "Введена некорректная ссылка";
    private static final Logger logger = LogManager.getLogger("AppFile");
    @Value("${index.url}")
    private String URL_KEY;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private SiteConfig siteConfig;
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private ShutdownableHolder shutdownableHolder;
    @Autowired
    private PageIndexingExecutor pageIndexingExecutor;
    @Autowired
    private CashHolder cashHolder;


    @Override
    public IndexingResponse startIndexing() {
        boolean isIndexingStarted = shutdownableHolder.isIndexing();

        if (isIndexingStarted) {
            return new IndexingResponse(INDEXING_ALREADY_STARTED);
        }

        try {
            this.cashHolder.clearStatisticCash();
            if (this.processIndexing()) {
                return new IndexingResponse();
            }
        } catch (Exception exception) {
            logger.error("Entire indexing failed cause of:\n{}", LogUtils.getStackTrace(exception));
        }
        return new IndexingResponse(INDEX_SITES_ERROR_MESSAGE);
    }

    @Override
    public IndexingResponse stopIndexing() {
        boolean isIndexingStarted = shutdownableHolder.isIndexing();

        if (!isIndexingStarted) {
            return new IndexingResponse(INDEXING_NOT_STARTED);
        }

        try {
            shutdownableHolder.interrupt();
            return new IndexingResponse();
        } catch (Exception exception) {
            logger.error("Stop indexing failed cause of:\n{}", LogUtils.getStackTrace(exception));
        }
        return new IndexingResponse(STOP_INDEXING_ERROR_MESSAGE);
    }


    @Override
    public IndexingResponse indexSite(Map<String, String> requestParams) {
        String url = requestParams.get(URL_KEY);

        try {
            SiteConfig.Site site = siteConfig.addSite(url);
            IndexingExecutor indexingExecutor = appConfig.indexingExecutor();
            this.cashHolder.clearStatisticCash();
            if (indexingExecutor.execute(site)) {
                return new IndexingResponse();
            }
        } catch (MalformedURLException malformedURLException) {
            logger.info("Incorrect URL input:{}", url);
            return new IndexingResponse(INCORRECT_URL_INPUT_MESSAGE);
        } catch (Exception exception) {
            logger.error("Indexing of {} failed cause of:\n{}", url, LogUtils.getStackTrace(exception));
        }

        return new IndexingResponse(INDEX_SITE_ERROR_MESSAGE);
    }

    @Override
    public IndexingResponse indexPage(Map<String, String> requestParams) {
        URL url;
        String urlString = requestParams.get(URL_KEY);

        try {
            url = Links.prepareUrl(urlString);
        } catch (MalformedURLException malformedURLException) {
            logger.info("Incorrect URL input:{}", urlString);
            return new IndexingResponse(INCORRECT_URL_INPUT_MESSAGE);
        }

        SiteConfig.Site site = siteConfig.getSiteByPageUrl(url);
        if (site == null) {
            logger.info("Can't find {} in indexing site list", url);
            return new IndexingResponse(INDEX_PAGE_OUT_OF_SCOPE_MESSAGE);
        }

        if (this.shutdownableHolder.isIndexing(site)) {
            return new IndexingResponse(SITE_IS_INDEXING_ALREADY_STARTED);
        }

        if (!this.shutdownableHolder.addPage(url)) {
            return new IndexingResponse(PAGE_IS_INDEXING_ALREADY_STARTED);
        }

        this.cashHolder.clearStatisticCash();
        if (pageIndexingExecutor.indexPage(url)) {
            return new IndexingResponse();
        } else {
            return new IndexingResponse(INDEX_PAGE_ERROR_MESSAGE);
        }
    }


    private boolean processIndexing() throws InterruptedException {
        int siteCount = siteConfig.getSiteCount();
        List<Callable<Boolean>> callableList = new ArrayList<>(siteCount);

        for (SiteConfig.Site site : siteConfig.getList()) {
            IndexingExecutor indexingExecutor = appConfig.indexingExecutor();
            Callable callable = () -> indexingExecutor.execute(site);
            callableList.add(callable);
        }

        ExecutorService executor = Executors.newFixedThreadPool(siteCount);
        List<Future<Boolean>> futureList = executor.invokeAll(callableList);
        return this.computeFutureList(futureList);
    }

    private boolean computeFutureList(List<Future<Boolean>> futureList) {
        Predicate<Future<Boolean>> futurePredicate = booleanFuture -> {
            try {
                return booleanFuture.get();
            } catch (Exception e) {
                logger.error(LogUtils.getStackTrace(e));
                return false;
            }
        };

        return futureList.stream().allMatch(futurePredicate);
    }

}
