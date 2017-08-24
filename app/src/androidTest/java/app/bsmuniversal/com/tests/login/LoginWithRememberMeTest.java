package app.bsmuniversal.com.tests.login;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;

import com.bsmwireless.screens.login.LoginActivity;
import com.bsmwireless.screens.selectasset.SelectAssetActivity;

import org.junit.Rule;
import org.junit.Test;

import app.bsmuniversal.com.R;
import app.bsmuniversal.com.base.BaseTestClass;
import app.bsmuniversal.com.utils.ViewActionsUtil;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.VerificationModes.times;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * AT class to verify remember me switch implementation
 */
public class LoginWithRememberMeTest extends BaseTestClass {

    @Rule
    public ActivityTestRule<LoginActivity> mLoginActivityTestRule = new IntentsTestRule<>(LoginActivity.class);

    @Test
    public void test_firstLogin() throws InterruptedException {
        fillLoginDataAndDoLogin("mera2", "pass789", "mera", true);
        onView(withId(R.id.select_asset_not_in_vehicle_button)).perform(click());
        doLogout();
        intended(hasComponent(LoginActivity.class.getName()));
        onView(withId(R.id.username)).check(matches(withText("mera2")));
        onView(withId(R.id.domain)).check(matches(withText("mera")));
        onView(withId(R.id.switchButton)).check(matches(isChecked()));
    }

    @Test
    public void test_secondLogin() throws InterruptedException {
        fillLoginDataAndDoLogin("mera2", "pass789", "mera", true);
        onView(withId(R.id.select_asset_not_in_vehicle_button)).perform(click());
        doLogout();
        onView(withId(R.id.password)).perform(typeText("pass789"), closeSoftKeyboard());
        onView(withId(R.id.execute_login)).perform(click());
        sleep(2);
        intended(hasComponent(SelectAssetActivity.class.getName()), times(2));
        onView(withId(R.id.select_asset_not_in_vehicle_button)).perform(click());
        doLogout();
    }

    @Test
    public void test_secondLogout() throws InterruptedException {
        fillLoginDataAndDoLogin("mera2", "pass789", "mera", true);
        onView(withId(R.id.select_asset_not_in_vehicle_button)).perform(click());
        doLogout();
        onView(withId(R.id.password)).perform(typeText("pass789"), closeSoftKeyboard());
        onView(withId(R.id.execute_login)).perform(click());
        sleep(2);
        onView(withId(R.id.select_asset_not_in_vehicle_button)).perform(click());
        doLogout();
        intended(hasComponent(LoginActivity.class.getName()), times(2));
        onView(withId(R.id.username)).check(matches(withText("mera2")));
        onView(withId(R.id.domain)).check(matches(withText("mera")));
        onView(withId(R.id.switchButton)).check(matches(isChecked()));
    }

    @Test
    public void test_unchecked() throws InterruptedException {
        fillLoginDataAndDoLogin("mera2", "pass789", "mera", true);
        onView(withId(R.id.select_asset_not_in_vehicle_button)).perform(click());
        doLogout();
        onView(withId(R.id.password)).perform(typeText("pass789"), closeSoftKeyboard());
        onView(withId(R.id.switchButton)).perform(ViewActionsUtil.setChecked(false));
        onView(withId(R.id.execute_login)).perform(click());
        sleep(2);
        onView(withId(R.id.select_asset_not_in_vehicle_button)).perform(click());
        doLogout();
        intended(hasComponent(LoginActivity.class.getName()), times(2));
        onView(withText("mera2")).check(doesNotExist());
        onView(withText("mera")).check(doesNotExist());
        onView(withId(R.id.switchButton)).check(matches(isChecked()));
    }

    @Test
    public void test_secondUser() throws InterruptedException {
        fillLoginDataAndDoLogin("mera2", "pass789", "mera", true);
        onView(withId(R.id.select_asset_not_in_vehicle_button)).perform(click());
        doLogout();
        fillLoginDataAndDoLogin("devin", "1234", "sfm", true);
        intended(hasComponent(SelectAssetActivity.class.getName()), times(2));
        onView(withId(R.id.select_asset_not_in_vehicle_button)).perform(click());
        doLogout();
    }

    @Test
    public void test_secondUserChecked() throws InterruptedException {
        fillLoginDataAndDoLogin("mera2", "pass789", "mera", true);
        onView(withId(R.id.select_asset_not_in_vehicle_button)).perform(click());
        doLogout();
        fillLoginDataAndDoLogin("devin", "1234", "sfm", true);
        onView(withId(R.id.select_asset_not_in_vehicle_button)).perform(click());
        doLogout();
        intended(hasComponent(LoginActivity.class.getName()), times(2));
        onView(withId(R.id.username)).check(matches(withText("devin")));
        onView(withId(R.id.domain)).check(matches(withText("sfm")));
        onView(withId(R.id.switchButton)).check(matches(isChecked()));
    }

    @Test
    public void test_loginWithUnchecked() throws InterruptedException {
        fillLoginDataAndDoLogin("mera2", "pass789", "mera", false);
        onView(withId(R.id.select_asset_not_in_vehicle_button)).perform(click());
        doLogout();
        intended(hasComponent(LoginActivity.class.getName()));
        onView(withText("mera2")).check(doesNotExist());
        onView(withText("mera")).check(doesNotExist());
        onView(withId(R.id.switchButton)).check(matches(isChecked()));
    }

}
