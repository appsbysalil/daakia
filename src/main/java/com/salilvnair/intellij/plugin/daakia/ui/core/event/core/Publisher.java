package com.salilvnair.intellij.plugin.daakia.ui.core.event.core;

import com.intellij.openapi.application.ApplicationManager;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Publisher<T> {
    private final List<WeakReference<Subscriber<T>>> subscribers = new CopyOnWriteArrayList<>();

    // Subscribe method remains the same
    public void subscribe(Subscriber<T> subscriber) {
        subscribers.add(new WeakReference<>(subscriber));
    }

    // Improved: run each subscriber asynchronously
    public void publish(T data) {
        for (WeakReference<Subscriber<T>> ref : subscribers) {
            Subscriber<T> subscriber = ref.get();
            if (subscriber != null) {
                ApplicationManager.getApplication().executeOnPooledThread(() -> {
                    try {
                        subscriber.onNext(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                subscribers.remove(ref);
            }
        }
    }
}
