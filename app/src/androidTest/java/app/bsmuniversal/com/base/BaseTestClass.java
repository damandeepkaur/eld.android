package app.bsmuniversal.com.base;

import android.content.Context;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.NoMatchingViewException;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import junit.framework.AssertionFailedError;

import org.hamcrest.Matcher;
import org.junit.Assert;

import app.bsmuniversal.com.locators.DrawerLocators;
import app.bsmuniversal.com.locators.LoginLocators;
import app.bsmuniversal.com.pages.CommonPage;
import app.bsmuniversal.com.pages.DrawerPage;
import app.bsmuniversal.com.utils.SystemUtil;
import app.bsmuniversal.com.utils.Users;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.times;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

/**
 * Base class for all types of tests.
 */
public abstract class BaseTestClass {

    protected static final int REQUEST_TIMEOUT = 5;

    protected void login(Users.User user, boolean rememberMe) {
        CommonPage.enter_on_view(LoginLocators.username, user.getUsername());
        CommonPage.enter_on_view(LoginLocators.password, user.getPassword());
        CommonPage.enter_on_view(LoginLocators.domain, user.getDomain(), false, true);
        CommonPage.set_checked(LoginLocators.switch_button, rememberMe);
        CommonPage.perform_click(LoginLocators.execute_login);
    }

    protected void logout() {
        DrawerPage.open_navigation_drawer();
        assert_navigation_drawer_opened(DrawerLocators.navigation_drawer, true);
        DrawerPage.click_on_navigation_item(DrawerLocators.nav_logout_item);
    }

    protected void assert_snack_bar_with_message_displayed(Matcher<View> view, String message) {
        wait_for_view(REQUEST_TIMEOUT, view);
        assert_something_displayed(view, true);
        assert_text_displayed(message, true);
    }

    public static void assert_not_exist(Matcher<View> view) {
        onView(view).check(doesNotExist());
    }

    public static void assert_activity_shown(String className, int count) {
        intended(hasComponent(className), times(count));
    }

    public static void assert_something_displayed(Matcher<View> view, boolean displayed) {
        onView(view).check(displayed ? matches(isDisplayed()) : matches(not(isDisplayed())));
    }

    public static void assert_navigation_drawer_opened(Matcher<View> view, boolean opened) {
        onView(view).check(opened ? matches(isOpen()) : matches(not(isOpen())));
    }

    public static void assert_text_displayed(String text, boolean displayed) {
        assert_something_displayed(withText(text), displayed);
    }

    public static void assert_text_not_exist(String text) {
        onView(withText(text)).check(doesNotExist());
    }

    public static void assert_matches_text(Matcher<View> view, String text, boolean displayed) {
        onView(view).check(displayed ? matches(withText(text)) : matches(not(withText(text))));
    }

    public static void assert_enabled(Matcher<View> view, boolean enabled) {
        onView(view).check(enabled ? matches(isEnabled()) : matches(not(isEnabled())));
    }

    public static void assert_checked(Matcher<View> view, boolean checked) {
        onView(view).check(checked ? matches(isChecked()) : matches(isNotChecked()));
    }

    public static void close_keyboard_and_wait_for_view(Matcher<View> viewMatcher) {
        Espresso.closeSoftKeyboard();
        wait_for_view(REQUEST_TIMEOUT, viewMatcher);
    }

    public static void assert_contains_text(Matcher<View> view, String subString) {
        onView(view).check(matches(withText(containsString(subString))));
    }

    public static void assert_list_contains_text(Matcher<View> view, String subString) {
        onView(view).check(matches(hasDescendant(withText(containsString(subString)))));
    }

    public static void assert_keyboard(boolean visible) {
        InputMethodManager inputMethodManager = (InputMethodManager)
                SystemUtil.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (visible) {
            Assert.assertTrue(inputMethodManager.isAcceptingText());
        } else {
            Assert.assertFalse(inputMethodManager.isAcceptingText());
        }
    }

    /**
     * A helper method that provides additional time to UI elements to be loaded before fails.
     *
     * @param timer       time to wait for UI elements in seconds
     * @param viewMatcher elements to be loaded
     * @return True if waited view is displayed
     */
    public static synchronized boolean wait_for_view(int timer, Matcher<View> viewMatcher) {
        System.out.println("ENTERED WAIT " + viewMatcher.toString());

        long startTime = System.currentTimeMillis();
        long newTime = System.currentTimeMillis();
        while (newTime - startTime < timer * 1000) {
            try {
                Thread.sleep(1000);
                onView(viewMatcher).check(matches(isCompletelyDisplayed()));
                System.out.println("SUCCESS - EXITING WAIT " + viewMatcher.toString());
                return true;
            } catch (NoMatchingViewException | AssertionFailedError ignored) {
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            newTime = System.currentTimeMillis();
            System.out.println("WAITING FOR " + viewMatcher.toString());
        }
        return false;
    }
}
