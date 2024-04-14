package com.salilvnair.intellij.plugin.daakia.ui.utils;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Salil V Nair
 */
public class FileUtils {
    private FileUtils() {}
    public static String findFileExtension(File file) {
        String name = file.getName();
        int pIndex = name.lastIndexOf(".");
        if(pIndex == -1) {
            return null;
        }
        if(pIndex == name.length() - 1) {
            return null;
        }
        return name.substring(pIndex+1);
    }

    public static void saveToFile(File file, Object[] objects) throws IOException {
        file.getParentFile().mkdirs();
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(objects);
        os.close();
    }

    public static <T> List<T> loadFromFile(File file, Class<T> tClass) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream is = new ObjectInputStream(fis);

        Object[] objects = (Object[]) is.readObject();

        List<T> data = Arrays.stream(objects).map(tClass::cast).collect(Collectors.toList());

        is.close();

        return data;
    }
}
