package ru.skillbox.searcher.model.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.searcher.model.entity.PageEntity;

import java.util.Collection;
import java.util.UUID;

public interface PageRepository extends CrudRepository<PageEntity, UUID> {


    default Collection<PageEntity> getByLemmaValueInAndPositiveCode(Collection<String> lemmaValues) {
        return this.getByLemmaValueInAndPositiveCode(lemmaValues, lemmaValues.size());
    }

    default Collection<PageEntity> getByLemmaValueInAndPositiveCodeAndSiteId(Collection<String> lemmaValues, UUID siteId) {
        return this.getByLemmaValueInAndPositiveCodeAndSiteId(lemmaValues, lemmaValues.size(), siteId);
    }

    @Query(value = "SELECT *\n" +
            "FROM page\n" +
            "WHERE id IN\n" +
            "       (SELECT page_id\n" +
            "       FROM lemma\n" +
            "       WHERE value IN ?1\n" +
            "       AND code/100 = 2\n" +
            "       GROUP BY page_id\n" +
            "       HAVING COUNT(*) = ?2)",
            nativeQuery = true)
    Collection<PageEntity> getByLemmaValueInAndPositiveCode(Collection<String> lemmaValues, int count);

    @Query(value = "SELECT *\n" +
            "FROM page\n" +
            "WHERE id IN\n" +
            "       (SELECT page_id\n" +
            "       FROM lemma INNER JOIN page\n" +
            "       ON page_id = page.id\n" +
            "       WHERE site_id = ?3\n" +
            "       AND value IN ?1\n" +
            "       GROUP BY page_id\n" +
            "       HAVING COUNT(*) = ?2)",
            nativeQuery = true)
    Collection<PageEntity> getByLemmaValueInAndPositiveCodeAndSiteId(Collection<String> lemmaValues, int count, UUID siteID);

    @Query(value = "SELECT page.*\n" +
            "FROM page INNER JOIN lemma\n" +
            "ON page.id IN ?2\n" +
            "AND page.id = page_id\n" +
            "WHERE value = ?1",
            nativeQuery = true)
    Collection<PageEntity> getByLemmaValueInAndPageIn(String lemmaValue, Collection<PageEntity> pageEntityCollection);

    Collection<PageEntity> getByPath(String path);

    @Query(value = "SELECT COUNT(*)\n" +
            "FROM page\n" +
            "WHERE site_id = ?1\n"
            , nativeQuery = true)
    long getCountPageBySiteId(UUID siteID);

    @Query(value = "SELECT COUNT(*)\n" +
            "FROM page\n" +
            "WHERE site_id IN ( SELECT id FROM site WHERE url =?1)\n" +
            "AND code/100 = 2"
            , nativeQuery = true)
    long getCountPageBySiteUrlAndPositiveCode(String url);

    @Transactional
    @Modifying
    void deleteBySiteId(UUID siteId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM page\n" +
            "WHERE path = ?2\n" +
            "AND site_id IN (SELECT id FROM site WHERE url = ?1)"
            , nativeQuery = true)
    void deleteByPath(String sitePath, String pagePath);



}
