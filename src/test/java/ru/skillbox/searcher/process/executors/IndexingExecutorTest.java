//package ru.skillbox.searcher.process.executors;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import ru.skillbox.searcher.config.AppConfig;
//import ru.skillbox.searcher.model.entity.SiteEntity;
//import ru.skillbox.searcher.model.entity.enums.SiteStatus;
//import ru.skillbox.searcher.process.executors.indexing.insertion.ParseResponseInsertExecutor;
//import ru.skillbox.searcher.model.repository.crud.SiteRepository;
//import ru.skillbox.searcher.process.executors.indexing.IndexingExecutor;
//import ru.skillbox.searcher.process.shutdown.ShutdownableHolder;
//
//import java.time.LocalDateTime;
//import static helpers.SiteUtils.testSite;
//import static helpers.SiteUtils.testUrl;
//import static org.junit.Assert.*;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = AppConfig.class)
//public class IndexingExecutorTest {
//
//    @Autowired
//    private SiteRepository siteRepository;
//    @Autowired
//    private ParseResponseInsertExecutor parseResponseInsertExecutor;
//    @Autowired
//    private IndexingExecutor indexingExecutor;
//    @Autowired
//    private ShutdownableHolder shutdownableHolder;
//
//
//    @Test
//    public void executeTest1() {
//        LocalDateTime startTime = LocalDateTime.now();
//        boolean indexingSuccess = indexingExecutor.execute(testSite);
//        SiteEntity siteEntity = siteRepository.getByUrl(testUrl).get();
//
//        SiteStatus expectedStatus = indexingSuccess ? SiteStatus.INDEXED : SiteStatus.FAILED;
//        assertEquals(expectedStatus, siteEntity.getStatus());
//        assertTrue(siteEntity.getStatusTime().isAfter(startTime));
//    }
//
//    @Test
//    public void executeTest2() throws InterruptedException {
//        assertFalse(shutdownableHolder.isIndexing(testUrl));
//
//        LocalDateTime startTime = LocalDateTime.now();
//        Thread thread = new Thread(() -> indexingExecutor.execute(testSite));
//        thread.start();
//        Thread.sleep(2000);
//
//        SiteEntity siteEntity = siteRepository.getByUrl(testUrl).get();
//        assertTrue(siteEntity.getStatusTime().isAfter(startTime));
//        assertEquals(SiteStatus.INDEXING, siteEntity.getStatus());
//
//        this.shutdownableHolder.interrupt();
//        thread.join();
//    }
//
//    @Test
//    public void interruptTest1() throws InterruptedException {
//        Thread thread = new Thread(() -> indexingExecutor.execute(testSite));
//        thread.start();
//        Thread.sleep(2000);
//
//        this.shutdownableHolder.interrupt();
//        thread.join();
//
//        SiteEntity siteEntity = siteRepository.getByUrl(testUrl).get();
//        assertEquals(SiteStatus.FAILED, siteEntity.getStatus());
//
//        String expectedError = "Indexing has stoped manually";
//        String actualError = siteEntity.getLastError();
//        assertEquals(expectedError, actualError);
//    }
//
//}
