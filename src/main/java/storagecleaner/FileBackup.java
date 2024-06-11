package storagecleaner;

import java.io.IOException;

import static util.FileUtility.copyDirectory;
import static util.FileUtility.moveDirectory;


public class FileBackup {
    // feature 3: backup file
    public static void backupFiles(String sourceDirectory, String cloudDirectory, boolean move) throws IOException {
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
}
