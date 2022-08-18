package ru.skillbox.searcher.view;

import beans.db.DataBaseFiller;
import beans.db.TestLemmaRepository;
import beans.db.TestPageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.skillbox.searcher.config.AppConfig;
import ru.skillbox.searcher.dto.PageDTO;
import ru.skillbox.searcher.model.entity.PageEntity;
import ru.skillbox.searcher.model.mapper.PageMapper;
import ru.skillbox.searcher.process.website.searcher.pageResult.PageResultProcessorImpl;
import ru.skillbox.searcher.service.search.dto.RankingPageDTO;

import java.sql.SQLException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static helpers.SiteUtils.testSite;
import static helpers.SiteUtils.testUrl;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@Import({TestPageRepository.class, DataBaseFiller.class, TestLemmaRepository.class})
public class PageTest {

    @Autowired
    private TestPageRepository testPageRepository;

    @Autowired
    private TestLemmaRepository testLemmaRepository;

    @Autowired
    private PageMapper pageMapper;

    @Autowired
    private DataBaseFiller dataBaseFiller;

    @Autowired
    private PageResultProcessorImpl pageResultProcessor;


    @Test
    public void test() throws SQLException {
        //given
        this.dataBaseFiller.indexTestSiteIfItNeeds();
        PageEntity pageEntityInput = testPageRepository.getRandom().get();

        Collection<String> stringInput = testLemmaRepository.getValueByPageId(pageEntityInput.getId());
        PageDTO pageDTO = pageMapper.getDTO(pageEntityInput);
        float expectedRelevance = 1f;
        RankingPageDTO rankingPageDTO = new RankingPageDTO(pageDTO, expectedRelevance);

        //when
        Page resultPage = new Page(testSite, rankingPageDTO, stringInput);

        //then
        String expectedUrl = pageEntityInput.getPath();
        String actualUrl = resultPage.getUri();
        assertEquals(expectedUrl, actualUrl);

        float actualRelevance = resultPage.getRelevance();
        assertEquals(expectedRelevance, actualRelevance , 0);

        String expectedTitle = this.getTitle(pageEntityInput);
        String actualTitle = resultPage.getTitle();
        assertEquals(expectedTitle, actualTitle);

        String expectedSite = testUrl.toString();
        String actualSite = resultPage.getSite();
        assertEquals(expectedSite, actualSite);

        String expectedSiteName = testUrl.getHost();
        String actualSiteName = resultPage.getSiteName();
        assertEquals(expectedSiteName, actualSiteName);
    }


    private String getTitle(PageEntity pageEntity) {
        String layout = "<title>(.*)</title>";
        Pattern pattern = Pattern.compile(layout);
        Matcher matcher = pattern.matcher(pageEntity.getContent());

        if (matcher.find()) {
            return matcher.group(1)
                    .replace("&quot;", "\"")
                    .replace("  ", " ");
        } else {
            return "";
        }
    }

}
