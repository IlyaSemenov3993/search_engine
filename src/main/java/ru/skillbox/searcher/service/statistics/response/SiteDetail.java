package ru.skillbox.searcher.service.statistics.response;

import org.springframework.beans.factory.annotation.Autowired;
import ru.skillbox.searcher.model.repository.LemmaRepository;
import ru.skillbox.searcher.model.repository.PageRepository;
import ru.skillbox.searcher.dto.SiteDTO;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

public class SiteDetail {

    private String url;
    private String name;
    private String status;
    private long statusTime;
    private String error;
    private UUID siteId;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private LemmaRepository lemmaRepository;
    private int pages;
    private int lemmas;

    public SiteDetail(SiteDTO siteDTO) {
        this.url = siteDTO.getUrl();
        this.name = siteDTO.getName();
        this.status = siteDTO.getStatus().name();
        this.statusTime = this.getStatusTime(siteDTO.getStatusTime());
        this.error = siteDTO.getLastError();
        this.siteId = siteDTO.getId();
    }

    public SiteDetail() {
    }

    @PostConstruct
    private void fillAttributes() {
        this.pages = (int) pageRepository.getCountPageBySiteId(siteId);
        this.lemmas = (int) lemmaRepository.getCountDistinctValueBySiteId(siteId);
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public long getStatusTime() {
        return statusTime;
    }

    public String getError() {
        return error;
    }

    public int getPages() {
        return pages;
    }

    public int getLemmas() {
        return lemmas;
    }

    private long getStatusTime(LocalDateTime localDateTime) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        Date date = Date.from(Instant.from(zonedDateTime));
        return date.getTime();
    }
}
