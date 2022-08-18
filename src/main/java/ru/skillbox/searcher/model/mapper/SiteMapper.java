package ru.skillbox.searcher.model.mapper;

import org.springframework.stereotype.Component;
import ru.skillbox.searcher.config.SiteConfig;
import ru.skillbox.searcher.model.entity.SiteEntity;
import ru.skillbox.searcher.dto.SiteDTO;

import java.time.LocalDateTime;

@Component
public class SiteMapper {

    public SiteEntity getEntity(SiteConfig.Site site) {
        SiteEntity siteEntity = new SiteEntity();
        siteEntity.setUrl(site.getUrl().toString());
        siteEntity.setName(site.getName());
        siteEntity.setStatusTime(LocalDateTime.now());
        return siteEntity;
    }

    public SiteDTO getDTO(SiteEntity siteEntity) {
        SiteDTO siteDTO = new SiteDTO();
        siteDTO.setId(siteEntity.getId());
        siteDTO.setStatus(siteEntity.getStatus());
        siteDTO.setStatusTime(siteEntity.getStatusTime());
        siteDTO.setName(siteEntity.getName());
        siteDTO.setLastError(siteEntity.getLastError());
        siteDTO.setUrl(siteEntity.getUrl());
        return siteDTO;
    }
}
