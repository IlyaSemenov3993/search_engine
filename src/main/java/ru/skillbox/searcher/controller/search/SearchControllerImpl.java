package ru.skillbox.searcher.controller.search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.searcher.service.search.SearchService;
import ru.skillbox.searcher.service.search.response.SearchResponse;

import java.util.Map;


@RestController
public class SearchControllerImpl implements SearchController {
    private static final Logger logger = LogManager.getLogger("AppFile");

    @Autowired
    private SearchService searchService;


    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public SearchResponse search(@RequestParam Map<String, String> requestParams) {
        return searchService.search(requestParams);
    }


}
