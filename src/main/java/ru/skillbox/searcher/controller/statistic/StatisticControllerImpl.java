package ru.skillbox.searcher.controller.statistic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.searcher.service.statistics.response.StatisticResponse;
import ru.skillbox.searcher.service.statistics.StatisticService;

@RestController
public class StatisticControllerImpl implements StatisticController {

    @Autowired
    private StatisticService statisticService;

    @GetMapping(path = "/statistics", produces = "application/json")
    public StatisticResponse getStatistic() {
        return statisticService.getStatistic();
    }
}
