package ru.skillbox.searcher.controller;

import beans.db.DataBaseFiller;
import beans.db.TestLemmaRepository;
import beans.db.TestPageRepository;
import beans.db.TestSiteRepository;
import beans.utils.UrlUtils;
import helpers.JsonUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.skillbox.searcher.config.AppConfig;
import ru.skillbox.searcher.config.SiteConfig;
import ru.skillbox.searcher.controller.indexing.IndexingController;
import ru.skillbox.searcher.model.entity.PageEntity;
import ru.skillbox.searcher.model.repository.LemmaRepository;
import ru.skillbox.searcher.model.repository.PageRepository;
import ru.skillbox.searcher.process.executors.indexing.IndexingExecutor;
import ru.skillbox.searcher.process.shutdown.ShutdownableHolder;
import ru.skillbox.searcher.service.indexing.response.IndexingResponse;
import ru.skillbox.searcher.model.entity.SiteEntity;
import ru.skillbox.searcher.model.entity.enums.SiteStatus;
import ru.skillbox.searcher.model.repository.SiteRepository;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static helpers.SiteUtils.testSite;
import static helpers.SiteUtils.testUrl;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@WebAppConfiguration
@Import({TestSiteRepository.class, TestPageRepository.class, TestLemmaRepository.class, DataBaseFiller.class, UrlUtils.class})
public class IndexingControllerTest {

    @Autowired
    private ShutdownableHolder shutdownableHolder;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private IndexingController indexingController;
    @Autowired
    private IndexingExecutor indexingExecutor;
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private TestSiteRepository testSiteRepository;
    @Autowired
    private TestPageRepository testPageRepository;
    @Autowired
    private TestLemmaRepository testLemmaRepository;
    @Autowired
    private LemmaRepository lemmaRepository;
    @Autowired
    private SiteConfig siteConfig;
    @Autowired
    private DataBaseFiller dataBaseFiller;
    @Autowired
    private UrlUtils urlUtils;
    private MockMvc mvc;

    @Before
    public void init() throws MalformedURLException {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.initMocks(this);

        siteConfig.addSite(testUrl.toString());
    }


    @Test
    public void testStartIndexing() throws Exception {
        final String uri = "/startIndexing";
        assertFalse(shutdownableHolder.isIndexing());

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        IndexingResponse actualResponse = JsonUtils.mapFromMvc(mvcResult, IndexingResponse.class);
        assertTrue(actualResponse.getResult());
        assertFalse(shutdownableHolder.isIndexing());
    }

    @Test
    public void testStopIndexing1() throws Exception {
        final String uri = "/stopIndexing";
        assertFalse(shutdownableHolder.isIndexing());

        Map<String, String> params = urlUtils.getIndexInputParams(testUrl.toString());
        Thread indexingThread = new Thread(() -> indexingController.indexSite(params));
        indexingThread.start();
        Thread.sleep(500);

        assertTrue(shutdownableHolder.isIndexing(testSite));
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();
        indexingThread.join();

        IndexingResponse actualResponse = JsonUtils.mapFromMvc(mvcResult, IndexingResponse.class);
        assertTrue(actualResponse.getResult());
        assertFalse(shutdownableHolder.isIndexing());

        SiteStatus actualStatus = this.testSiteRepository.getStatusByUrl(testUrl);
        assertEquals(SiteStatus.FAILED, actualStatus);
    }

    @Test
    public void testStopIndexing2() throws Exception {
        final String uri = "/stopIndexing";
        boolean isIndexingAlreadyStarted = shutdownableHolder.isIndexing();
        assertFalse(isIndexingAlreadyStarted);

        Thread indexingThread = new Thread(() -> indexingController.startIndexing());
        indexingThread.start();
        Thread.sleep(500);

        assertTrue(shutdownableHolder.isIndexing());
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        IndexingResponse actualResponse = JsonUtils.mapFromMvc(mvcResult, IndexingResponse.class);
        assertTrue(actualResponse.getResult());

        this.shutdownableHolder.interrupt();
        indexingThread.join();
        assertFalse(shutdownableHolder.isIndexing());

        for (SiteConfig.Site site : siteConfig.getList()) {
            SiteStatus actualStatus = this.testSiteRepository.getStatusByUrl(site.getUrl());
            assertEquals(SiteStatus.FAILED, actualStatus);
        }
    }

    @Test
    public void testStopIndexingNegative() throws Exception {
        final String uri = "/stopIndexing";
        assertFalse(shutdownableHolder.isIndexing());

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        IndexingResponse actualResponse = JsonUtils.mapFromMvc(mvcResult, IndexingResponse.class);
        assertFalse(actualResponse.getResult());
        assertEquals("Индексация не запущена", actualResponse.getError());
    }

    @Test
    public void testIndexPage() throws Exception {
        this.dataBaseFiller.indexTestSiteIfItNeeds();
        PageEntity oldPage = testPageRepository.getRandomBySiteUrl(testUrl);

        String oldPagePath = oldPage.getPath();
        UUID oldPageId = oldPage.getId();

        int oldLemmaCount = (int) lemmaRepository.countByPageId(oldPageId);
        int countToDelete = oldLemmaCount / 2 + 1;
        int remainedCount = oldLemmaCount - countToDelete;
        testLemmaRepository.deleteRowsByPageId(countToDelete, oldPageId);

        String link = testSite.getUrl() + oldPagePath;
        String url = urlUtils.getIndexPageUrl(link);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(url)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        IndexingResponse actualResponse = JsonUtils.mapFromMvc(mvcResult, IndexingResponse.class);
        assertTrue(actualResponse.getResult());
        assertNull(actualResponse.getError());

        PageEntity newPage = pageRepository.getByPath(oldPagePath).iterator().next();
        assertNotEquals(oldPage, newPage.getId());
        assertFalse(pageRepository.findById(oldPageId).isPresent());
        assertTrue(lemmaRepository.countByPageId(oldPageId) == 0);

        long actualLemmaCount = lemmaRepository.countByPageId(newPage.getId());
        assertTrue(remainedCount < actualLemmaCount);
    }

    @Test
    public void testStopIndexPage() throws Exception {
        this.dataBaseFiller.indexTestSiteIfItNeeds();

        PageEntity oldPage = testPageRepository.getRandomBySiteUrl(testUrl);
        String oldPagePath = oldPage.getPath();

        String link = testSite.getUrl() + oldPagePath;
        Map<String, String> params = urlUtils.getIndexInputParams(link);

        Thread thread = new Thread(() -> indexingController.indexPage(params));
        thread.start();
        Thread.sleep(500);

        assertTrue(shutdownableHolder.isIndexing(new URL(link)));
        final String stopUri = "/stopIndexing";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(stopUri)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        IndexingResponse actualResponse = JsonUtils.mapFromMvc(mvcResult, IndexingResponse.class);
        assertTrue(actualResponse.getResult());
        assertNull(actualResponse.getError());
        thread.join();
    }

    @Test
    public void testIndexPageHasNoSite() throws Exception {
        final String wrongUri = "https://www.translate.google.com";
        String url = this.urlUtils.getIndexPageUrl(wrongUri);

        assertFalse(this.shutdownableHolder.isIndexing());
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(url)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        IndexingResponse actualResponse = JsonUtils.mapFromMvc(mvcResult, IndexingResponse.class);
        assertFalse(actualResponse.getResult());
        assertEquals("Данная страница находится за пределами сайтов, указанных в конфигурационном файле"
                , actualResponse.getError());
    }

    @Test
    public void testIndexPageWhenSiteIsIndexing() throws Exception {
        this.dataBaseFiller.indexTestSiteIfItNeeds();
        String pageUrl = testPageRepository.getRandomBySiteUrl(testUrl).getPath();
        String uri = this.urlUtils.getIndexPageUrl(testUrl + pageUrl);

        Thread thread = new Thread(() -> indexingExecutor.execute(testSite));
        thread.start();
        Thread.sleep(2000);

        assertTrue(shutdownableHolder.isIndexing());
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        this.shutdownableHolder.interrupt();
        thread.join();

        IndexingResponse actualResponse = JsonUtils.mapFromMvc(mvcResult, IndexingResponse.class);
        assertFalse(actualResponse.getResult());
        assertEquals("Индексация сайта уже запущена", actualResponse.getError());
    }

    @Test
    public void testIndexSite() throws Exception {
        String uri = this.urlUtils.getIndexSiteUrl(testUrl.toString());

        LocalDateTime startTime = LocalDateTime.now();
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        IndexingResponse actualResponse = JsonUtils.mapFromMvc(mvcResult, IndexingResponse.class);
        assertTrue(actualResponse.getResult());
        assertNull(actualResponse.getError());

        SiteEntity siteEntity = siteRepository.getByUrl(testUrl).get();
        LocalDateTime lastModifiedTime = siteEntity.getStatusTime();
        assertTrue(lastModifiedTime.isAfter(startTime));

        String page = "https://www.mr-right.ru";
        uri = this.urlUtils.getIndexSiteUrl(page);
        mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        actualResponse = JsonUtils.mapFromMvc(mvcResult, IndexingResponse.class);
        assertTrue(actualResponse.getResult());
        assertNull(actualResponse.getError());
    }

}
