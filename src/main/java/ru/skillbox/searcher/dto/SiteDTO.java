package ru.skillbox.searcher.dto;

import ru.skillbox.searcher.model.entity.enums.SiteStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class SiteDTO {
    private UUID id;
    private SiteStatus status;
    private LocalDateTime statusTime;
    private String lastError;
    private String url;
    private String name;

    public SiteDTO() {
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public SiteStatus getStatus() {
        return status;
    }

    public void setStatus(SiteStatus status) {
        this.status = status;
    }

    public LocalDateTime getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(LocalDateTime statusTime) {
        this.statusTime = statusTime;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
