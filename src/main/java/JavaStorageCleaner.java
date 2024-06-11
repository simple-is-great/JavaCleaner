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
import java.util.List;
import java.util.Random;

public class JavaStorageCleaner {
    // TODO: think when to initialize this buffer
    private static final int bufferSize = 1000 * 1000;
    private static final ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);

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

    private static FileTime localDateToFileTime(LocalDate localDate) {
        // LocalDate -> LocalDateTime -> Instant -> FileTime
        LocalDateTime localDateTime = localDate.atStartOfDay();
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return FileTime.from(instant);
    }

    public static void syncToCloud(String sourceDirectory, String cloudDirectory, boolean move) throws IOException {
        if (move) { // move
            try {
                moveDirectory(sourceDirectory, cloudDirectory);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        } else { // copy
            try {
                copyDirectory(sourceDirectory, cloudDirectory);
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

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
            // beware: File size can overflow int value
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
}