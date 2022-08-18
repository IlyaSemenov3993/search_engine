package ru.skillbox.searcher.logs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import ru.skillbox.searcher.logs.LogUtils;

@Component
public class ParseResponseMultiTablesLogger  {
    private static final Logger fileLogger = LogManager.getLogger("DbFile");

    private int pageCount;
    private int lemmaCount;

    public void increasePageCount() {
        pageCount++;
    }

    public void increaseLemmaCount() {
        lemmaCount++;
    }


    public void logSuccess() {
        fileLogger.info("Successfully inserted {} pages and {} lemmas"
                , pageCount
                , lemmaCount);
        this.resetCounters();
    }

    public void logFail(Exception exception) {
        fileLogger.error("Failed insertion of  {} pages and {} lemmas"
                , pageCount
                , lemmaCount);
        fileLogger.debug(LogUtils.getStackTrace(exception));
        this.resetCounters();
    }

    private void resetCounters() {
        pageCount = 0;
        lemmaCount = 0;
    }
}
