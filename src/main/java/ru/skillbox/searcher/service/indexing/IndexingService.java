package ru.skillbox.searcher.service.indexing;

import ru.skillbox.searcher.service.indexing.response.IndexingResponse;

import java.util.Map;

public interface IndexingService {

    IndexingResponse startIndexing();

    IndexingResponse stopIndexing();

    IndexingResponse indexPage(Map<String, String> requestParams);

    IndexingResponse indexSite(Map<String, String> requestParams);
}
