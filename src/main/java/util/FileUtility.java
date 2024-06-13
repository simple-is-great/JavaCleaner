package util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FileUtility {
    private static final int bufferSize = 1000 * 1000; // 1MB
    private static final ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);
    private static final String commonPath = Paths.get("").toAbsolutePath().toString() + "/testRelated";

    /* File Search Related */
    public static FileTime localDateToFileTime(LocalDate localDate) {
        // LocalDate -> LocalDateTime -> Instant -> FileTime
        LocalDateTime localDateTime = localDate.atStartOfDay();
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return FileTime.from(instant);
    }

    /* Space Save Related */
    public static byte[] randomFastRead(Path toReadPath, long amountToRead) {
        int toRead = (int) Math.random() * 1000 / 1;
        FileChannel readChannel = null;
        byte[] result = null;
        // get byte array for saving result
        result = new byte[(int) amountToRead];

        // open read channel
        try {
            readChannel = FileChannel.open(toReadPath, StandardOpenOption.READ);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // read through channel
        try {
            while (readChannel.read(buffer) >= 0) {
                buffer.flip();
                buffer.get(result);
                buffer.clear();
            }
        } catch (IOException channelReadException) {
            System.err.println(channelReadException);
            channelReadException.printStackTrace();
        }

        return result;
    }

    public static byte[] randomRead(Path path, long readAmount) {
        if (readAmount <= 0) {
            throw new IllegalArgumentException("read amount must be greater than zero.");
        }

        Random rand;
        try {
            // set readAmount to fileSize if fileSize is smaller than readAmount
            long fileSize = Files.size(path);
            readAmount = fileSize < readAmount ? fileSize : readAmount;
        } catch (IOException e) {
            System.err.println("Error in reading file size " + path);
        }

        byte[] result = new byte[(int) readAmount];
        int chunkSize = (int) (readAmount / 10);
        int remainder = (int) (readAmount % 10);


        try {
            // seed Random instance with file size divided by 1000
            long seed = Files.size(path) / 1000;
            rand = new Random(seed);

            // open the file channel for reading
            try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ)) {
                long fileSize = fileChannel.size();

                for (int i = 0; i < 10; i++) {
                    // determine the random position to read from
                    long randomPosition = Math.abs(rand.nextLong()) % Math.max(fileSize - chunkSize, 1);
                    fileChannel.position(randomPosition);

                    // read from the random position into the buffer
                    buffer.clear();  // Clear buffer before each read
                    int bytesRead = fileChannel.read(buffer);
                    // Check if bytesRead is valid

                    if (bytesRead > 0) {
                        buffer.flip();  // prepare buffer for reading
                        buffer.get(result, i * chunkSize, Math.min(bytesRead, chunkSize));
                    } else if (bytesRead == -1) {
                        System.err.println("Reached end of file unexpectedly.");
                        break;
                    }

                    // handle the remainder if there is any
                    if (remainder > 0) {
                        randomPosition = Math.abs(rand.nextLong()) % Math.max(fileSize - remainder, 1);
                        fileChannel.position(randomPosition);
                        bytesRead = fileChannel.read(buffer);
                        if (bytesRead > 0) {
                            buffer.flip();
                            buffer.get(result, 10 * chunkSize, Math.min(bytesRead, remainder));
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error in randomRead, error reading file: " + path);
            e.printStackTrace();
        }

        return result;
    }

    public static List<Path> getPathList(Path path) {
        // recursively get path list from original path
        List<Path> pathList = new ArrayList<>();
        Path dsStore = Paths.get(commonPath + "/listPath/.DS_Store");

        try {
            Files.walk(path).forEach(source -> {
                if (!Files.isDirectory(source)) { // do not add directory to path list
                    if (!source.equals(dsStore)) {
                        pathList.add(source);
                    }

                }
            });
        } catch (IOException e) {
            System.err.println("Error in getting file list: " + path);
        }

        return pathList;
    }


    public static Map<Long, List<Path>> collectSameSizeFiles(Path path, long minFilterFileSize) {
        // collect paths with same size
        List<Path> pathList = getPathList(path);
        Map<Long, List<Path>> sizePathMap = new HashMap<>();

        try {
            for (Path p : pathList) {
                Long size = Files.size(p);
                List<Path> innerList;
                if (size > (Long) minFilterFileSize) { // can replace with minFilterFileSize
                    innerList = sizePathMap.get(size);
                    if (innerList == null) {
                        innerList = new ArrayList<>();
                    }

                    innerList.add(p);
                    Collections.sort(innerList);
                    sizePathMap.put(size, innerList);
                }
            }

            Iterator<Map.Entry<Long, List<Path>>> iterator = sizePathMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, List<Path>> entry = iterator.next();
                if (entry.getValue().size() == 1) {
                    // remove innerList with size 1
                    // use Iterator to prevent ConcurrentModificationException
                    iterator.remove();
                }
            }

        } catch (IOException e) {
            System.err.println("Error in getting file size: ");
        }

        return sizePathMap;
    }

    /* File Backup Related */
    public static void copyDirectory(String sourceDirectory, String destinationDirectory)
            throws IOException {
        Files.walk(Paths.get(sourceDirectory))
                .forEach(source -> {
                    // destination = destinationDirectoryLocation + File(Directory)
                    Path destination = Paths.get(destinationDirectory, source.toString()
                            .substring(sourceDirectory.length()));
                    try {
                        // copy
                        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        System.err.println("Error in copying inside copyDirectory()");
                        e.printStackTrace();
                    }
                });
    }

    public static void moveDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation)
            throws IOException {
        Path sourceDir = Paths.get(sourceDirectoryLocation);
        Path targetDir = Paths.get(destinationDirectoryLocation);
        try {
            Files.move(sourceDir, targetDir, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error occurred in moveDirectory()");
            e.printStackTrace();
        }
    }

    /* Common */
    public static byte[] fastRead(Path toReadPath) {
        FileChannel readChannel = null;
        byte[] result = null;
        // get byte array for saving result
        try {
            result = new byte[(int) Files.size(toReadPath)];
        } catch (IOException e) {
            System.err.println("Error in getting file size");
        }

        // open read channel
        try {
            readChannel = FileChannel.open(toReadPath, StandardOpenOption.READ);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // read through channel
        try {
            while (readChannel.read(buffer) >= 0) {
                buffer.flip();
                buffer.get(result);
                buffer.clear();
            }
        } catch (IOException channelReadException) {
            System.err.println(channelReadException);
            channelReadException.printStackTrace();
        }

        return result;
    }

    public static byte[] fastRead(Path toReadPath, long amountToRead) {
        FileChannel readChannel = null;
        byte[] result = null;
        // get byte array for saving result
        try {
            // WARNING: File size can overflow int value
            // but we cannot use long for array
            long fileSize = (int) Files.size(toReadPath);
            if (fileSize < amountToRead) {
                result = new byte[(int) fileSize];
            } else {
                result = new byte[(int) amountToRead];
            }
        } catch (IOException e) {
            System.err.println("Error in reading file size");
        }


        // open read channel
        try {
            readChannel = FileChannel.open(toReadPath, StandardOpenOption.READ);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // read through channel
        try {
            while (readChannel.read(buffer) >= 0) {
                buffer.flip();
                buffer.get(result);
                buffer.clear();
            }
        } catch (IOException channelReadException) {
            System.err.println("Error in fastRead read: " + channelReadException);
            channelReadException.printStackTrace();
        }

        return result;
    }

    public static void fastCopy(Path fromPath, Path toPath) {
        FileChannel readChannel = null;
        FileChannel writeChannel = null;
        try {
            readChannel = FileChannel.open(fromPath, StandardOpenOption.READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // create file if not exists
        if (Files.notExists(toPath)) {
            try {
                Files.createFile(toPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // write to a file
        try {
            writeChannel = FileChannel.open(toPath, StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // read and write(copy)
        try {
            while (readChannel.read(buffer) >= 0) {
                buffer.flip();
                try {
                    writeChannel.write(buffer);
                } catch (IOException writeException) {
                    System.err.println("Error in writing buffer");
                    writeException.printStackTrace();
                }
                buffer.clear();
            }
        } catch (IOException readException) {
            System.err.println("Error in reading buffer");
        }
    }


}
