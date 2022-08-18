//package ru.skillbox.searcher.process.website.searcher.lemmaScanner;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import ru.skillbox.searcher.config.AppConfig;
//import ru.skillbox.searcher.model.repository.crud.LemmaRepository;
//
//import java.sql.SQLException;
//import java.util.Collection;
//import java.util.Set;
//
//import static org.junit.Assert.assertTrue;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = AppConfig.class)
//public class MostPopularLemmasTest {
//
//    @Autowired
//    private LemmaRepository lemmaRepository;
//
//    @Autowired
//    private MostPopularLemmas mostPopularLemmas;
//
//    @Test
//    public void getMostPopularLemmasTest() throws SQLException {
//        Set<String> mostPopularLemma = mostPopularLemmas.getMostPopularLemmas();
//
//        long lemmaCount = lemmaRepository.count();
//        if (lemmaCount == 0) {
//            assert (mostPopularLemma.isEmpty());
//            return;
//        }
//
//        Collection<String> morePopularLemmas = lemmaRepository.getMorePopularLemmas(mostPopularLemma);
//        assertTrue(morePopularLemmas.isEmpty());
//    }
//}
