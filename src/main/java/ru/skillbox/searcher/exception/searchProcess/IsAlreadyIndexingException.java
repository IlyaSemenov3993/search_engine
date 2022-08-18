package ru.skillbox.searcher.exception.searchProcess;

public class IsAlreadyIndexingException extends SearchProcessException{

    public final static String ERROR_MESSAGE = "Индексация ещё не завершена";

    public IsAlreadyIndexingException() {
        super(ERROR_MESSAGE);
    }
}
