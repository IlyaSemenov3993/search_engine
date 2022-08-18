package ru.skillbox.searcher.exception.searchProcess;

public abstract class SearchProcessException extends Exception {


    public SearchProcessException(String errorMessage) {
        super(errorMessage);
    }
}
