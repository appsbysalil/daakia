package com.salilvnair.intellij.plugin.daakia.ui.core.event.type;

import java.util.ArrayList;
import java.util.List;

// Interface for subscribers
interface Subscriber<T> {
    void onNext(T data);
}

// Publisher class
class Publisher<T> {
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

public class RxJavaExample {
    public static void main(String[] args) {
        // Create a publisher
        Publisher<String> publisher = new Publisher<>();

        // Create subscribers
        Subscriber<String> subscriber1 = data -> System.out.println("Subscriber 1 received: " + data);
        Subscriber<String> subscriber2 = data -> System.out.println("Subscriber 2 received: " + data);

        // Subscribe subscribers to the publisher
        publisher.subscribe(subscriber1);
        publisher.subscribe(subscriber2);

        // Publish data
        publisher.publish("Hello, world!");
    }
}
