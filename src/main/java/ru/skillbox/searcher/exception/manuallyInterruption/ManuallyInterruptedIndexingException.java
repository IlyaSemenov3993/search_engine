package ru.skillbox.searcher.exception.manuallyInterruption;

public class ManuallyInterruptedIndexingException extends RuntimeException {

    private static final String message = "Indexing has stoped manually";

    public ManuallyInterruptedIndexingException() {
        super(message);
    }
}
