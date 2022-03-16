package com.LiangLliu.utils.file;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class FileUtils {

    public static File save(Workbook workbook, String fileName) throws IOException {
        File file = new File(fileName);
        createParent(file.getParentFile());

        FileOutputStream out = new FileOutputStream(file);
        workbook.write(out);
        out.close();
        return file;
    }

    public static void createParent(File file) {
        File fileParent = file.getParentFile();
        if (!fileParent.exists()) {
            createParent(fileParent);
        }
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public static File create(String fileName) {
        File file = new File(fileName);
        createParent(file.getParentFile());
        return file;
    }

    public static void sortFiles(final File[] files) {
        if (files != null) {
            Arrays.sort(files, Comparator.comparingLong(File::lastModified));
        }
    }

    public static File getlLastModifiedFile(String path) {
        File folder = new File(path);
        File[] files = folder.listFiles();
        if (files != null && files.length > 0) {
            sortFiles(files);
            return files[files.length - 1];
        }
        return null;
    }

}
