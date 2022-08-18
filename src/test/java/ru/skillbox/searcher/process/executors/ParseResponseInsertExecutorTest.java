package ru.skillbox.searcher.process.executors;

import beans.db.TestSiteRepository;
import helpers.RandomWord;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.skillbox.searcher.config.AppConfig;
import ru.skillbox.searcher.config.SiteConfig;
import ru.skillbox.searcher.model.entity.PageEntity;
import ru.skillbox.searcher.model.entity.SiteEntity;
import ru.skillbox.searcher.model.entity.enums.SiteStatus;
import ru.skillbox.searcher.process.executors.indexing.insertion.ParseResponseInsertExecutor;
import ru.skillbox.searcher.model.repository.LemmaRepository;
import ru.skillbox.searcher.model.repository.PageRepository;
import ru.skillbox.searcher.model.repository.SiteRepository;
import ru.skillbox.searcher.model.mapper.SiteMapper;
import ru.skillbox.searcher.dto.LemmaDTO;
import ru.skillbox.searcher.process.website.parser.ParseResponse;
import ru.skillbox.searcher.process.website.tools.Links;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static helpers.LinksUtils.defineUrlRootFromUrl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static ru.skillbox.searcher.process.website.tools.Links.REQUIRED_PROTOCOL;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@Import(TestSiteRepository.class)
public class ParseResponseInsertExecutorTest {
    private final int ENTITIES_COUNT = 100;
    private final int LEMMAS_COUNT = 20;
    private final Random random = new Random();

    @Autowired
    private ParseResponseInsertExecutor parseResponseInsertExecutor;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private LemmaRepository lemmaRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private TestSiteRepository testSiteRepository;

    @Autowired
    private SiteMapper siteMapper;

    @After
    public void clearDataBase() throws SQLException {
        testSiteRepository.deleteByStatus(SiteStatus.TEST);
    }

    @Test
    public void executeTest1() throws SQLException, MalformedURLException {
        List<ParseResponse> createdList = this.generateParseResponseList();

        SiteEntity siteEntity = this.getSiteDaoFromParseResponse(createdList);
        siteRepository.save(siteEntity);

        parseResponseInsertExecutor.execute(createdList);

        for (ParseResponse parserResponse : createdList) {

            String relativeUrl = parserResponse.getRelativeUrl();
            Collection<PageEntity> pageEntityCollection = pageRepository.getByPath(relativeUrl);
            assert pageEntityCollection.size() == 1;

            PageEntity pageEntity = pageEntityCollection.iterator().next();
            assertEquals(siteEntity.getId(), pageEntity.getSiteId());

            UUID pageId = pageEntity.getId();
            List<LemmaDTO> lemmaDTOList = lemmaRepository.getByPageId(pageId).stream()
                    .map(LemmaDTO::new)
                    .collect(Collectors.toList());
            int expectedIndexSize = parserResponse.getEstimatedLemmas().size();
            int actualIndexSize = lemmaDTOList.size();
            assertEquals(expectedIndexSize, actualIndexSize);

            assertIndexDAOList(lemmaDTOList, parserResponse);
        }
    }


    private List<ParseResponse> generateParseResponseList() throws MalformedURLException {
        URL url = this.generateSite();

        return Stream.generate(() -> {
            try {
                return generateParserResponse(url);
            } catch (MalformedURLException malformedURLException) {
                throw new RuntimeException(malformedURLException);
            }
        })
                .limit(ENTITIES_COUNT)
                .collect(Collectors.toList());
    }

    private URL generateSite() throws MalformedURLException {
        String layout = REQUIRED_PROTOCOL + "://www.test_%s.ru";
        String randomWord = RandomWord.generate(10);
        String url = String.format(layout, randomWord);
        return Links.prepareUrl(url);
    }

    private ParseResponse generateParserResponse(URL url) throws MalformedURLException {
        int responseCode = random.nextInt(600);
        if (responseCode == 0) {
            responseCode++;
        }

        List<String> innerLinks = Stream.generate(() -> random.nextInt())
                .limit(responseCode)
                .map(i -> i + "")
                .collect(Collectors.toList());
        String content = innerLinks.get(random.nextInt(responseCode)) + "'some apost'rof";
        URL newURL = new URL(url, innerLinks.get(random.nextInt(responseCode)));
        Map<String, Integer> estimatedLemmas = generateEstimatedLemmas();

        return ParseResponse.newBuilder()
                .setResponseCode(responseCode)
                .setUrl(newURL)
                .setContent(content)
                .setEstimatedLemmas(estimatedLemmas)
                .build();
    }

    private SiteEntity getSiteDaoFromParseResponse(List<ParseResponse> createdList) throws MalformedURLException {
        URL url = createdList.get(0).getUrl();
        url = defineUrlRootFromUrl(url);
        SiteConfig.Site site = new SiteConfig.Site(url.toString(), url.getHost());

        SiteEntity siteEntity = siteMapper.getEntity(site);
        siteEntity.setStatus(SiteStatus.TEST);
        siteEntity.setId(UUID.randomUUID());
        return siteEntity;
    }

    private void assertIndexDAOList(List<LemmaDTO> lemmaDTOList, ParseResponse parserResponse) throws SQLException {
        for (int i = 0; i < lemmaDTOList.size(); i++) {
            if (i % 10 != 0) {
                continue;
            }

            LemmaDTO lemmaDTO = lemmaDTOList.get(i);
            String lemmaValue = lemmaDTO.getValue();

            Integer lemmaRank = parserResponse
                    .getEstimatedLemmas()
                    .get(lemmaValue);
            assertNotNull(lemmaRank);

            float expected = lemmaRank / 10f;
            float actual = lemmaDTO.getRank();

            assert (expected == actual);
        }
    }

    private Map<String, Integer> generateEstimatedLemmas() {
        Map<String, Integer> result = new HashMap<>();

        for (int i = 0; i < LEMMAS_COUNT; i++) {
            String lemma = random.nextInt() + "";
            int rank = random.nextInt(100);

            result.put(lemma, rank);
        }

        return result;
    }

}
