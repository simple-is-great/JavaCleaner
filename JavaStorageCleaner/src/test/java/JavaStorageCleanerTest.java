import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static helper.JavaStorageCleanerTestHelper.deleteRecursively;
import static helper.JavaStorageCleanerTestHelper.verifyCopy;
import static helper.JavaStorageCleanerTestHelper.verifyFileFilter;
import static helper.JavaStorageCleanerTestHelper.verifyMove;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaStorageCleanerTest {
    private static final String commonPath = "/Users/byeonjiseob/IdeaProjects/JavaStorageCleaner/src/test/testRelated"; // TODO: change this path before you test!
    private static byte[] sourceBytes = new byte[0];
    private static byte[] targetBytes = new byte[0];

    @Test
    void fastReadTest() {
        String sourceDir = commonPath + "/filterTest/card2(2164).py";
        Path sourcePath = Paths.get(sourceDir);
        byte[] resultBytes = null;
        try {
            sourceBytes = Files.readAllBytes(sourcePath);
        } catch (IOException e) {
            System.err.println("error in reading bytes");
            e.printStackTrace();
        }

        resultBytes = JavaStorageCleaner.fastRead(sourcePath);
        assert (Arrays.equals(sourceBytes, resultBytes));
    }

    @Test
    void fastCopyTest() {
        String sourceDir = commonPath + "/filterTest/card2(2164).py";
        String targetDir = commonPath + "/card2(2164).py";
        Path sourcePath = Paths.get(sourceDir);
        Path targetPath = Paths.get(targetDir);
        JavaStorageCleaner.fastCopy(sourcePath, targetPath);
        sourceBytes = JavaStorageCleaner.fastRead(sourcePath);
        targetBytes = JavaStorageCleaner.fastRead(targetPath);
        assertTrue(Arrays.equals(sourceBytes, targetBytes));
        try {
            Files.delete(targetPath);
        } catch (IOException e) {
            System.err.println("Error in deleting file");
            e.printStackTrace();
        }
    }

    @Test
    void copyDirectoryTest() {
        // check if method copies directory recursively
        String sourceDir = commonPath + "/copyFrom";
        String targetDir = commonPath + "/copyTo";

        try {
            JavaStorageCleaner.copyDirectory(sourceDir, targetDir);
        } catch (IOException e1) {
            System.err.println("Error in copying directories" + e1);
//            e1.printStackTrace();
        }

        // check if source directory and target directory files, structures are equal
        try {
            assertTrue(verifyCopy(sourceDir, targetDir));
        } catch (IOException e2) {
            System.err.println("Error in verifying copies");
            e2.printStackTrace();
        }

        try {
            deleteRecursively(targetDir);
        } catch (IOException e3) {
            System.err.println("Error in deleting copied files");
            e3.printStackTrace();
        }
    }

    @Test
    void moveDirectoryTest() {
        String sourceDir = commonPath + "/moveFrom";
        String targetDir = commonPath + "/moveTo";

        try {
            JavaStorageCleaner.moveDirectory(sourceDir, targetDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // check if source directory and target directory files, structures are equal
        try {
            assertTrue(verifyMove(sourceDir, targetDir));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // rollback -> move targetDir back to sourceDir
        try {
            JavaStorageCleaner.moveDirectory(targetDir, sourceDir);
        } catch (IOException e3) {
            System.out.println("Error occurred in moving source file");
            e3.printStackTrace();
        }
    }

    @Test
    void syncToCloudTest() {
        // feature 4: sync folders to cloud
        String sourceDir = commonPath + "/syncFrom";
        String cloudDir = "/Users/byeonjiseob/Library/Mobile Documents/com~apple~CloudDocs/syncTo";

        // move
        try {
            JavaStorageCleaner.syncToCloud(sourceDir, cloudDir, true); // last parameter: move flag
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
            JavaStorageCleaner.moveDirectory(cloudDir, sourceDir);
        } catch (IOException e3) {
            e3.printStackTrace();
        }

        // copy
        try {
            JavaStorageCleaner.syncToCloud(sourceDir, cloudDir, false);
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

    @Test
    void filterFilesByModifiedDateTest() {
        String sourceDir = commonPath + "/filterTest";
        List<Path> fileList = new ArrayList<Path>();
        LocalDate fromDate = LocalDate.of(2024, 4, 1);
        LocalDate toDate = LocalDate.of(2024, 6, 4);
        List<Path> filterList = new ArrayList<>();
        // sample data to test
        fileList.add(Paths.get(sourceDir + "/parent_of_tree(11725).py"));
        fileList.add(Paths.get(sourceDir + "/ordinary_bag(12865).py"));
        fileList.add(Paths.get(sourceDir + "/parent_of_tree(11725).py"));
        fileList.add(Paths.get(sourceDir + "/ordinary_bag(12865).py"));
        fileList.add(Paths.get(sourceDir + "/parent_of_tree(11725).py"));
        fileList.add(Paths.get(sourceDir + "/ordinary_bag(12865).py"));

        try {
            filterList = JavaStorageCleaner.filterFilesByModifiedDate(sourceDir, fromDate, toDate);
        } catch (IOException e1) {
            e1.printStackTrace();
        }


        try {
            assertTrue(verifyFileFilter(filterList, fileList));
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }


}