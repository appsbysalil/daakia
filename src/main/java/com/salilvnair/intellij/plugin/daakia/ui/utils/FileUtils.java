package com.salilvnair.intellij.plugin.daakia.ui.utils;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.TransactionGuard;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.fileChooser.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.util.ui.UIUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

    public static void saveResponseAsFile(Project project, ResponseEntity<?> raw) {
        if (!(raw.getBody() instanceof byte[] || raw.getBody() instanceof String)) {
            notify(project, "Unsupported response body type", NotificationType.ERROR);
            return;
        }
        if (!raw.getStatusCode().is2xxSuccessful()) {
            notify(project, "Failed to download data from the server.", NotificationType.ERROR);
            return;
        }

        byte[] bytes = (raw.getBody() instanceof byte[])
                ? (byte[]) raw.getBody()
                : ((String) raw.getBody()).getBytes(StandardCharsets.UTF_8);

        String contentType = raw.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        String defaultName = resolveDefaultFilename(raw.getHeaders(), contentType);

        UIUtil.invokeLaterIfNeeded(() -> {
            FileSaverDescriptor descriptor = new FileSaverDescriptor("Save Response", "Choose where to save the file");
            FileSaverDialog dialog = FileChooserFactory.getInstance().createSaveFileDialog(descriptor, project);

            VirtualFileWrapper wrapper = dialog.save((VirtualFile) null, defaultName);
            if (wrapper == null) {
                notify(project, "Save operation was canceled.", NotificationType.INFORMATION);
                return;
            }

            Path path = wrapper.getFile().toPath();

            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                try {
                    Path parent = path.getParent();
                    if (parent != null) {
                        Files.createDirectories(parent);
                    }
                    Files.write(path, bytes);

                    ApplicationManager.getApplication().invokeLater(() -> {
                        TransactionGuard.getInstance().submitTransaction(project, () ->
                                WriteAction.run(() -> {
                                    VirtualFile vFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(path.toFile());
                                    if (vFile != null) {
                                        vFile.refresh(false, false);
                                    }
                                }));
                        notify(project, "File saved to: " + path, NotificationType.INFORMATION);
                    }, ModalityState.NON_MODAL);
                } catch (Exception e) {
                    ApplicationManager.getApplication().invokeLater(() ->
                            notify(project, "Error saving file: " + e.getMessage(), NotificationType.ERROR), ModalityState.NON_MODAL);
                }
            });
        });
    }

    public static void saveResponseAsFile2(Project project, ResponseEntity<?> raw) {
        // Validate
        if (!(raw.getBody() instanceof byte[] || raw.getBody() instanceof String)) {
            notify(project, "Unsupported response body type", NotificationType.ERROR);
            return;
        }
        if (!raw.getStatusCode().is2xxSuccessful()) {
            notify(project, "Failed to download data from the server.", NotificationType.ERROR);
            return;
        }

        // Convert body to bytes
        byte[] bytes = (raw.getBody() instanceof byte[])
                ? (byte[]) raw.getBody()
                : ((String) raw.getBody()).getBytes(StandardCharsets.UTF_8);

        String contentType = raw.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        String defaultName = resolveDefaultFilename(raw.getHeaders(), contentType);

        // Run chooser on EDT
        UIUtil.invokeLaterIfNeeded(() -> {
            FileSaverDescriptor descriptor = new FileSaverDescriptor("Save Response", "Choose where to save the file");
            FileSaverDialog dialog = FileChooserFactory.getInstance().createSaveFileDialog(descriptor, project);

            // Explicitly call VirtualFile overload
            VirtualFileWrapper wrapper = dialog.save((VirtualFile) null, defaultName);
            if (wrapper == null) {
                notify(project, "Save operation was canceled.", NotificationType.INFORMATION);
                return;
            }

            Path path = wrapper.getFile().toPath();

            // Write file on background thread
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                try {
                    Files.createDirectories(path.getParent());
                    Files.write(path, bytes);

                    // Refresh VFS safely
                    ApplicationManager.getApplication().invokeLaterOnWriteThread(() -> {
                        ApplicationManager.getApplication().runWriteAction(() -> {
                            var vFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(path.toFile());
                            if (vFile != null) {
                                vFile.refresh(false, false);
                            }
                        });
                    }, ModalityState.nonModal());

                    // Notify success
                    UIUtil.invokeLaterIfNeeded(() ->
                            notify(project, "File saved to: " + path, NotificationType.INFORMATION));
                } catch (Exception e) {
                    UIUtil.invokeLaterIfNeeded(() ->
                            notify(project, "Error saving file: " + e.getMessage(), NotificationType.ERROR));
                }
            });
        });
    }

// === helpers ===

    private static String resolveDefaultFilename(HttpHeaders headers, String contentType) {
        String cd = headers.getFirst(HttpHeaders.CONTENT_DISPOSITION);
        if (cd != null && cd.contains("filename=")) {
            String name = cd.substring(cd.indexOf("filename=") + 9).replaceAll("\"", "");
            if (!name.isBlank()) return name;
        }
        if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equalsIgnoreCase(contentType)) {
            return "output.xlsx";
        }
        if (contentType != null && contentType.startsWith("text")) {
            return "output.txt";
        }
        return "output.bin";
    }

    private static void notify(Project project, String message, NotificationType type) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("Daakia Notifications") // must match plugin.xml id
                .createNotification(message, type)
                .notify(project);
    }


    public static void saveResponseAsFile1(ResponseEntity<?> responseEntity) {
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
