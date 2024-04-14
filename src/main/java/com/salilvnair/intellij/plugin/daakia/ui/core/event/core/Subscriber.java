package com.salilvnair.intellij.plugin.daakia.ui.core.event.core;

public interface Subscriber<T> {
    void onNext(T data);
}