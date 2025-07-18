package com.salilvnair.intellij.plugin.daakia.ui.core.event.core;

import com.intellij.openapi.application.ApplicationManager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Publisher<T> {
    private final List<Subscriber<T>> subscribers = new CopyOnWriteArrayList<>();

    // Subscribe method remains the same
    public void subscribe(Subscriber<T> subscriber) {
        subscribers.add(subscriber);
    }

    // Improved: run each subscriber asynchronously
    public void publish(T data) {
        for (Subscriber<T> subscriber : subscribers) {
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                try {
                    subscriber.onNext(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
