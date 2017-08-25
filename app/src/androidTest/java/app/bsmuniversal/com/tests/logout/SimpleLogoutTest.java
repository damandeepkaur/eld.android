package app.bsmuniversal.com.tests.logout;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;

import com.bsmwireless.screens.login.LoginActivity;
import com.bsmwireless.screens.selectasset.SelectAssetActivity;

import org.junit.Rule;
import org.junit.Test;

import app.bsmuniversal.com.R;
import app.bsmuniversal.com.base.BaseTestClass;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.VerificationModes.times;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * AT class to verify simple logout implementation
 */
public class SimpleLogoutTest extends BaseTestClass {

    @Rule
    public ActivityTestRule<LoginActivity> mLoginActivityTestRule = new IntentsTestRule<>(LoginActivity.class);

    @Test
    public void test_logout() throws InterruptedException {
        fillLoginDataAndDoLogin("mera2", "pass789", "mera", true);
        onView(withId(R.id.select_asset_not_in_vehicle_button)).perform(click());
        doLogout();
        intended(hasComponent(LoginActivity.class.getName()));
    }

    @Test
    public void test_logoutUncheckedRememberMe() throws InterruptedException {
        fillLoginDataAndDoLogin("mera2", "pass789", "mera", false);
        onView(withId(R.id.select_asset_not_in_vehicle_button)).perform(click());
        doLogout();
        intended(hasComponent(LoginActivity.class.getName()));
        onView(withText("mera2")).check(doesNotExist());
        onView(withText("mera")).check(doesNotExist());
        onView(withId(R.id.switchButton)).check(matches(isChecked()));
    }

    @Test
    public void test_logoutAndLogin() throws InterruptedException {
        fillLoginDataAndDoLogin("mera2", "pass789", "mera", false);
        onView(withId(R.id.select_asset_not_in_vehicle_button)).perform(click());
        doLogout();
        fillLoginDataAndDoLogin("mera2", "pass789", "mera", false);
        intended(hasComponent(SelectAssetActivity.class.getName()), times(2));
        onView(withId(R.id.select_asset_not_in_vehicle_button)).perform(click());
        doLogout();
    }

}
