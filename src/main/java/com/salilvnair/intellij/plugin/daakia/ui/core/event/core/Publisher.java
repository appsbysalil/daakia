package com.salilvnair.intellij.plugin.daakia.ui.core.event.core;


import java.util.ArrayList;
import java.util.List;

public class Publisher<T> {
    private final List<Subscriber<T>> subscribers = new ArrayList<>();

    // Method to subscribe a subscriber
    public void subscribe(Subscriber<T> subscriber) {
        subscribers.add(subscriber);
    }

    // Method to publish data to all subscribers
    public void publish(T data) {
        for (Subscriber<T> subscriber : subscribers) {
            subscriber.onNext(data);
        }
    }
}