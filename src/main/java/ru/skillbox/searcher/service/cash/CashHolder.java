package ru.skillbox.searcher.service.cash;

import org.springframework.stereotype.Component;
import ru.skillbox.searcher.service.statistics.response.StatisticResponse;

@Component
public class CashHolder {

    private StatisticResponse cashedResponse;

    public StatisticResponse getCashedResponse() {
        return cashedResponse;
    }

    public void setCashedResponse(StatisticResponse cashedResponse) {
        this.cashedResponse = cashedResponse;
    }

    public void clearStatisticCash(){
        this.cashedResponse = null;
    }
}
