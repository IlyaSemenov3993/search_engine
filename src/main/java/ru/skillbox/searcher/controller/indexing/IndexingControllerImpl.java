package ru.skillbox.searcher.controller.indexing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.searcher.service.indexing.response.IndexingResponse;
import ru.skillbox.searcher.service.indexing.IndexingService;

import java.util.Map;

@RestController
public class IndexingControllerImpl implements IndexingController {

    @Autowired
    private IndexingService indexingService;

    @GetMapping(path = "/startIndexing", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public IndexingResponse startIndexing() {
        return this.indexingService.startIndexing();
    }

    @GetMapping(path = "/stopIndexing", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public IndexingResponse stopIndexing() {
        return indexingService.stopIndexing();
    }

    @PostMapping(path = "/indexSite", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public IndexingResponse indexSite(@RequestParam Map<String, String> requestParams) {
        return indexingService.indexSite(requestParams);
    }

    @PostMapping(path = "/indexPage", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public IndexingResponse indexPage(@RequestParam Map<String, String> requestParams) {
        return indexingService.indexPage(requestParams);
    }
}
