package ru.skillbox.searcher.service.indexing.response;


public class IndexingResponse {
    private boolean result;
    private String error;

    public IndexingResponse() {
        this.result = true;
    }

    public IndexingResponse(String error) {
        this.error = error;
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
}
