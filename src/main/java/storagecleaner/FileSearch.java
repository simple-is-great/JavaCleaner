package storagecleaner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static util.FileUtility.localDateToFileTime;

public class FileSearch {
    // feature 1: search file
    public static List<Path> filterFilesByModifiedDate(String sourceDir, LocalDate fromDate, LocalDate toDate) throws IOException {
        List<Path> filterResults = new ArrayList<>();
        FileTime fromFileTime = localDateToFileTime(fromDate);
        FileTime toFileTime = localDateToFileTime(toDate);

        Files.walk(Paths.get(sourceDir))
                .forEach(source -> {
                    try {
                        if (!Files.isDirectory(source)) { // ignore directories
                            FileTime sourceModifiedTime = Files.getLastModifiedTime(source);
                            if (sourceModifiedTime.compareTo(fromFileTime) >= 0
                                    && sourceModifiedTime.compareTo(toFileTime) <= 0) {
                                filterResults.add(source);
                            }
                        }
                    } catch (IOException e) {
                        System.err.println("Error in getting modified time: " + source);
                    }
                });

        return filterResults;
    }
}
