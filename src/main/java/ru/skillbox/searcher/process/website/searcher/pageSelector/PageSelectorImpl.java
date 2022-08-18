package ru.skillbox.searcher.process.website.searcher.pageSelector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.skillbox.searcher.model.entity.PageEntity;
import ru.skillbox.searcher.model.repository.PageRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

@Component
public class PageSelectorImpl implements PageSelector {

    @Autowired
    private PageRepository pageRepository;

    @Override
    public Collection<PageEntity> selectPages(Collection<String> stringCollection) {
        Collection<PageEntity> result = null;

        for (String lemmaValue : stringCollection) {
            result = this.getPageWhereLemmaValueEquals(lemmaValue, result);

            if (result.isEmpty()) {
                break;
            }
        }

        return result;
    }

    @Override
    public Collection<PageEntity> selectPages(Collection<String> stringCollection, UUID siteId) {
        Collection<PageEntity> result = null;

        for (String lemmaValue : stringCollection) {
            result = this.getPageWhereLemmaValueEqualsAndSiteId(lemmaValue, result, siteId);

            if (result.isEmpty()) {
                break;
            }
        }

        return result;
    }

    private Collection<PageEntity> getPageWhereLemmaValueEquals(String lemmaValue, Collection<PageEntity> pageEntityCollection) {
        if (pageEntityCollection == null) {
            Collection<String> input = Arrays.asList(lemmaValue);
            return pageRepository.getByLemmaValueInAndPositiveCode(input);
        } else {
            return pageRepository.getByLemmaValueInAndPageIn(lemmaValue
                    , pageEntityCollection);
        }
    }

    private Collection<PageEntity> getPageWhereLemmaValueEqualsAndSiteId(String lemmaValue, Collection<PageEntity> pageEntityCollection
            , UUID siteId) {
        if (pageEntityCollection == null) {
            Collection<String> input = Arrays.asList(lemmaValue);
            return pageRepository.getByLemmaValueInAndPositiveCodeAndSiteId(input, siteId);
        } else {
            return pageRepository.getByLemmaValueInAndPageIn(lemmaValue
                    , pageEntityCollection);
        }
    }

}
