package ru.skillbox.searcher.process.website.searcher.pageResult;

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
import ru.skillbox.searcher.config.SiteConfig;
import ru.skillbox.searcher.model.mapper.PageMapper;
import ru.skillbox.searcher.dto.PageDTO;
import ru.skillbox.searcher.service.search.dto.RankingPageDTO;
import ru.skillbox.searcher.view.Page;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static helpers.SiteUtils.testSite;
import static helpers.SiteUtils.testUrl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@Import({TestPageRepository.class, TestLemmaRepository.class, DataBaseFiller.class})
public class PageResultProcessorImplTest {

    @Autowired
    private PageResultProcessor pageResultProcessor;
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
        Collection<RankingPageDTO> rankingPageDTOS = this.getRankingPageList(valueInput);
        Map<SiteConfig.Site, Collection<RankingPageDTO>> pageDTOMap = new HashMap<>();
        pageDTOMap.put(testSite, rankingPageDTOS);

        //when
        Collection<Page> result = pageResultProcessor.process(pageDTOMap, valueInput);

        //then
        float previousRelevance = Float.MAX_VALUE;
        Set<String> urlSet = rankingPageDTOS.stream()
                .map(rankingPageDTO -> rankingPageDTO.getPageDTO().getUrl())
                .collect(Collectors.toSet());

        for (Page page : result) {
            String uri = page.getUri();
            assertTrue(urlSet.contains(uri));

            float relevance = page.getRelevance();
            assertTrue(relevance <= previousRelevance);
            previousRelevance = relevance;

            String expectedSite = testUrl.toString();
            String actualSite = page.getSite();
            assertEquals(expectedSite, actualSite);

            String expectedSiteName = testUrl.getHost();
            String actualSiteName = page.getSiteName();
            assertEquals(expectedSiteName, actualSiteName);
        }
    }

    private Collection<RankingPageDTO> getRankingPageList(Collection<String> lemmaValues) throws SQLException {
        List<PageDTO> pageDTOList = pageRepository.getByAllLemmaValueInAndPositiveCode(lemmaValues)
                .stream().map(pageMapper::getDTO)
                .collect(Collectors.toList());

        List<RankingPageDTO> result = new ArrayList<>(pageDTOList.size());
        float relevance = 100f;

        for (PageDTO pageDTO : pageDTOList) {
            RankingPageDTO rankingPageDTO = new RankingPageDTO(pageDTO , relevance);
            result.add(rankingPageDTO);
            relevance--;
        }

        return result;
    }


}
