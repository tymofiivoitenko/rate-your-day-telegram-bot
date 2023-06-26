package com.tymofiivoitenko.rateyourdaybot.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {


    public static void create(String filePath, String input) throws IOException {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filePath);
            fileWriter.write(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fileWriter != null) {
                fileWriter.close();
            }
        }
    }

    public static void deleteFile(String filePath) {
        new File(filePath).delete();
    }

    public static void createDirectory(String path) {
        var directory = new File(path);
        if (directory.exists()) {
            directory.delete();
        }

        directory.mkdir();
    }
}
