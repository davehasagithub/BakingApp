package com.example.android.baking;

import com.example.android.baking.ui.main.MainActivity;
import com.example.android.baking.ui.main.RecipeAdapter;
import com.example.android.baking.utilities.EspressoIdlingResource;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.android.baking.StringContainsIgnoringCase.containsStringIgnoringCase;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

// https://stackoverflow.com/a/53840200
// https://developer.android.com/training/testing/espresso/lists#recycler-view-list-items
@RunWith(AndroidJUnit4.class)
public class RecipeNavigationTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @BeforeClass
    public static void initIdlingResource() {
        EspressoIdlingResource.init();
    }

    @Before
    public void registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
    }

    @After
    public void unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
    }

    @Test
    public void myTest() {
        //onView(withId(R.id.recyclerview_recipes)).perform(actionOnItem(hasDescendant(withText(RecipeAdapter.RECIPE_NAME_FOR_TEST)), click()));

        onView(withText(RecipeAdapter.RECIPE_NAME_FOR_TEST)).perform(click());
        onView(withText("Ingredients")).perform(click());

        onView(withText(containsString("BITTERSWEET CHOCOLATE"))).check(matches(isDisplayed()));

        onView(withId(R.id.recyclerview_detail_items)).perform(scrollToPosition(10));
        onView(withTagValue(is("position10"))).check(matches(withText(containsStringIgnoringCase("cut and serve"))));
    }
}