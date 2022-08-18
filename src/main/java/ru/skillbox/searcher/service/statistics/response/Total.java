package ru.skillbox.searcher.service.statistics.response;

import org.springframework.beans.factory.annotation.Autowired;
import ru.skillbox.searcher.model.entity.enums.SiteStatus;
import ru.skillbox.searcher.model.repository.LemmaRepository;
import ru.skillbox.searcher.model.repository.PageRepository;
import ru.skillbox.searcher.model.repository.SiteRepository;

import javax.annotation.PostConstruct;

public class Total {

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private LemmaRepository lemmaRepository;

    private int sites;
    private int pages;
    private int lemmas;
    private boolean index;

    @PostConstruct
    private void fillAttributes(){
        this.sites = (int) siteRepository.count();
        this.pages = (int) pageRepository.count();
        this.lemmas = (int) lemmaRepository.getCountDistinctValue();

        this.index = !siteRepository.getByStatus(SiteStatus.INDEXING).isEmpty();
    }

    public int getSites() {
        return sites;
    }

    public int getPages() {
        return pages;
    }

    public int getLemmas() {
        return lemmas;
    }

    public boolean isIndex() {
        return index;
    }


}
