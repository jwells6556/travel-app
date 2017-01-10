package com.justinwells.mytravelproject.Activities;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.justinwells.mytravelproject.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityTest() {
        ViewInteraction cardView = onView(
                allOf(withId(R.id.button),

                        isDisplayed()));
        cardView.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.direct_search_location), isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.direct_search_location), isDisplayed()));
        appCompatEditText2.perform(replaceText("phoenix"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.continue_direct_search), withText("search"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.destination_title), withText("Phoenix"),

                        isDisplayed()));
        textView.check(matches(withText("Phoenix")));

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Navigate up"),

                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction frameLayout = onView(
                allOf(withId(R.id.activity_main),

                        isDisplayed()));
        frameLayout.check(matches(isDisplayed()));

        ViewInteraction cardView2 = onView(
                allOf(withId(R.id.no_destination_search_button),

                        isDisplayed()));
        cardView2.perform(click());

        ViewInteraction linearLayout = onView(

                        isDisplayed());
        linearLayout.check(matches(isDisplayed()));

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.random_search_budget), isDisplayed()));
        appCompatEditText3.perform(click());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.random_search_budget), isDisplayed()));
        appCompatEditText4.perform(replaceText("300"), closeSoftKeyboard());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.random_search_departure), isDisplayed()));
        appCompatEditText5.perform(replaceText("phl"), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.continue_random_search), withText("search"), isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.flight_search_results),

                        isDisplayed()));
        recyclerView.check(matches(isDisplayed()));

        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.flight_search_results),

                        isDisplayed()));
        recyclerView2.perform(actionOnItemAtPosition(7, click()));

        ViewInteraction linearLayout2 = onView(
                allOf(withId(R.id.activity_detail),

                        isDisplayed()));
        linearLayout2.check(matches(isDisplayed()));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
