package ru.skillbox.searcher.exception.searchProcess;

public class InvalidURLException extends SearchProcessException{
    public final static String ERROR_MESSAGE = "Введена некорректная ссылка";

    public InvalidURLException() {
        super(ERROR_MESSAGE);
    }
}
