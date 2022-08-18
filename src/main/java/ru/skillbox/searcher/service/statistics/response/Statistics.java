package ru.skillbox.searcher.service.statistics.response;

public class Statistics {
    private Total total;
    private SiteDetail[] detailed;

    public Statistics(Total total, SiteDetail[] detailed) {
        this.total = total;
        this.detailed = detailed;
    }

    public Statistics() {
    }

    public Total getTotal() {
        return total;
    }

    public SiteDetail[] getDetailed() {
        return detailed;
    }

    public void setTotal(Total total) {
        this.total = total;
    }

    public void setDetailed(SiteDetail[] detailed) {
        this.detailed = detailed;
    }
}
