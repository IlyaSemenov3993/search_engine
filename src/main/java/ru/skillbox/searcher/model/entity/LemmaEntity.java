package ru.skillbox.searcher.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "lemma")
public class LemmaEntity {

    @Id
    @Column
    private UUID id;

    @Column(name = "page_id")
    private UUID pageId;

    @Column
    private String value;

    @Column
    private float rank;

    public LemmaEntity() {
    }

    public LemmaEntity(UUID id, UUID pageId, String value, float rank) {
        this.id = id;
        this.pageId = pageId;
        this.value = value;
        this.rank = rank;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public UUID getPageId() {
        return pageId;
    }

    public void setPageId(UUID pageId) {
        this.pageId = pageId;
    }

    public float getRank() {
        return rank;
    }

    public void setRank(float rank) {
        this.rank = rank;
    }
}
