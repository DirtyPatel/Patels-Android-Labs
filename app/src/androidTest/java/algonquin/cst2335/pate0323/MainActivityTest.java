package algonquin.cst2335.pate0323;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import algonquin.cst2335.pate0323.R;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void mainActivityTest() {
        ViewInteraction appCompatEditText = onView(withId(R.id.cityField));
        appCompatEditText.perform(replaceText("12345"), closeSoftKeyboard());

        ViewInteraction materialButton = onView(withId(R.id.loginButton));
        materialButton.perform(click());

        ViewInteraction textView = onView(withId(R.id.cityVeiw));
        textView.check(matches(withText("Failed Attempt!")));
    }

    @Test
    public void passwordCheckUpperTest(){
        ViewInteraction appCompatEditText = onView(withId(R.id.cityField));
        appCompatEditText.perform(replaceText("password1234!"));

        ViewInteraction materialButton = onView(withId(R.id.loginButton));
        materialButton.perform(click());

        ViewInteraction textView = onView(withId(R.id.cityVeiw));
        textView.check(matches(withText("Failed Attempt!")));
    }

    @Test
    public void passwordCheckLowerTest(){
        ViewInteraction appCompatEditText = onView(withId(R.id.cityField));
        appCompatEditText.perform(replaceText("PASSWORD123!"));

        ViewInteraction materialButton = onView(withId(R.id.loginButton));
        materialButton.perform(click());

        ViewInteraction textView = onView(withId(R.id.cityVeiw));
        textView.check(matches(withText("Failed Attempt!")));
    }

    @Test
    public void passwordCheckNumericTest(){
        ViewInteraction appCompatEditText = onView(withId(R.id.cityField));
        appCompatEditText.perform(replaceText("Password!"));

        ViewInteraction materialButton = onView(withId(R.id.loginButton));
        materialButton.perform(click());

        ViewInteraction textView = onView(withId(R.id.cityVeiw));
        textView.check(matches(withText("Failed Attempt!")));
    }

    @Test
    public void passwordCheckSpecialTest(){
        ViewInteraction appCompatEditText = onView(withId(R.id.cityField));
        appCompatEditText.perform(replaceText("Password12"));

        ViewInteraction materialButton = onView(withId(R.id.loginButton));
        materialButton.perform(click());

        ViewInteraction textView = onView(withId(R.id.cityVeiw));
        textView.check(matches(withText("Failed Attempt!")));
    }

    @Test
    public void passwordValidationTest(){
        ViewInteraction appCompatEditText = onView(withId(R.id.cityField));
        appCompatEditText.perform(replaceText("Password1!"));

        ViewInteraction materialButton = onView(withId(R.id.loginButton));
        materialButton.perform(click());

        ViewInteraction textView = onView(withId(R.id.cityVeiw));
        textView.check(matches(withText("Success!")));
    }

}