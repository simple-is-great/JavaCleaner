package storagecleaner;

import util.FileUtility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static util.FileUtility.collectSameSizeFiles;
import static util.FileUtility.randomRead;

public class SpaceSave {
    public static long DUPLICATE_LOWER_BOUND = 0; // feature 2: save space
    public static long BIGFILE_LOWER_BOUND = 1000;

    private static long INITIAL_READ = 1000 * 1000;

    public static List<Path> findBigFiles(String sourceDirectory) {
        Path sourceDir = Paths.get(sourceDirectory);
        List<Path> pathList = FileUtility.getPathList(sourceDir);
        List<Path> bigFiles = new ArrayList<>();

        try {
            for (Path p : pathList) {
                long size = Files.size(p);
                if (size >= BIGFILE_LOWER_BOUND) {
                    bigFiles.add(p);
                }

                // sort by file size in descending order
                Collections.sort(bigFiles, (p1, p2) -> {
                    try {
                        return (int)(Files.size(p2) - Files.size(p1));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (IOException e) {
            System.err.println("Error in getting file size: ");
        }
        return bigFiles;
    }

    public static List<Path> findOldFiles(String sourceDirectory) {
        List<Path> oldFiles = new ArrayList<>();
        // TODO: implement this
        return oldFiles;
    }

    public static Map<String, List<Path>> findDuplicates(String sourceDirectory) {
        // randomly sample INITIAL_READ amount of files and generate hashes(SHA-256)
        // if equal -> save hashes:[path lists] to duplicates map
        Path sourceDir = Paths.get(sourceDirectory);
        Map<String, List<Path>> duplicatesMap = new HashMap<>();

        Map<Long, List<Path>> sameSizePathMap = collectSameSizeFiles(sourceDir, DUPLICATE_LOWER_BOUND);
        Set<Long> sameSizeMapKeys = sameSizePathMap.keySet();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            StringBuilder hexHash = new StringBuilder();
            String hashStr = "";
            for (Long key : sameSizeMapKeys) {
                for (Path p : sameSizePathMap.get(key)) {
                    byte[] hashBytes = digest.digest(randomRead(p, INITIAL_READ));
                    for (byte b : hashBytes) {
                        hexHash.append(String.format("%02x", b));
                    }

                    hashStr = hexHash.toString();
                    List<Path> innerList = duplicatesMap.get(hashStr);
                    if (innerList == null) {
                        innerList = new ArrayList<>();
                    }
                    innerList.add(p);
                    duplicatesMap.put(hashStr, innerList);
                    hexHash.delete(0, hexHash.length());
                }

                if (duplicatesMap.get(hashStr).size() == 1) {
                    // delete key-val pairs with single Path
                    duplicatesMap.remove(hashStr);
                }
            }
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error in getting hashing instance");
        }

        return duplicatesMap;
    }
}
