package app.bsmuniversal.com.base;

import app.bsmuniversal.com.R;
import app.bsmuniversal.com.utils.ViewActionsUtil;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.contrib.NavigationViewActions.navigateTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Base class for all types of tests.
 */
public class BaseTestClass {

    protected void sleep(int seconds) throws InterruptedException {
        Thread.sleep(seconds * 1000);
    }

    protected void fillLoginDataAndDoLogin(String username, String password, String domain, boolean rememberMe) throws InterruptedException {
        onView(withId(R.id.username)).perform(clearText(), typeText(username));
        onView(withId(R.id.password)).perform(clearText(), typeText(password));
        onView(withId(R.id.domain)).perform(clearText(), typeText(domain), closeSoftKeyboard());
        onView(withId(R.id.switchButton)).perform(ViewActionsUtil.setChecked(rememberMe));
        onView(withId(R.id.execute_login)).perform(click());
        sleep(2);
    }

    protected void doLogout() throws InterruptedException {
        onView(withId(R.id.navigation_drawer)).perform(open());
        onView(withId(R.id.navigation_drawer)).check(matches(isOpen()));
        onView(withId(R.id.navigation_view)).perform(navigateTo(R.id.nav_logout));
        sleep(2);
    }

    protected void checkIfSnackBarWithMessageIsDisplayed(int snackBarId, String message) {
        onView(withId(snackBarId)).check(matches(isDisplayed()));
        onView(withText(message)).check(matches(isDisplayed()));
    }
}
