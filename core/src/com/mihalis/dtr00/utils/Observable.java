package com.mihalis.dtr00.utils;

import com.badlogic.gdx.utils.Array;

public class Observable<T> {
    private final Array<Subscriber<T>> subscribers = new Array<>(false, 16);

    public void subscribe(Subscriber<T> subscriber) {
        subscribers.add(subscriber);
    }

    public void runEvent(T value) {
        for (Subscriber<T> subscriber : subscribers) {
            subscriber.onEvent(value);
        }
    }
}
