package ru.skillbox.searcher.model.repository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.skillbox.searcher.model.entity.LemmaEntity;

import java.util.Collection;
import java.util.UUID;

public interface LemmaRepository extends CrudRepository<LemmaEntity, UUID> {


    Collection<LemmaEntity> getByPageId(UUID pageId);

    Collection<LemmaEntity> getByPageIdAndValueIn(UUID pageId, Collection<String> values);

    @Query(value = "SELECT value\n" +
            "FROM\n" +
            "    (SELECT value, count , NTILE(10) OVER w AS part\n" +
            "    FROM\n" +
            "        (SELECT value , COUNT(*) count\n" +
            "        FROM lemma\n" +
            "        GROUP BY value) t1\n" +
            "    WINDOW w AS (ORDER BY count)) t2\n" +
            "WHERE part = 10",
            nativeQuery = true)
    Collection<String> getMostPopularValues();

    @Query(value = "SELECT SUM(rank)\n" +
            "FROM lemma\n" +
            "WHERE page_id = ?1\n" +
            "AND value IN ?2",
            nativeQuery = true)
    float sumRankByPageIdAndValueIn(UUID pageId, Collection<String> values);

    @Query(value = "SELECT value " +
            "FROM lemma " +
            "WHERE value IN ?1 " +
            "GROUP BY value " +
            "ORDER BY COUNT(value)",
            nativeQuery = true)
    Collection<String> getValueInOrderByCount(Collection<String> values);

    @Query(value = "SELECT DISTINCT value FROM lemma WHERE value IN ?1", nativeQuery = true)
    Collection<String> getValueDistinctWhereValueIn(Collection<String> valueCollection);

    @Query(value = "SELECT COUNT(DISTINCT value) FROM lemma ", nativeQuery = true)
    long getCountDistinctValue();

    @Query(value = "SELECT COUNT(DISTINCT value) FROM lemma\n" +
            "INNER JOIN page ON page.id = lemma.page_id\n" +
            "WHERE page.site_id = ?1"
            , nativeQuery = true)
    long getCountDistinctValueBySiteId(UUID siteId);

    @Query(value = "SELECT value FROM lemma\n" +
            "WHERE value NOT IN ?1\n" +
            "GROUP BY value\n" +
            "HAVING COUNT(*) > (SELECT MIN(count) FROM\n" +
            "   (SELECT value, COUNT(*) AS count\n" +
            "   FROM lemma\n" +
            "   WHERE value IN ?1\n" +
            "   GROUP BY value) t1)"
            , nativeQuery = true)
    Collection<String> getMorePopularLemmas(Collection<String> lemmas);


    long countByPageId(UUID pageId);

}
