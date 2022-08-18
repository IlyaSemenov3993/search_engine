package ru.skillbox.searcher.exception.searchProcess;


public class NoSuitablePageException extends SearchProcessException {

    public final static String ERROR_MESSAGE = "Страница не найдена";

    public NoSuitablePageException() {
        super(ERROR_MESSAGE);
    }
}
