package ru.skillbox.searcher.process.website.searcher.pageSelector;

import ru.skillbox.searcher.model.entity.PageEntity;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public interface PageSelector {

    Collection<PageEntity> selectPages(Collection<String> stringCollection);

    Collection<PageEntity> selectPages(Collection<String> stringCollection, UUID siteId);
}
