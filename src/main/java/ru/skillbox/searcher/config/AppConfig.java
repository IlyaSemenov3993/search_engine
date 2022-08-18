package ru.skillbox.searcher.config;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.skillbox.searcher.model.nativeExecutors.insert.LemmaInsertExecutor;
import ru.skillbox.searcher.model.nativeExecutors.insert.PageInsertExecutor;
import ru.skillbox.searcher.process.executors.indexing.insertion.ParseResponseInsertExecutor;
import ru.skillbox.searcher.dto.SiteDTO;
import ru.skillbox.searcher.process.executors.indexing.IndexingExecutor;
import ru.skillbox.searcher.process.executors.indexing.IndexingExecutorImpl;
import ru.skillbox.searcher.process.executors.indexing.insertion.ParseResponseInserter;
import ru.skillbox.searcher.process.executors.indexing.parse.ParseExecutor;
import ru.skillbox.searcher.process.website.parser.Parser;
import ru.skillbox.searcher.process.website.parser.RecursiveScanner;
import ru.skillbox.searcher.process.website.parser.RecursiveScannerTask;
import ru.skillbox.searcher.service.statistics.response.SiteDetail;
import ru.skillbox.searcher.service.statistics.response.Total;

import java.net.URL;

@Configuration
@ComponentScan("ru/skillbox/searcher")
@PropertySource("classpath:/application.properties")
@EnableScheduling
public class AppConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertiesResolver() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Parser parser() {
        return new Parser();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RecursiveScanner recursiveScanner() {
        return new RecursiveScanner();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RecursiveScannerTask recursiveScannerTask(SiteConfig.Site site) {
        return new RecursiveScannerTask(site);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ParseResponseInserter parseResponseInserter() {
        return new ParseResponseInserter();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ParseExecutor parseExecutor() {
        return new ParseExecutor();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ParseResponseInsertExecutor parseResponseMultiTablesInsertExecutor() {
        return new ParseResponseInsertExecutor();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public PageInsertExecutor pageInsertExecutor() {
        return new PageInsertExecutor();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public LemmaInsertExecutor lemmaInsertExecutor() {
        return new LemmaInsertExecutor();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public IndexingExecutor indexingExecutor() {
        return new IndexingExecutorImpl();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public SiteDetail siteDetail(SiteDTO siteDTO) {
        return new SiteDetail(siteDTO);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Total total() {
        return new Total();
    }

}
