package ru.skillbox.searcher.controller.search;

import ru.skillbox.searcher.service.search.response.SearchResponse;

import java.util.Map;

public interface SearchController {

    SearchResponse search(Map<String,String> requestParams);
}
