package app.bsmuniversal.com.base;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;

import com.bsmwireless.screens.login.LoginActivity;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;

import app.bsmuniversal.com.locators.DrawerLocators;
import app.bsmuniversal.com.locators.LoginLocators;
import app.bsmuniversal.com.locators.SelectAssetLocators;
import app.bsmuniversal.com.pages.SelectAssetPage;
import app.bsmuniversal.com.utils.Users;

/**
 * Base class for general purpose tests in case when uset should be logged in once for all test
 */
public class BaseTestCaseLoginOnce extends BaseTestClass {

    private static boolean loggedIn;

    private static Users.User activeUser;

    @Rule
    public ActivityTestRule<LoginActivity> loginActivityTestRule =
            new IntentsTestRule<>(LoginActivity.class);

    public BaseTestCaseLoginOnce(Users.User user) {
        activeUser = user;
    }

    @BeforeClass
    public static void prepare() {
        loggedIn = false;
    }

    @Before
    public void setUp() throws Exception {
        if (!loggedIn) {
            login(activeUser, true);
            wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.not_in_vehicle_button);
            loggedIn = true;
            SelectAssetPage.go_to_home_screen();
        }
    }

    @AfterClass
    public static void logoutAfter() {
        if (loggedIn) {
            if (getCurrentActivity() == null) {
                launchActivity();
            }
            logout();
            wait_for_view(REQUEST_TIMEOUT, LoginLocators.execute_login);
            loggedIn = false;
        }
        activeUser = null;
    }

    private static void launchActivity() {
        ActivityTestRule<LoginActivity> loginActivityTestRule = new ActivityTestRule<>(LoginActivity.class);
        loginActivityTestRule.launchActivity(new Intent());
        wait_for_view(REQUEST_TIMEOUT, DrawerLocators.navigation_drawer);
    }
}
