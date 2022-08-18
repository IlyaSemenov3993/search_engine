package ru.skillbox.searcher.process.website.searcher.pageRanking;

import beans.db.DataBaseFiller;
import beans.db.TestLemmaRepository;
import beans.db.TestPageRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.skillbox.searcher.config.AppConfig;
import ru.skillbox.searcher.model.mapper.PageMapper;
import ru.skillbox.searcher.dto.PageDTO;
import ru.skillbox.searcher.process.website.searcher.rankingProcess.PageRankingProcessor;
import ru.skillbox.searcher.service.search.dto.RankingPageDTO;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@Import({TestPageRepository.class, TestLemmaRepository.class, DataBaseFiller.class})
public class PageRankingProcessorImplTest {

    @Autowired
    private PageRankingProcessor pageRankingProcessor;
    @Autowired
    private TestPageRepository pageRepository;
    @Autowired
    private TestLemmaRepository lemmaRepository;
    @Autowired
    private PageMapper pageMapper;
    @Autowired
    private DataBaseFiller dataBaseFiller;

    @Before
    public void init() {
        this.dataBaseFiller.indexTestSiteIfItNeeds();
    }

    @Test
    public void processTest() throws SQLException {
        //given
        Collection<String> valueInput = lemmaRepository.getTestValues();
        List<PageDTO> pageDTOList = pageRepository.getByAllLemmaValueInAndPositiveCode(valueInput)
                .stream().map(pageMapper::getDTO)
                .collect(Collectors.toList());

        //when
        Collection<RankingPageDTO> result = pageRankingProcessor.process(pageDTOList, valueInput);

        //then
        float previousRelevance = Float.MAX_VALUE;
        for (RankingPageDTO page : result) {
            float relevance = page.getRelevance();
            assertTrue(relevance <= previousRelevance);
            previousRelevance = relevance;
        }
    }


}
