package ru.skillbox.searcher.exception.searchProcess;

public class LemmaNotDefinedException extends SearchProcessException{

    public final static String ERROR_MESSAGE = "Введенные леммы на сайте не найдены";

    public LemmaNotDefinedException() {
        super(ERROR_MESSAGE);
    }
}
