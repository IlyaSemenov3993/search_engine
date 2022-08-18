package ru.skillbox.searcher.controller.indexing;

import ru.skillbox.searcher.service.indexing.response.IndexingResponse;

import java.util.Map;

public interface IndexingController {

    IndexingResponse startIndexing();

    IndexingResponse stopIndexing();

    IndexingResponse indexSite(Map<String, String> requestParams);

    IndexingResponse indexPage(Map<String, String> requestParams);
}
