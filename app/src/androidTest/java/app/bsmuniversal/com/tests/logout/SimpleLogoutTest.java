package app.bsmuniversal.com.tests.logout;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;

import com.bsmwireless.screens.login.LoginActivity;
import com.bsmwireless.screens.selectasset.SelectAssetActivity;

import org.junit.Rule;
import org.junit.Test;

import app.bsmuniversal.com.base.BaseTestClass;
import app.bsmuniversal.com.locators.LoginLocators;
import app.bsmuniversal.com.locators.SelectAssetLocators;
import app.bsmuniversal.com.pages.SelectAssetPage;
import app.bsmuniversal.com.utils.Users;

/**
 * AT class to verify simple logout implementation
 */
public class SimpleLogoutTest extends BaseTestClass {

    @Rule
    public ActivityTestRule<LoginActivity> loginActivityTestRule = new IntentsTestRule<>(LoginActivity.class);

    @Test
    public void test_logout() {
        login(Users.getUserOne(), false);
        wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.not_in_vehicle_button);
        doLogout();
        assert_activity_shown(LoginActivity.class.getName(), 1);
    }

    @Test
    public void test_logoutUncheckedRememberMe() {
        login(Users.getUserOne(), false);
        wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.not_in_vehicle_button);
        doLogout();
        assert_activity_shown(LoginActivity.class.getName(), 1);
        assert_text_not_exist(Users.getUserOne().getUsername());
        assert_text_not_exist(Users.getUserOne().getPassword());
        assert_checked(LoginLocators.switch_button, true);
    }

    @Test
    public void test_logoutAndLogin() {
        login(Users.getUserOne(), false);
        wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.not_in_vehicle_button);
        doLogout();
        login(Users.getUserOne(), false);
        wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.not_in_vehicle_button);
        assert_activity_shown(SelectAssetActivity.class.getName(), 2);
        doLogout();
    }

    private void doLogout() {
        SelectAssetPage.go_to_home_screen();
        logout();
        wait_for_view(REQUEST_TIMEOUT, LoginLocators.execute_login);
    }
}
