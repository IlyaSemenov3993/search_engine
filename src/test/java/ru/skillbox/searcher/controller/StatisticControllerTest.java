package ru.skillbox.searcher.controller;

import beans.db.TestLemmaRepository;
import helpers.JsonUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.skillbox.searcher.config.AppConfig;
import ru.skillbox.searcher.process.shutdown.ShutdownableHolder;
import ru.skillbox.searcher.service.statistics.response.SiteDetail;
import ru.skillbox.searcher.service.statistics.response.StatisticResponse;
import ru.skillbox.searcher.service.statistics.response.Statistics;
import ru.skillbox.searcher.service.statistics.response.Total;
import ru.skillbox.searcher.model.entity.SiteEntity;
import ru.skillbox.searcher.model.repository.LemmaRepository;
import ru.skillbox.searcher.model.repository.PageRepository;
import ru.skillbox.searcher.model.repository.SiteRepository;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AppConfig.class)
@WebAppConfiguration
@Import(TestLemmaRepository.class)
public class StatisticControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ShutdownableHolder shutdownableHolder;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private LemmaRepository lemmaRepository;

    @Autowired
    private TestLemmaRepository testLemmaRepository;

    private MockMvc mvc;

    @Before
    public void init() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testStartIndexing() throws Exception {
        final String uri = "/statistics";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        StatisticResponse statisticResponse = JsonUtils.mapFromMvc(mvcResult, StatisticResponse.class);
        assertTrue(statisticResponse.isResult());

        Statistics statistics = statisticResponse.getStatistics();
        Total total = statistics.getTotal();

        assertEquals(siteRepository.count(), total.getSites());
        assertEquals(pageRepository.count(), total.getPages());
        assertEquals(testLemmaRepository.getValueDistinct().size(), total.getLemmas());
        assertEquals(shutdownableHolder.isIndexing(), total.isIndex());

        SiteDetail[] detailed = statistics.getDetailed();
        assertEquals(siteRepository.count(), detailed.length);

        for (SiteDetail siteDetail : detailed) {
            this.assertSiteDetail(siteDetail);
        }
    }


    private void assertSiteDetail(SiteDetail siteDetail) {
        Optional<SiteEntity> siteEntityList = siteRepository.getByUrl(siteDetail.getUrl());
        SiteEntity siteEntity = siteEntityList.get();

        assertEquals(siteEntity.getName(), siteDetail.getName());
        assertEquals(siteEntity.getStatus().name(), siteDetail.getStatus());

        Date date = Date.from(Instant.from(siteEntity.getStatusTime().atZone(ZoneId.systemDefault())));
        assertEquals(date.getTime(), siteDetail.getStatusTime());
        assertEquals(siteEntity.getLastError(), siteDetail.getError());

        long expectedPageCount = pageRepository.getCountPageBySiteId(siteEntity.getId());
        assertEquals(expectedPageCount, siteDetail.getPages());

        long expectedLemmasCount = lemmaRepository.getCountDistinctValueBySiteId(siteEntity.getId());
        assertEquals(expectedLemmasCount, siteDetail.getLemmas());
    }
}
