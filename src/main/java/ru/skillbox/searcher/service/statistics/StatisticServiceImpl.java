package ru.skillbox.searcher.service.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.skillbox.searcher.config.AppConfig;
import ru.skillbox.searcher.model.mapper.SiteMapper;
import ru.skillbox.searcher.model.repository.SiteRepository;
import ru.skillbox.searcher.service.cash.CashHolder;
import ru.skillbox.searcher.service.statistics.response.SiteDetail;
import ru.skillbox.searcher.service.statistics.response.StatisticResponse;
import ru.skillbox.searcher.service.statistics.response.Statistics;
import ru.skillbox.searcher.service.statistics.response.Total;

import java.util.stream.StreamSupport;

@Service
public class StatisticServiceImpl implements StatisticService {

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private SiteMapper siteMapper;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private CashHolder cashHolder;


    @Override
    public StatisticResponse getStatistic() {
        StatisticResponse statisticResponse = cashHolder.getCashedResponse();

        if (statisticResponse != null) {
            return statisticResponse;
        } else {
            statisticResponse = this.buildStatisticResponse();
            cashHolder.setCashedResponse(statisticResponse);
        }

        return statisticResponse;
    }

    private StatisticResponse buildStatisticResponse() {
        StatisticResponse statisticResponse = new StatisticResponse();
        Statistics statistics = this.buildStatistics();
        statisticResponse.setStatistics(statistics);
        return statisticResponse;
    }

    private Statistics buildStatistics() {
        Statistics statistics = new Statistics();

        Total total = appConfig.total();
        SiteDetail[] detailed = this.buildDetailed();

        statistics.setTotal(total);
        statistics.setDetailed(detailed);

        return statistics;
    }

    private SiteDetail[] buildDetailed() {
        return StreamSupport.stream(siteRepository.findAll().spliterator(), false)
                .map(siteMapper::getDTO)
                .parallel()
                .map(appConfig::siteDetail)
                .toArray(SiteDetail[]::new);
    }


}
