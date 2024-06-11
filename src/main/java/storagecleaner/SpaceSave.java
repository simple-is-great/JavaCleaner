package storagecleaner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static util.FileUtility.collectSameSizeFiles;
import static util.FileUtility.randomRead;

public class SpaceSave {
    public static long DUPLICATE_LOWER_BOUND = 0; // feature 2: save space
    private static long INITIAL_READ = 1000 * 1000;

    public static List<Path> findBigFiles(String sourceDirectory) {
        List<Path> bigFiles = new ArrayList<>();
        // TODO: implement this
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
