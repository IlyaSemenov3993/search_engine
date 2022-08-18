package ru.skillbox.searcher.process.website.parser;

import org.springframework.beans.factory.annotation.Autowired;
import ru.skillbox.searcher.config.AppConfig;
import ru.skillbox.searcher.config.SiteConfig;
import ru.skillbox.searcher.process.shutdown.Shutdownable;

import java.util.Collection;
import java.util.concurrent.ForkJoinPool;

public class RecursiveScanner implements Shutdownable {

    @Autowired
    private AppConfig appConfig;

    private ForkJoinPool pool;
    private RecursiveScannerTask task;

    public RecursiveScanner() {
        this.pool = new ForkJoinPool();
    }

    public Collection<ParseResponse> execute(SiteConfig.Site site) {
        this.task = appConfig.recursiveScannerTask(site);
        return this.pool.invoke(task);
    }

    @Override
    public void shutdown() {
        this.pool.shutdownNow();
    }

    @Override
    public boolean isStartShutdownable() {
        return true;
    }
}
