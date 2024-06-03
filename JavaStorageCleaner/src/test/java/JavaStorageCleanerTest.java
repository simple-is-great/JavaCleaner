import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        String targetDir = commonPath +"/card2(2164).py";
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
        LocalDate toDate = LocalDate.of(2024, 5, 10);
        List<Path> filterList = new ArrayList<>();
        // sample data to test
        fileList.add(Paths.get(sourceDir + "/parent_of_tree(11725).py"));
        fileList.add(Paths.get(sourceDir + "/ordinary_bag(12865).py"));

        try {
            filterList = JavaStorageCleaner.filterFilesByModifiedDate(sourceDir, fromDate, toDate);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            for (Path fp : filterList) {
                System.out.println("filterList: " + fp);
            }

            for (Path flp : fileList) {
                System.out.println("fileList: " + flp);
            }

            assertTrue(verifyFileFilter(filterList, fileList));
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private static boolean verifyFileFilter(List<Path> filterResult, List<Path> fileList) throws IOException {
        // handle null cases
        if (filterResult == null || fileList == null) {
            System.out.println("null error -> false");
            return false;
        }

        if (filterResult.size() != fileList.size()) {
            System.out.println("list size different -> false");
            return false;
        }

        for (int i = 0; i < filterResult.size(); i++) {
            // for debugging
//             System.out.println("filterResult: " + filterResult.get(i) + "\nfileList: " + fileList.get(i));
            if (!fileContentEquals(filterResult.get(i), fileList.get(i))) {
                System.out.println("file content not same -> false");
                return false;
            }
        }

        return true;
    }

    // method to delete a file or directory recursively
    private static void deleteRecursively(String pathStr) throws IOException {
        Path path = Paths.get(pathStr);
        if (Files.isDirectory(path)) {
            // walk through the directory and delete all files and subdirectories
            try (Stream<Path> stream = Files.walk(path)) {
                stream.sorted((a, b) -> b.compareTo(a))  // Reverse order to delete children first
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                                System.out.println("Deleted: " + p);
                            } catch (IOException e) {
                                throw new RuntimeException("Failed to delete: " + p, e);
                            }
                        });
            }
        } else {
            // delete a single file
            Files.delete(path);
            System.out.println("Deleted: " + path);
        }
    }

    // method to verify the copied directory and files
    private static boolean verifyCopy(String sourceDirectoryLocation, String destinationDirectoryLocation) throws IOException {
        Path sourceDir = Paths.get(sourceDirectoryLocation);
        Path targetDir = Paths.get(destinationDirectoryLocation);

        // check if target directory exists
        if (!Files.exists(targetDir)) {
            System.err.println("Target directory does not exist or is not a directory.");
            return false;
        }

        // walk source directory and compare with target directory
        try (Stream<Path> sourceStream = Files.walk(sourceDir)) {
            for (Path sourcePath : sourceStream.collect(Collectors.toList())) {
                // get relative path
                Path relativePath = sourceDir.relativize(sourcePath);
                // extend relative path
                Path targetPath = targetDir.resolve(relativePath);

                if (Files.isDirectory(sourcePath)) {
                    // check if directory exists in the target location
                    if (!Files.exists(targetPath) || !Files.isDirectory(targetPath)) {
                        System.err.println("Directory not copied: " + targetPath);
                        return false;
                    }
                } else { // check if file exists and has the same content
                    if (!Files.exists(targetPath) || !Files.isRegularFile(targetPath)) {
                        System.err.println("File not copied or is not a regular file: " + targetPath);
                        return false;
                    }

                    // compare file contents
                    if (!fileContentEquals(sourcePath, targetPath)) {
                        System.err.println("File contents differ: " + sourcePath + " and " + targetPath);
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private static boolean verifyMove(String sourceDirectoryLocation, String destinationDirectoryLocation) throws IOException {
        Path sourceDir = Paths.get(sourceDirectoryLocation);
        Path targetDir = Paths.get(destinationDirectoryLocation);

        // check if source directory exists
        if (Files.exists(sourceDir)) {
            System.err.println("Source directory still exists: " + sourceDir);
            return false;
        }

        // walk source directory and compare with target directory
        try (Stream<Path> sourceStream = Files.walk(targetDir)) {
            for (Path targetPath : sourceStream.collect(Collectors.toList())) {
                // get relative path
                Path relativePath = targetDir.relativize(targetPath);
                // extend relative path
                Path OriginalSourcePath = sourceDir.resolve(relativePath);

                if (Files.isDirectory(targetPath)) {
                    // check if directory exists in the target location
                    if (!Files.exists(targetPath) || !Files.isDirectory(targetPath)) {
                        System.err.println("Directory structure mismatch or directory not found: " + targetPath);
                        return false;
                    }
                } else { // check if file exists and has the same content
                    if (!Files.exists(targetPath) || !Files.isRegularFile(targetPath)) {
                        System.err.println("File not found or is not a regular file: " + targetPath);
                        return false;
                    }

                    // check if we can access file contents
                    if (!canAccessFile(targetPath)) {
                        System.err.println("File content is not accessible: " + targetPath);
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private static boolean canAccessFile(Path filePath) {
        try {
            Files.readAllBytes(filePath);
            return true;
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath);
            return false;
        }
    }

    private static boolean fileContentEquals(Path path1, Path path2) throws IOException{
        sourceBytes = Files.readAllBytes(path1);
        targetBytes = Files.readAllBytes(path2);

        if (Arrays.equals(sourceBytes, targetBytes)) return true;
        return false;
    }

}