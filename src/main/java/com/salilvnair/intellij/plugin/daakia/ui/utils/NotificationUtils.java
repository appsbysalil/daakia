package com.salilvnair.intellij.plugin.daakia.ui.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;

/**
 * @author Salil V Nair
 */
public class NotificationUtils {
    private NotificationUtils() {}

    public static void showNotification(String groupId, String title, String content, NotificationType type) {
        Notification notification = new Notification(groupId, title, content, type);
    }
}
