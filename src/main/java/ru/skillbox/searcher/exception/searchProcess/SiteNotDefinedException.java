package ru.skillbox.searcher.exception.searchProcess;

public class SiteNotDefinedException extends SearchProcessException{

    public final static String ERROR_MESSAGE = "Указанный сайт не индексирован";

    public SiteNotDefinedException() {
        super(ERROR_MESSAGE);
    }
}
