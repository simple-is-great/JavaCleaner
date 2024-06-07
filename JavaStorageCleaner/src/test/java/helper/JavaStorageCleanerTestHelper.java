package helper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaStorageCleanerTestHelper {
    // method to delete a file or directory recursively
    private static ByteBuffer buffer1 = ByteBuffer.allocateDirect(1000 * 1000); // 1MB
    private static ByteBuffer buffer2 = ByteBuffer.allocateDirect(1000 * 1000);

    public static boolean duplicateMapEquals(Map<String, List<Path>> duplicateMap1, Map<String, List<Path>> duplicateMap2) {
        Set<String> map1Keys = duplicateMap1.keySet();
        Set<String> map2Keys = duplicateMap2.keySet();

        if (map1Keys.size() != map2Keys.size()) {
            System.out.println("key size differs -> return false");
            System.out.printf("map1: %d, map2: %d", map1Keys.size(), map2Keys.size());
            // key size differs -> return false
            return false;
        }

        for (String key : map1Keys) {
            List<Path> innerList1 = duplicateMap1.get(key);
            List<Path> innerList2 = duplicateMap2.get(key);

            if (innerList1.size() != innerList2.size()) {
                System.out.println("innerList size differs -> return false");
                // return false if innerList size differs
                return false;
            }

            for (int i = 0; i < innerList1.size(); i++) {
                if (!innerList1.get(i).equals(innerList2.get(i))) {
                    System.out.println("innerList element differs -> return false");
                    // element of innerList differs
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean pathListEquals(List<Path> pathList1, List<Path> pathList2) {
        if (pathList1.size() != pathList2.size()) {
            System.out.println("pathList1, pathList2 size differs! :" + pathList1.size() + pathList2.size());
            return false;
        }

        for (int i = 0; i < pathList1.size(); i++) {
            if (!pathList1.get(i).equals(pathList2.get(i))) {
                System.out.println("pathList1, pathList2 element differs! :" + pathList1.get(i) + pathList2.get(i));
                return false;
            }
        }

        return true;
    }

    public static boolean sameSizeMapEquals(Map<Long, List<Path>> map1, Map<Long, List<Path>> map2) {
        Set<Long> map1Keys = map1.keySet();
        Set<Long> map2Keys = map2.keySet();

        if (!map1Keys.equals(map2Keys)) {
            System.out.println("map1 and map2 has different keys!");
            return false;
        }

        for (Long key: map1Keys) {
            List<Path> map1InnerList = map1.get(key);
            List<Path> map2InnerList = map2.get(key);

            if (map1InnerList.size() != map2InnerList.size()) { // List<Path> size differs
                System.out.println("map1 and map2 innerLists has different sizes!");
                return false;
            }
            for (int i = 0; i < map1InnerList.size(); i++) {
                if (!map1InnerList.get(i).equals(map2InnerList.get(i))) {
                    System.out.println("map1 element" + map1InnerList.get(i));
                    System.out.println("map2 element" + map2InnerList.get(i));
                    System.out.println("map1 and map2 innerLists has different elements!"); // path differs
                    return false;
                }
            }
        }

        return true;
    }



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