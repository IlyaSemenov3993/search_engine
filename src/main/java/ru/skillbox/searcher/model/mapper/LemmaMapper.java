package ru.skillbox.searcher.model.mapper;

import org.springframework.stereotype.Component;
import ru.skillbox.searcher.model.entity.LemmaEntity;

import java.util.UUID;

@Component
public class LemmaMapper {

    public LemmaEntity getEntity(UUID pageId, String lemma, int intIndex) {
        UUID id = UUID.randomUUID();
        float rank = intIndex / 10f;

        LemmaEntity lemmaEntity = new LemmaEntity();
        lemmaEntity.setId(id);
        lemmaEntity.setPageId(pageId);
        lemmaEntity.setValue(lemma);
        lemmaEntity.setRank(rank);

        return lemmaEntity;
    }
}
