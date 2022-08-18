package ru.skillbox.searcher.process.executors.indexing.insertion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import ru.skillbox.searcher.config.AppConfig;
import ru.skillbox.searcher.exception.manuallyInterruption.ManuallyInterruptedIndexingException;
import ru.skillbox.searcher.logs.LogUtils;
import ru.skillbox.searcher.process.shutdown.Shutdownable;
import ru.skillbox.searcher.process.website.parser.ParseResponse;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.concurrent.*;

public class ParseResponseInserter implements Shutdownable {
    private static final Logger logger = LogManager.getLogger("AppFile");

    @Autowired
    private AppConfig appConfig;
    private ParseResponseInsertExecutor insertExecutor;
    private ExecutorService executorService;

    @PostConstruct
    private void  initBeans() {
        this.insertExecutor = appConfig.parseResponseMultiTablesInsertExecutor();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public boolean execute(Collection<ParseResponse> collection) {
        Callable<Boolean> executeCallable = () -> insertExecutor.execute(collection);
        Future<Boolean> future = this.executorService.submit(executeCallable);

        try {
            return future.get();
        } catch (InterruptedException interruptedException) {
            throw new ManuallyInterruptedIndexingException();
        } catch (Exception e) {
            logger.error(LogUtils.getStackTrace(e));
            return false;
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
        return false;
    }
}
