package ru.skillbox.searcher.dto;


import java.util.Objects;
import java.util.UUID;

public class PageDTO {
    private UUID id;
    private UUID siteId;
    private int responseCode;
    private String content;
    private String url;


    public int getResponseCode() {
        return responseCode;
    }

    public UUID getSiteId() {
        return siteId;
    }

    public void setSiteId(UUID siteId) {
        this.siteId = siteId;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageDTO pageDTO = (PageDTO) o;
        return Objects.equals(url, pageDTO.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
