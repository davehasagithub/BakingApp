package com.example.android.baking.utilities;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;

// https://github.com/googlecodelabs/android-testing/blob/master/app/src/main/java/com/example/android/testing/notes/util/EspressoIdlingResource.java
// also considered this approach: https://github.com/googlesamples/android-testing/blob/master/ui/espresso/IdlingResourceSample/app/src/main/java/com/example/android/testing/espresso/IdlingResourceSample/IdlingResource/SimpleIdlingResource.java
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
}
