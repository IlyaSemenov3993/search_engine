package ru.skillbox.searcher.service.statistics.response;

public class StatisticResponse {
    private final boolean result;
    private Statistics statistics;

    public StatisticResponse(Statistics statistics) {
        this();
        this.statistics = statistics;
    }

    public StatisticResponse() {
        this.result = true;
    }

    public boolean isResult() {
        return result;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }
}
