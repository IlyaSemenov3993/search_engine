package ru.skillbox.searcher.exception.searchProcess;

public class RequestMissingException extends SearchProcessException {

    public final static String ERROR_MESSAGE = "Задан пустой поисковый запрос";

    public RequestMissingException() {
        super(ERROR_MESSAGE);
    }
}
