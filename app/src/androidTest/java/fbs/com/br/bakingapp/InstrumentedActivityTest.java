package fbs.com.br.bakingapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class InstrumentedActivityTest {

        @Rule
        public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);


        private IdlingResource mIdlingResource;

        @Before
        public void registerIdlingResource() {
            mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
            Espresso.registerIdlingResources(mIdlingResource);
        }

        @Test
        public void checkTextMainActivity() {
            onView(ViewMatchers.withId(R.id.recipe_recycler)).perform(RecyclerViewActions.scrollToPosition(1));
            onView(withText("Brownies")).check(matches(isDisplayed()));
        }

        @Test
        public void checkPlayerViewIsVisible() {
            onView(ViewMatchers.withId(R.id.recipe_recycler)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
            onView(ViewMatchers.withId(R.id.recipe_detail_recycler)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
            onView(withId(R.id.playerView)).check(matches(isDisplayed()));
        }

        @After
        public void unregisterIdlingResource() {
            if (mIdlingResource != null) {
                Espresso.unregisterIdlingResources(mIdlingResource);
            }
        }

}
