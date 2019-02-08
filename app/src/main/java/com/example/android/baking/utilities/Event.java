package com.example.android.baking.utilities;

// https://github.com/google/iosched/blob/master/shared/src/main/java/com/google/samples/apps/iosched/shared/result/Event.kt
public class Event<T> {
    private final T content;

    public Event(T content) {
        this.content = content;
    }

    private boolean hasBeenHandled = false;

    public T getContentIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        } else {
            hasBeenHandled = true;
            return content;
        }
    }
}