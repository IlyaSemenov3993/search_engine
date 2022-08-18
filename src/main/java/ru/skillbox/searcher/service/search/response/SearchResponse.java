package ru.skillbox.searcher.service.search.response;

import ru.skillbox.searcher.view.Page;
import java.util.Collection;

public class SearchResponse {
    private boolean result;
    private String error;
    private int count;
    private Collection<Page> data;

    public SearchResponse(Collection<Page> pageCollection) {
        this.data = pageCollection;
        this.count = pageCollection.size();
        this.result = true;
    }

    public SearchResponse(String error) {
        this.error = error;
    }

    public SearchResponse() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Collection<Page> getData() {
        return data;
    }

    public boolean isResult() {
        return result;
    }
}
