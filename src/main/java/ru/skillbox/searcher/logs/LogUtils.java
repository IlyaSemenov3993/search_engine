package ru.skillbox.searcher.logs;

import java.util.Arrays;
import java.util.stream.Collectors;

public class LogUtils {

    public static String getStackTrace(Throwable throwable){
        StackTraceElement[] stackTrace = throwable.getStackTrace();

        return Arrays.stream(stackTrace)
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
    }
}
