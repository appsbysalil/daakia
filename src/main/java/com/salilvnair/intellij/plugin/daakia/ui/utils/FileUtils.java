package com.salilvnair.intellij.plugin.daakia.ui.utils;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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


    public static void saveResponseAsFile(ResponseEntity<?> responseEntity) {
        try {
            // Fetch the response using RestTemplate
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                // Get the Content-Type header
                String contentType = responseEntity.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);

                // Create a file chooser for save location
                FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false, false, false, false);
                descriptor.setTitle("Choose Save Location");

                VirtualFile file = FileChooser.chooseFile(descriptor, null, null);
                if (file != null) {
                    // Default save path
                    Path savePath = Path.of(file.getPath(), "output");

                    // Process response based on Content-Type
                    if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equalsIgnoreCase(contentType)) {
                        savePath = savePath.resolveSibling("output.xlsx");
                        saveByteArrayToFile((byte[]) responseEntity.getBody(), savePath);
                    }
                    else if ("application/octet-stream".equalsIgnoreCase(contentType)) {
                        String contentDisposition = responseEntity.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
                        String defaultFileName = "output.bin";
                        if (contentDisposition != null && contentDisposition.contains("filename=")) {
                            defaultFileName = contentDisposition
                                    .substring(contentDisposition.indexOf("filename=") + 9)
                                    .replaceAll("\"", "");

                        }
                        savePath = Path.of(file.getPath(), defaultFileName);
                        saveByteArrayToFile((byte[]) responseEntity.getBody(), savePath);
                    }
                    else if (contentType != null && contentType.startsWith("text")) {
                        savePath = savePath.resolveSibling("output.txt");
                        saveTextToFile(new String((byte[]) responseEntity.getBody(), StandardCharsets.UTF_8), savePath);
                    }
                    else {
                        Messages.showErrorDialog("Unsupported content type: " + contentType, "Error");
                        return;
                    }
                    Messages.showInfoMessage("File saved successfully to: " + savePath, "Success");
                }
                else {
                    Messages.showInfoMessage("Save operation was canceled.", "Info");
                }
            }
            else {
                Messages.showErrorDialog("Failed to download the data from the server.", "Error");
            }
        }
        catch (Exception e) {
            Messages.showErrorDialog("An error occurred: " + e.getMessage(), "Error");
        }
    }

    private static void saveByteArrayToFile(byte[] data, Path path) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
            fos.write(data);
        }
    }

    private static void saveTextToFile(String text, Path path) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
            fos.write(text.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static String userHomePath() {
        return System.getProperty("user.home");
    }
}
