package ru.skillbox.searcher.service.search;

import ru.skillbox.searcher.service.search.response.SearchResponse;

import java.util.Map;

public interface SearchService {

    SearchResponse search(Map<String, String> requestParams);
}

