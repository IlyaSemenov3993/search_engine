package ru.skillbox.searcher.process.executors.indexing.parse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import ru.skillbox.searcher.config.AppConfig;
import ru.skillbox.searcher.exception.ParseWebsiteException;
import ru.skillbox.searcher.exception.manuallyInterruption.ManuallyInterruptedIndexingException;
import ru.skillbox.searcher.logs.LogUtils;
import ru.skillbox.searcher.process.shutdown.Shutdownable;
import ru.skillbox.searcher.process.website.parser.ParseResponse;
import ru.skillbox.searcher.process.website.parser.Parser;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.util.concurrent.*;

public class ParseExecutor implements Shutdownable {
    private static final Logger logger = LogManager.getLogger("AppFile");

    @Autowired
    private AppConfig appConfig;
    private final ExecutorService executorService;
    private Parser parser;

    public ParseExecutor() {
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @PostConstruct
    private void initBeans(){
        this.parser = appConfig.parser();
    }

    public ParseResponse execute(URL url) throws ParseWebsiteException {
        Callable<ParseResponse> executeCallable = () -> parser.parsePage(url);

        try {
            Future<ParseResponse> future = this.executorService.submit(executeCallable);
            return future.get();
        } catch (InterruptedException | RejectedExecutionException e ) {
            throw new ManuallyInterruptedIndexingException();
        } catch (Exception e) {
            throw new ParseWebsiteException(e);
        } finally {
            this.executorService.shutdown();
        }
    }

    @Override
    public void shutdown() {
        this.executorService.shutdownNow();
    }

    @Override
    public boolean isStartShutdownable() {
        return true;
    }
}
