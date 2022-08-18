package ru.skillbox.searcher.controller;

import beans.db.DataBaseFiller;
import beans.db.TestLemmaRepository;
import beans.db.TestSiteRepository;
import beans.utils.UrlUtils;
import helpers.JsonUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
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
import ru.skillbox.searcher.exception.searchProcess.*;
import ru.skillbox.searcher.process.executors.indexing.IndexingExecutor;
import ru.skillbox.searcher.process.shutdown.ShutdownableHolder;
import ru.skillbox.searcher.process.website.tools.Links;
import ru.skillbox.searcher.service.search.SearchServiceImpl;
import ru.skillbox.searcher.service.search.response.SearchResponse;
import ru.skillbox.searcher.view.Page;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static helpers.SiteUtils.testSite;
import static helpers.SiteUtils.testUrl;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@WebAppConfiguration
@Import({TestLemmaRepository.class, TestSiteRepository.class, UrlUtils.class, DataBaseFiller.class})
public class SearchingControllerTest {

    private static Collection<String> testLemmas;

    @Value("${search.default.limit}")
    private int DEFAULT_LIMIT;
    @Autowired
    private SiteConfig siteConfig;
    @Autowired
    private UrlUtils urlUtils;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private SearchServiceImpl searchService;
    @Autowired
    private TestLemmaRepository testLemmaRepository;
    @Autowired
    private TestSiteRepository testSiteRepository;
    @Autowired
    private IndexingExecutor indexingExecutor;
    @Autowired
    private DataBaseFiller dataBaseFiller;
    @Autowired
    private ShutdownableHolder shutdownableHolder;
    @Autowired
    private IndexingController indexingController;
    private MockMvc mvc;


    @Before
    public void init() throws MalformedURLException, SQLException {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.initMocks(this);

        siteConfig.addSite(testUrl.toString());
        testLemmas = getTestLemmas();
    }


    @Test
    public void testSearchEmptyRequest() throws Exception {
        String query = "";
        String site = "someSite";
        int offset = 14;
        int limit = 88;
        String uri = this.urlUtils.getTestUri(query, site, offset, limit);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        SearchResponse actualResponse = JsonUtils.mapFromMvc(mvcResult, SearchResponse.class);
        assertFalse(actualResponse.getResult());
        assertEquals(RequestMissingException.ERROR_MESSAGE, actualResponse.getError());
    }

    @Test
    public void testSearchNoSiteFound() throws Exception {
        String query = "someQuery";
        String site = "https://youtube.com";
        int offset = 14;
        int limit = 88;
        String uri = this.urlUtils.getTestUri(query, site, offset, limit);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        SearchResponse actualResponse = JsonUtils.mapFromMvc(mvcResult, SearchResponse.class);
        assertFalse(actualResponse.getResult());
        assertEquals(SiteNotDefinedException.ERROR_MESSAGE, actualResponse.getError());
    }

    @Test
    public void testSearchNoPageFound() throws Exception {
        String site = testSite.getUrl().toString();
        Collection<String> lemmaCollection = new LinkedList<>(getTestLemmas());
        lemmaCollection.addAll(Arrays.asList("духи, пуля, вентилятор, корабль, диалектика, массаж"));
        String query = lemmaCollection.stream()
                .collect(Collectors.joining(","));
        int offset = 14;
        int limit = 88;
        String uri = this.urlUtils.getTestUri(query, site, offset, limit);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        SearchResponse actualResponse = JsonUtils.mapFromMvc(mvcResult, SearchResponse.class);
        assertFalse(actualResponse.getResult());
        assertEquals(NoSuitablePageException.ERROR_MESSAGE, actualResponse.getError());
    }

    @Test
    public void testSearchIsIndexing() throws Exception {
        String query = "someQuery";
        String site = "someSite";
        int offset = 14;
        int limit = 88;
        String uri = this.urlUtils.getTestUri(query, site, offset, limit);

        Thread thread = new Thread(() -> indexingExecutor.execute(testSite));
        thread.start();
        Thread.sleep(500);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();
        shutdownableHolder.interrupt();
        thread.join();

        SearchResponse actualResponse = JsonUtils.mapFromMvc(mvcResult, SearchResponse.class);
        assertFalse(actualResponse.getResult());
        assertEquals(IsAlreadyIndexingException.ERROR_MESSAGE, actualResponse.getError());
    }

    @Test
    public void testSearchBySingleSiteDefaultLimit() throws Exception {
        String site = testSite.getUrl().toString();
        String query = this.getTestQuery();
        int offset = 14;
        int limit = 88;
        String uri = this.urlUtils.getTestUri(query, site, offset, limit);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        SearchResponse actualResponse = JsonUtils.mapFromMvc(mvcResult, SearchResponse.class);
        assertTrue(actualResponse.getResult());
        assertNull(actualResponse.getError());

        Map<String, String> inputParams = this.urlUtils.getSearchInputParams(query, site, DEFAULT_LIMIT);
        Collection<Page> expected = searchService.search(inputParams).getData();
        Collection<Page> actual = actualResponse.getData();
        assertEquals(new ArrayList<>(expected), new ArrayList(actual));
        assertTrue(actual.size() <= DEFAULT_LIMIT);
    }

    @Test
    public void testSearchBySingleSiteSelectedLimit() throws Exception {
        String site = testSite.getUrl().toString();
        String query = this.getTestQuery();
        int offset = 14;
        int limit = 8;
        String uri = this.urlUtils.getTestUri(query, site, offset, limit);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        SearchResponse actualResponse = JsonUtils.mapFromMvc(mvcResult, SearchResponse.class);
        assertTrue(actualResponse.getResult());
        assertNull(actualResponse.getError());
        assertTrue(actualResponse.getData().size() <= limit);
    }

    @Test
    public void testSearchBySingleSiteWrongSelectedLimit1() throws Exception {
        String site = testSite.getUrl().toString();
        String query = this.getTestQuery();
        int offset = 14;
        int limit = 0;
        String uri = this.urlUtils.getTestUri(query, site, offset, limit);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        SearchResponse actualResponse = JsonUtils.mapFromMvc(mvcResult, SearchResponse.class);
        assertTrue(actualResponse.getResult());
        assertNull(actualResponse.getError());

        Map<String, String> inputParams = this.urlUtils.getSearchInputParams(query, site, DEFAULT_LIMIT);
        Collection<Page> expected = searchService.search(inputParams).getData();
        Collection<Page> actual = actualResponse.getData();
        assertEquals(new ArrayList<>(expected), new ArrayList(actual));
        assertTrue(actual.size() <= DEFAULT_LIMIT);
    }

    @Test
    public void testSearchBySingleSiteWrongSelectedLimit2() throws Exception {
        String site = testSite.getUrl().toString();
        String query = this.getTestQuery();
        int offset = 14;
        int limit = 88;
        String uri = this.urlUtils.getTestUri(query, site, offset, limit);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        SearchResponse actualResponse = JsonUtils.mapFromMvc(mvcResult, SearchResponse.class);
        assertTrue(actualResponse.getResult());
        assertNull(actualResponse.getError());

        Map<String, String> inputParams = this.urlUtils.getSearchInputParams(query, site, DEFAULT_LIMIT);
        Collection<Page> expected = searchService.search(inputParams).getData();
        Collection<Page> actual = actualResponse.getData();
        assertEquals(new ArrayList<>(expected), new ArrayList(actual));
        assertTrue(actual.size() <= DEFAULT_LIMIT);
    }

    @Test
    public void testSearchAllSitesRequest() throws Exception {
        String query = this.getLemmaExistingInAllSites();
        int offset = 14;
        int limit = 88;
        String uri = this.urlUtils.getTestUri(query, offset, limit);

        assertTrue(query.length() > 0);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        SearchResponse actualResponse = JsonUtils.mapFromMvc(mvcResult, SearchResponse.class);
        assertTrue(actualResponse.getResult());

        Set<SiteConfig.Site> siteSet = new HashSet<>();
        for (Page page : actualResponse.getData()) {
            SiteConfig.Site site = siteConfig.getSiteByStringUrl(page.getSite());
            assertNotNull(site);
            siteSet.add(site);
        }
        assertTrue(siteSet.size() > 1);
    }


    private Collection<String> getTestLemmas() throws SQLException {
        if (testLemmas == null) {
            this.dataBaseFiller.indexTestSiteIfItNeeds();
            testLemmas = testLemmaRepository.getTestValuesBySiteUrl(testUrl);
        }

        return testLemmas;
    }

    private String getTestQuery() throws SQLException {
        return this.getTestLemmas().stream()
                .collect(Collectors.joining(","));
    }

    private String getLemmaExistingInAllSites() throws SQLException {
        long siteCount = testSiteRepository.countSiteHavingLemmas();
        if (siteCount < 2) {
            indexingController.startIndexing();
        }
        return testLemmaRepository.getLemmaExistingInAllSites();
    }


}
