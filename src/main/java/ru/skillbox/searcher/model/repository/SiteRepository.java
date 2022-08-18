package ru.skillbox.searcher.model.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.searcher.config.SiteConfig;
import ru.skillbox.searcher.model.entity.SiteEntity;
import ru.skillbox.searcher.model.entity.enums.SiteStatus;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SiteRepository extends CrudRepository<SiteEntity, UUID> {

    default Optional<SiteEntity> getByUrl(SiteConfig.Site site) {
        return this.getByUrl(site.getUrl());
    }

    default Optional<SiteEntity> getByUrl(URL url) {
        return this.getByUrl(url.toString());
    }

    default UUID getIdByUrl(URL url) {
        return this.getIdByUrl(url.toString());
    }

    Optional<SiteEntity> getByUrl(String url);

    @Query(value = "SELECT CAST(id AS varchar) FROM site WHERE url = ?1"
            , nativeQuery = true)
    UUID getIdByUrl(String url);

    List<SiteEntity> getByStatus(SiteStatus status);
}
