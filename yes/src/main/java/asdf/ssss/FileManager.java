package asdf.ssss;

import java.io.File;
import java.io.IOException;

public class FileManager {

    public static void initializeFiles(String... fileNames) throws IOException {
        for (String fileName : fileNames) {
            File file = new File(fileName);
            if (!file.exists()) {
                if (file.createNewFile()) {
                    System.out.println(fileName + " created successfully.");
                } else {
                    System.err.println("Failed to create " + fileName);
                }
            } else {
                System.out.println(fileName + " already exists.");
            }
        }
    }
}
