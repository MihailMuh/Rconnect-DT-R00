package com.mihalis.dtr00.utils;

public interface Subscriber<T> {
    void onEvent(T value);
}
