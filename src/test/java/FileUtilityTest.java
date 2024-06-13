import org.junit.jupiter.api.Test;
import storagecleaner.SpaceSave;
import util.FileUtility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static helper.JavaStorageCleanerTestHelper.deleteRecursively;
import static helper.JavaStorageCleanerTestHelper.pathListEquals;
import static helper.JavaStorageCleanerTestHelper.sameSizeMapEquals;
import static helper.JavaStorageCleanerTestHelper.verifyCopy;
import static helper.JavaStorageCleanerTestHelper.verifyMove;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static util.FileUtility.fastRead;

public class FileUtilityTest {
    private static final String commonPath = Paths.get("").toAbsolutePath().toString() + "/testRelated";
    private static byte[] sourceBytes = new byte[0];
    private static byte[] targetBytes = new byte[0];

    @Test
    void getPathListTest() {
        String sourceDir = commonPath + "/listPath";
        Path sourcePath = Paths.get(sourceDir);
        List<Path> pathList;
        List<Path> answer = new ArrayList<>();

        answer.add(Paths.get(sourceDir + "/inner/innerfile.txt"));
        answer.add(Paths.get(sourceDir + "/inner/innerfile2.txt"));
        answer.add(Paths.get(sourceDir + "/inner/innerfile3.txt"));
        answer.add(Paths.get(sourceDir + "/test1.txt"));
        answer.add(Paths.get(sourceDir + "/test2.txt"));
        answer.add(Paths.get(sourceDir + "/test3.txt"));

        pathList = FileUtility.getPathList(sourcePath);

        Collections.sort(answer);
        Collections.sort(pathList);

        assertTrue(pathListEquals(pathList, answer));
    }

    @Test
    void collectSameSizeFilesTest() {
        // this method is for collecting Paths which has identical file sizes()
        String sourceDir = commonPath + "/listPath";
        Path sourcePath = Paths.get(sourceDir);
        Map<Long, List<Path>> collectResult;
        Map<Long, List<Path>> sameSize = new HashMap();

        Path path1 = Paths.get(sourceDir + "/inner/innerfile.txt");
        Path path2 = Paths.get(sourceDir + "/inner/innerfile2.txt");
        Path path3 = Paths.get(sourceDir + "/inner/innerfile3.txt");
        Path path4 = Paths.get(sourceDir + "/test1.txt");
        Path path5 = Paths.get(sourceDir + "/test2.txt");
        Path path6 = Paths.get(sourceDir + "/test3.txt");

        List<Path> innerList1 = new ArrayList<>();
        List<Path> innerList2 = new ArrayList<>();

        innerList1.add(path1);
        innerList1.add(path2);
        innerList1.add(path3);
        innerList2.add(path4);
        innerList2.add(path5);
        innerList2.add(path6);

        Collections.sort(innerList1);
        Collections.sort(innerList2);

        try {
            sameSize.put(Files.size(path1), innerList1);
            sameSize.put(Files.size(path4), innerList2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        collectResult = FileUtility.collectSameSizeFiles(sourcePath, SpaceSave.DUPLICATE_LOWER_BOUND);

        assertTrue(sameSizeMapEquals(collectResult, sameSize));
    }

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

        resultBytes = fastRead(sourcePath);
        assert (Arrays.equals(sourceBytes, resultBytes));
    }

    @Test
    void fastCopyTest() {
        String sourceDir = commonPath + "/filterTest/card2(2164).py";
        String targetDir = commonPath + "/card2(2164).py";
        Path sourcePath = Paths.get(sourceDir);
        Path targetPath = Paths.get(targetDir);
        FileUtility.fastCopy(sourcePath, targetPath);
        sourceBytes = FileUtility.fastRead(sourcePath);
        targetBytes = FileUtility.fastRead(targetPath);
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
            FileUtility.copyDirectory(sourceDir, targetDir);
        } catch (IOException e1) {
            System.err.println("Error in copying directories" + e1);
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
            FileUtility.moveDirectory(sourceDir, targetDir);
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
            FileUtility.moveDirectory(targetDir, sourceDir);
        } catch (IOException e3) {
            System.out.println("Error occurred in moving source file");
            e3.printStackTrace();
        }
    }
}
