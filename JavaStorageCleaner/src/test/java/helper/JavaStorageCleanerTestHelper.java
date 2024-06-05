package helper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaStorageCleanerTestHelper {
    // method to delete a file or directory recursively
    private static ByteBuffer buffer1 = ByteBuffer.allocateDirect(1024 * 1024); // 1MB
    private static ByteBuffer buffer2 = ByteBuffer.allocateDirect(1024 * 1024);

    public static void deleteRecursively(String pathStr) throws IOException {
        Path path = Paths.get(pathStr);
        if (Files.isDirectory(path)) {
            // walk through the directory and delete all files and subdirectories
            try (Stream<Path> stream = Files.walk(path)) { // Reverse order to delete children first
                stream.sorted(Comparator.reverseOrder()).forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to delete: " + p, e);
                    }
                });
            }
        } else { // delete a single file
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete: " + path, e);
            }
        }
    }

    // method to verify the copied directory and files
    public static boolean verifyCopy(String sourceDirectoryLocation, String destinationDirectoryLocation) throws IOException {
        Path sourceDir = Paths.get(sourceDirectoryLocation);
        Path targetDir = Paths.get(destinationDirectoryLocation);

        if (!Files.exists(targetDir)) {
            System.err.println("Target directory does not exist or is not a directory.");
            return false;
        }

        // walk source directory and compare with source directory
        try (Stream<Path> sourceStream = Files.walk(sourceDir)) {
            // get relative path from sourcePath and extend relative path to targetDir
            for (Path sourcePath : sourceStream.collect(Collectors.toList())) {
                // ex. sourceDir/hello.c -> relativePath: hello.c
                //     targetDir + relativePath -> targetDir/hello.c
                Path relativePath = sourceDir.relativize(sourcePath);
                Path targetPath = targetDir.resolve(relativePath);

                if (Files.notExists(targetPath)) {
                    System.err.println("Target file not exists: " + targetPath);
                    return false;
                }

                if (!(Files.isReadable(targetPath) || Files.isWritable(targetPath) || Files.isExecutable(targetPath))) {
                    System.err.println("File content is not accessible, please check permission for the file: " + targetPath);
                    return false;
                }

                if (!fileSizeEquals(targetPath, targetPath)) {
                    System.err.println("File size not equals: " + targetPath + " and " + targetPath);
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean verifyMove(String sourceDirectoryLocation, String destinationDirectoryLocation) throws IOException {
        Path sourceDir = Paths.get(sourceDirectoryLocation);
        Path targetDir = Paths.get(destinationDirectoryLocation);

        // check if source directory exists
        if (Files.exists(sourceDir)) {
            System.err.println("Source directory still exists: " + sourceDir);
            return false;
        }

        // walk target directory and check
        try (Stream<Path> targetStream = Files.walk(targetDir)) {
            for (Path targetPath : targetStream.collect(Collectors.toList())) {
                // throw error message when file content is not accessible by JavaCleaner
                if (!(Files.isReadable(targetPath) || Files.isWritable(targetPath) || Files.isExecutable(targetPath))) {
                    System.err.println("File content is not accessible, please check permission for the file: " + targetPath);
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean fileSizeEquals(Path path1, Path path2) {
        long path1Size;
        long path2Size;

        try {
            path1Size = Files.readAttributes(path1, "size").size();
        } catch (IOException e) {
            System.err.println("Cannot get file size from path1");
            return false;
        }
        try {
            path2Size = Files.readAttributes(path2, "size").size();
        } catch (IOException e) {
            System.err.println("Cannot get file size from path2");
            return false;
        }

        return path1Size == path2Size;
    }

    public static boolean verifyFileFilter(List<Path> filterResult, List<Path> fileList) throws IOException { // handle null cases
        if (filterResult == null || fileList == null) {
            System.out.println("null error -> false");
            return false;
        }

        if (filterResult.size() != fileList.size()) {
            System.out.println("fileList size: " + fileList.size());
            System.out.println("filterResult size: " + filterResult.size());
            System.out.println("list size different -> false");
            return false;
        }

        for (int i = 0; i < filterResult.size(); i++) {
            // for debugging
            System.out.println("filterResult: " + filterResult.get(i) + "\nfileList: " + fileList.get(i));
            if (!JavaStorageCleanerTestHelper.fileSizeEquals(filterResult.get(i), fileList.get(i))) {
                System.out.println("file size not same -> false");
                return false;
            }
        }

        return true;
    }
}