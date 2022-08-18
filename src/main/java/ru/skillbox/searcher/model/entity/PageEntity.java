package ru.skillbox.searcher.model.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "page")
public class PageEntity {

    @Id
    @Column
    private UUID id;

    @Column(name = "site_id")
    private UUID siteId;

    @Column
    private int code;

    @Column
    private String content;

    @Column
    private String path;

    public PageEntity() {
    }

    public PageEntity(UUID id, UUID siteId, int code, String content, String path) {
        this.id = id;
        this.siteId = siteId;
        this.code = code;
        this.content = content;
        this.path = path;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSiteId() {
        return siteId;
    }

    public void setSiteId(UUID siteId) {
        this.siteId = siteId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
