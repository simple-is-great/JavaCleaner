import org.junit.jupiter.api.Test;
import storagecleaner.FileBackup;
import storagecleaner.FileSearch;
import storagecleaner.SpaceSave;
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

import static helper.JavaStorageCleanerTestHelper.deleteRecursively;
import static helper.JavaStorageCleanerTestHelper.duplicateMapEquals;
import static helper.JavaStorageCleanerTestHelper.pathListEquals;
import static helper.JavaStorageCleanerTestHelper.verifyCopy;
import static helper.JavaStorageCleanerTestHelper.verifyFileFilter;
import static helper.JavaStorageCleanerTestHelper.verifyMove;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaStorageCleanerTest {
    private static final String commonPath = Paths.get("").toAbsolutePath().toString() + "/testRelated";

    @Test
    void filterFilesByModifiedDateTest() {
        String sourceDir = commonPath + "/filterTest";
        List<Path> fileList = new ArrayList<>();
        List<Path> filterList = new ArrayList<>();
        LocalDate fromDate = LocalDate.of(2024, 4, 1);
        LocalDate toDate = LocalDate.now().plusDays(1); // tomorrow

        fileList.add(Paths.get(sourceDir + "/parent_of_tree(11725).py"));
        fileList.add(Paths.get(sourceDir + "/ordinary_bag(12865).py"));
        fileList.add(Paths.get(sourceDir + "/parent_of_tree(11725).py"));
        fileList.add(Paths.get(sourceDir + "/ordinary_bag(12865).py"));
        fileList.add(Paths.get(sourceDir + "/parent_of_tree(11725).py"));
        fileList.add(Paths.get(sourceDir + "/ordinary_bag(12865).py"));

        try {
            filterList = FileSearch.filterFilesByModifiedDate(sourceDir, fromDate, toDate);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            assertTrue(verifyFileFilter(filterList, fileList));
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }
    @Test
    void findBigFilesTest() {
        String sourceDir = commonPath + "/listPath";
        List<Path> result = SpaceSave.findBigFiles(sourceDir);
        List<Path> bigFiles = new ArrayList<>();

        Path path1 = Paths.get(sourceDir + "/test1.txt");
        Path path2 = Paths.get(sourceDir + "/test2.txt");
        Path path3 = Paths.get(sourceDir + "/test3.txt");

        bigFiles.add(path1);
        bigFiles.add(path2);
        bigFiles.add(path3);

        // sort by file size in descending order
        Collections.sort(bigFiles, (p1, p2) -> {
            try {
                return (int)(Files.size(p2) - Files.size(p1));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        assertTrue(pathListEquals(result, bigFiles));
    }

    @Test
    void findDuplicatesTest() {
        String sourceDir = commonPath + "/listPath";
        Map<String, List<Path>> duplicateMap = SpaceSave.findDuplicates(sourceDir);

        Map<String, List<Path>> answer = new HashMap<>();

        Path path1 = Paths.get(sourceDir + "/inner/innerfile.txt");
        Path path2 = Paths.get(sourceDir + "/inner/innerfile2.txt");
        Path path3 = Paths.get(sourceDir + "/test1.txt");
        Path path4 = Paths.get(sourceDir + "/test2.txt");

        List<Path> innerList = new ArrayList<>();
        innerList.add(path1);
        innerList.add(path2);

        List<Path> innerList2 = new ArrayList<>();
        innerList2.add(path3);
        innerList2.add(path4);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(FileUtility.randomRead(path1, 1000 * 1000));
            StringBuilder hexHash = new StringBuilder();
            for (byte b : hashBytes) {
                hexHash.append(String.format("%02x", b));
            }

            String hashStr = hexHash.toString();
            answer.put(hashStr, innerList);

            hexHash.delete(0, hexHash.length());
            byte[] hashBytes2 = digest.digest(FileUtility.randomRead(path3, 1000 * 1000));
            for (byte b : hashBytes2) {
                hexHash.append(String.format("%02x", b));
            }

            String hashStr2 = hexHash.toString();
            answer.put(hashStr2, innerList2);

        } catch (NoSuchAlgorithmException e) {
            System.err.println("algorithm not valid");
            e.printStackTrace();
        }

        assertTrue(duplicateMapEquals(duplicateMap, answer));
    }

    @Test
    void backupFilesTest() {
        // feature 4: sync folders to cloud
        String sourceDir = commonPath + "/syncFrom";
        String cloudDir = "/Users/byeonjiseob/Library/Mobile Documents/com~apple~CloudDocs/syncTo";

        // move
        try {
            FileBackup.backupFiles(sourceDir, cloudDir, true); // last parameter: move flag
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            assertTrue(verifyMove(sourceDir, cloudDir));
        } catch (IOException e2) {
            e2.printStackTrace();
        }

        // rollback
        try {
            FileUtility.moveDirectory(cloudDir, sourceDir);
        } catch (IOException e3) {
            e3.printStackTrace();
        }

        // copy
        try {
            FileBackup.backupFiles(sourceDir, cloudDir, false);
        } catch (IOException e4) {
            e4.printStackTrace();
        }

        try {
            assertTrue(verifyCopy(sourceDir, cloudDir));
        } catch (IOException e5) {
            e5.printStackTrace();
        }
        // rollback
        try {
            deleteRecursively(cloudDir);
        } catch (IOException e6) {
            e6.printStackTrace();
        }
    }
}