package helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class FileUtils {

    public static String getFileContent(String path) throws IOException {
        return Files.lines(Paths.get(path))
                .collect(Collectors.joining("\n"));
    }

    public static String getFileContentWithLimitLines(String path, long limit) throws IOException {
        return Files.lines(Paths.get(path))
                .limit(limit)
                .collect(Collectors.joining("\n"));
    }
}
