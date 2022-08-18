package ru.skillbox.searcher.process.shutdown;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import ru.skillbox.searcher.config.SiteConfig;
import ru.skillbox.searcher.exception.manuallyInterruption.IllegalStateOfShotdownableList;
import ru.skillbox.searcher.logs.LogUtils;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ShutdownableHolder {
    private static final Logger logger = LogManager.getLogger("AppFile");

    private final static int MAX_SHUTDOWNABLE_COUNT = 2;
    private final Map<SiteConfig.Site, List<Shutdownable>> siteIndexingMap;
    private final Map<URL, List<Shutdownable>> pageIndexingMap;
    private volatile boolean isInterrupting;

    public ShutdownableHolder() {
        this.siteIndexingMap = new ConcurrentHashMap<>();
        this.pageIndexingMap = new ConcurrentHashMap<>();
    }

    public synchronized void interrupt() {
        logger.info("Interrupting started");
        this.isInterrupting = true;

        this.clearSiteMap();
        this.clearPageMap();

        logger.info("Interrupting finished");
        this.isInterrupting = false;
        notifyAll();
    }

    public boolean isIndexing() {
        return !siteIndexingMap.isEmpty() ||
                !pageIndexingMap.isEmpty();
    }

    public boolean isIndexing(SiteConfig.Site site) {
        return this.siteIndexingMap.containsKey(site);
    }

    public boolean isIndexing(URL page) {
        return this.pageIndexingMap.containsKey(page);
    }


    public boolean addSite(SiteConfig.Site site) {
        if (this.isIndexing(site)) {
            return false;
        }
        List<Shutdownable> shutdownableList = new ArrayList<>(MAX_SHUTDOWNABLE_COUNT);
        this.siteIndexingMap.put(site, shutdownableList);
        return true;
    }

    public boolean addPage(URL page) {
        if (this.isIndexing(page)) {
            return false;
        }
        List<Shutdownable> shutdownableList = new ArrayList<>(MAX_SHUTDOWNABLE_COUNT);
        this.pageIndexingMap.put(page, shutdownableList);
        return true;
    }

    public void addShutdownable(SiteConfig.Site site, Shutdownable shutdownable) {
        this.waitWhileIsInterrupting();

        List<Shutdownable> shutdownableList = this.siteIndexingMap.get(site);

        if (shutdownableList == null ||
                (shutdownableList.isEmpty() && !shutdownable.isStartShutdownable())) {
            throw new IllegalStateOfShotdownableList();
        } else if (shutdownableList.size() >= MAX_SHUTDOWNABLE_COUNT) {
            logger.error("Wrong count of shutdownable. Must be not more than {} before adding new one", MAX_SHUTDOWNABLE_COUNT - 1);
            throw new IllegalStateException();
        }

        shutdownableList.add(shutdownable);
    }

    public void addShutdownable(URL page, Shutdownable shutdownable) {
        this.waitWhileIsInterrupting();

        List<Shutdownable> shutdownableList = this.pageIndexingMap.get(page);

        if (shutdownableList == null && shutdownable.isStartShutdownable()) {
            logger.error("ShutdownableList is null but with existing page key", MAX_SHUTDOWNABLE_COUNT - 1);
            throw new IllegalStateException();
        } else if (!shutdownable.isStartShutdownable() &&
                (shutdownableList == null || shutdownableList.isEmpty())) {
            throw new IllegalStateOfShotdownableList();
        } else if (shutdownableList.size() >= MAX_SHUTDOWNABLE_COUNT) {
            logger.error("Wrong count of shutdownable. Must be not more than {} before adding new one", MAX_SHUTDOWNABLE_COUNT - 1);
            throw new IllegalStateException();
        }

        shutdownableList.add(shutdownable);
    }

    public void removeSite(SiteConfig.Site site) {
        if (this.siteIndexingMap.remove(site) != null) {
            logger.info("{} is removed from {}", site.getUrl(), this.getClass().getName());
        }
    }

    public void removePage(URL page) {
        if (this.pageIndexingMap.remove(page) != null) {
            logger.info("{} is removed from {}", page, this.getClass().getName());
        }
    }

    private void clearSiteMap() {
        for (SiteConfig.Site site : siteIndexingMap.keySet()) {

            Iterator<Shutdownable> iterator = siteIndexingMap.get(site).iterator();
            while (iterator.hasNext()) {
                Shutdownable shutdownable = iterator.next();
                shutdownable.shutdown();
                iterator.remove();
            }
            siteIndexingMap.remove(site);
        }
    }

    private void clearPageMap() {
        for (URL page : pageIndexingMap.keySet()) {

            Iterator<Shutdownable> iterator = pageIndexingMap.get(page).iterator();
            while (iterator.hasNext()) {
                Shutdownable shutdownable = iterator.next();
                shutdownable.shutdown();
                iterator.remove();
            }
            pageIndexingMap.remove(page);
        }
    }

    private synchronized void waitWhileIsInterrupting() {
        if (isInterrupting) {
            try {
                wait();
            } catch (InterruptedException e) {
                logger.error(LogUtils.getStackTrace(e));
            }
        }
    }


}
