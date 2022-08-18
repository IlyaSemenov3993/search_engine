package ru.skillbox.searcher.process.website.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ru.skillbox.searcher.config.AppConfig;
import ru.skillbox.searcher.config.SiteConfig;
import ru.skillbox.searcher.exception.manuallyInterruption.ManuallyInterruptedIndexingException;
import ru.skillbox.searcher.logs.LogUtils;

import javax.annotation.PostConstruct;
import java.net.ConnectException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveTask;

public class RecursiveScannerTask extends RecursiveTask<List<ParseResponse>> {
    private static final Logger logger = LogManager.getLogger("AppFile");

    private final URL url;
    private final Set<String> relativePathSet;

    @Value("${sleep.parse.timeout}")
    private int sleepTime;
    @Autowired
    private AppConfig appConfig;
    private Parser parser;


    public RecursiveScannerTask(SiteConfig.Site site) {
        this.url = site.getUrl();
        this.relativePathSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.addToPathSet(this.url);
    }

    private RecursiveScannerTask(URL url, RecursiveScannerTask recursiveScannerTask) {
        this.url = url;
        this.parser = recursiveScannerTask.parser;
        this.sleepTime = recursiveScannerTask.sleepTime;
        this.relativePathSet = recursiveScannerTask.relativePathSet;
    }

    @PostConstruct
    private void initBeans(){
        this.parser = appConfig.parser();
    }


    @Override
    protected List<ParseResponse> compute() throws ManuallyInterruptedIndexingException {
        List<ParseResponse> resultList = new LinkedList<>();
        try {
            Thread.sleep(sleepTime);

            Collection<RecursiveScannerTask> taskList = new LinkedList<>();
            ParseResponse parserResponse = parser.parseWebsite(url);

            if (parserResponse.isRedirected()) {
                return resultList;
            }

            resultList.add(parserResponse);
            parserResponse.getInnerLinksStream()
                    .forEach(link -> {

                        if (addToPathSet(link)) {
                            RecursiveScannerTask task = new RecursiveScannerTask(link, this);
                            task.fork();
                            taskList.add(task);
                        }
                    });
            parserResponse.setInnerLinks(null);

            taskList.forEach(t -> {
                List<ParseResponse> subCollection = t.join();
                resultList.addAll(subCollection);
            });

        } catch (Exception exception) {
            this.processException(exception, url);
        }

        return resultList;
    }


    private void processException(Exception exception, URL url) {
        if (exception instanceof CancellationException
                || exception instanceof InterruptedException
                || exception instanceof ConnectException) {
            logger.info("Parsing thread has been stoped");
        } else {
            logger.info("URL:{} returns 0 links cause of:{}", url.toString(), exception);
            logger.debug(LogUtils.getStackTrace(exception));
        }
    }

    private boolean addToPathSet(URL url) {
        return this.relativePathSet.add(url.getFile());
    }

}
