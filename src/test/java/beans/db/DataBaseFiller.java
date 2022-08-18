package beans.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.skillbox.searcher.model.repository.PageRepository;
import ru.skillbox.searcher.process.executors.indexing.IndexingExecutor;

import static helpers.SiteUtils.testSite;
import static helpers.SiteUtils.testUrl;

@Component
public class DataBaseFiller {

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private IndexingExecutor indexingExecutor;

    public void indexTestSiteIfItNeeds() {
        long pageCount = pageRepository.getCountPageBySiteUrlAndPositiveCode(testUrl.toString());
        if (pageCount == 0) {
            indexingExecutor.execute(testSite);
        }
    }
}
