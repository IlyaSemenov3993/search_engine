package ru.skillbox.searcher.process.shutdown;

public interface Shutdownable {

    void shutdown();

    boolean isStartShutdownable();
}
