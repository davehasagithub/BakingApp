package com.example.android.baking.utilities;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;

// https://github.com/googlesamples/android-testing/blob/master/ui/espresso/IdlingResourceSample/app/src/main/java/com/example/android/testing/espresso/IdlingResourceSample/IdlingResource/SimpleIdlingResource.java
// https://github.com/googlecodelabs/android-testing/blob/master/app/src/main/java/com/example/android/testing/notes/util/EspressoIdlingResource.java
public class EspressoIdlingResource {

    @Nullable
    private static CountingIdlingResource idlingResource;

    @VisibleForTesting
    public static void init() {
        idlingResource = new CountingIdlingResource(EspressoIdlingResource.class.getSimpleName());
    }

    public static void increment() {
        if (idlingResource != null) {
            idlingResource.increment();
        }
    }

    public static void decrement() {
        if (idlingResource != null) {
            idlingResource.decrement();
        }
    }
    public static IdlingResource getIdlingResource() {
        return idlingResource;
    }

    public static boolean isInTest() {
        return EspressoIdlingResource.getIdlingResource() != null;
    }
}

/*
class SimpleIdlingResource implements IdlingResource {

    @Nullable private volatile ResourceCallback mCallback;

    private AtomicBoolean mIsIdleNow = new AtomicBoolean(true);

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isIdleNow() {
        return mIsIdleNow.get();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        mCallback = callback;
    }

    public void setIdleState(boolean isIdleNow) {
        mIsIdleNow.set(isIdleNow);
        ResourceCallback callback = this.mCallback;
        if (isIdleNow && callback != null) {
            callback.onTransitionToIdle();
        }
    }
}*/
