package ru.skillbox.searcher.dto;

import ru.skillbox.searcher.model.entity.LemmaEntity;

public class LemmaDTO {

    private String value;
    private float rank;

    public LemmaDTO(LemmaEntity lemmaEntity){
        this.value = lemmaEntity.getValue();
        this.rank = lemmaEntity.getRank();
    }

    public String getValue() {
        return value;
    }

    public float getRank() {
        return rank;
    }
}
