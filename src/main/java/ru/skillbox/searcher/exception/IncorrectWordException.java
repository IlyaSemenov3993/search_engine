package ru.skillbox.searcher.exception;

public class IncorrectWordException extends Exception {

    private final static String messageLayout = "Can't define language at word: %s";

    public IncorrectWordException(String word) {
        super(String.format(messageLayout , word));
    }
}
