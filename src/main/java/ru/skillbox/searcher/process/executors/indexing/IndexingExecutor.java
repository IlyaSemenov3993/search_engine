package ru.skillbox.searcher.process.executors.indexing;

import ru.skillbox.searcher.config.SiteConfig;

public interface IndexingExecutor {

    boolean execute(SiteConfig.Site site);

}
