package ru.skillbox.searcher.process.website.searcher.pageSelector;

import beans.db.TestLemmaRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.skillbox.searcher.config.AppConfig;
import ru.skillbox.searcher.model.entity.PageEntity;
import ru.skillbox.searcher.model.entity.SiteEntity;
import ru.skillbox.searcher.model.repository.LemmaRepository;
import ru.skillbox.searcher.model.repository.PageRepository;
import ru.skillbox.searcher.model.repository.SiteRepository;
import ru.skillbox.searcher.process.executors.indexing.IndexingExecutor;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import static helpers.SiteUtils.testUrl;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@Import(TestLemmaRepository.class)
public class PageSelectorImplTest {

    @Autowired
    private PageSelector pageSelector;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private LemmaRepository lemmaRepository;

    @Autowired
    private TestLemmaRepository testLemmaRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private IndexingExecutor indexingExecutor;


    @Test
    public void selectPagesTest1() throws SQLException {
        Collection<String> stringInput = testLemmaRepository.getTestValues();
        Collection<PageEntity> result = pageSelector.selectPages(stringInput);

        int expected = stringInput.size();
        for (PageEntity pageEntity : result) {
            UUID pageId = pageEntity.getId();
            int actual = lemmaRepository.getByPageIdAndValueIn(pageId, stringInput).size();
            assertEquals(expected, actual);
        }

        long actual = pageRepository.getByLemmaValueInAndPositiveCode(stringInput).size();
        expected = result.size();
        assertEquals(expected, actual);
    }

    @Test
    public void selectPagesTest2() throws SQLException {
        SiteEntity siteEntity = siteRepository.getByUrl(testUrl).get();
        UUID siteId = siteEntity.getId();
        Collection<String> stringInput = testLemmaRepository.getTestValuesBySiteUrl(testUrl);
        Collection<PageEntity> result = pageSelector.selectPages(stringInput, siteId);

        int expected = stringInput.size();
        for (PageEntity pageEntity : result) {
            assertEquals(siteId, pageEntity.getSiteId());

            UUID pageId = pageEntity.getId();
            int actual = lemmaRepository.getByPageIdAndValueIn(pageId, stringInput).size();
            assertEquals(expected, actual);
        }

        long actual = pageRepository.getByLemmaValueInAndPositiveCodeAndSiteId(stringInput, siteId)
                .size();
        expected = result.size();
        assertEquals(expected, actual);
    }

    @Test
    public void processNegativeTest() {
        Collection<String> input = Arrays.asList("edasfcads");
        boolean isResultEmpty = pageSelector.selectPages(input).size() == 0;
        assert (isResultEmpty);
    }

}
