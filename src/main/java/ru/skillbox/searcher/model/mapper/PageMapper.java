package ru.skillbox.searcher.model.mapper;

import org.springframework.stereotype.Component;
import ru.skillbox.searcher.model.entity.PageEntity;
import ru.skillbox.searcher.dto.PageDTO;
import ru.skillbox.searcher.process.website.parser.ParseResponse;

import java.util.UUID;

@Component
public class PageMapper {


    public PageEntity getEntity(ParseResponse parseResponse) {
        PageEntity pageEntity = new PageEntity();
        pageEntity.setId(UUID.randomUUID());
        pageEntity.setPath(parseResponse.getRelativeUrl());
        pageEntity.setCode(parseResponse.getResponseCode());
        pageEntity.setContent(parseResponse.getContent());
        return pageEntity;
    }

    public PageDTO getDTO(PageEntity pageEntity) {
        PageDTO pageDTO = new PageDTO();
        pageDTO.setId(pageEntity.getId());
        pageDTO.setSiteId(pageEntity.getSiteId());
        pageDTO.setUrl(pageEntity.getPath());
        pageDTO.setContent(pageEntity.getContent());
        pageDTO.setResponseCode(pageEntity.getCode());
        return pageDTO;
    }
}
