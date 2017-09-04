package app.bsmuniversal.com.tests.login;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.Suppress;
import android.support.test.rule.ActivityTestRule;

import com.bsmwireless.screens.login.LoginActivity;
import com.bsmwireless.screens.selectasset.SelectAssetActivity;

import org.junit.Rule;
import org.junit.Test;

import app.bsmuniversal.com.base.BaseTestClass;
import app.bsmuniversal.com.locators.LoginLocators;
import app.bsmuniversal.com.locators.SelectAssetLocators;
import app.bsmuniversal.com.pages.LoginPage;
import app.bsmuniversal.com.pages.SelectAssetPage;
import app.bsmuniversal.com.utils.Users;

/**
 * AT class to verify remember me switch implementation
 */
@Suppress
public class LoginWithRememberMeTest extends BaseTestClass {

    @Rule
    public ActivityTestRule<LoginActivity> mLoginActivityTestRule = new IntentsTestRule<>(LoginActivity.class);

    @Test
    public void test_firstLogin() {
        login(Users.getUserOne(), true);
        wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.not_in_vehicle_button);
        doLogout();
        assert_activity_shown(LoginActivity.class.getName(), 1);
        assert_matches_text(LoginLocators.username, Users.getUserOne().getUsername(), true);
        assert_matches_text(LoginLocators.domain, Users.getUserOne().getDomain(), true);
        assert_checked(LoginLocators.switch_button, true);
    }

    @Test
    public void test_secondLogin() {
        login(Users.getUserOne(), true);
        wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.not_in_vehicle_button);
        doLogout();
        LoginPage.enter_password(Users.getUserOne().getPassword());
        LoginPage.click_on_login();
        wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.not_in_vehicle_button);
        assert_activity_shown(SelectAssetActivity.class.getName(), 2);
        doLogout();
    }

    @Test
    public void test_secondLogout() {
        login(Users.getUserOne(), true);
        wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.not_in_vehicle_button);
        doLogout();
        LoginPage.enter_password(Users.getUserOne().getPassword());
        LoginPage.click_on_login();
        wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.not_in_vehicle_button);
        doLogout();
        assert_activity_shown(LoginActivity.class.getName(), 2);
        assert_matches_text(LoginLocators.username, Users.getUserOne().getUsername(), true);
        assert_matches_text(LoginLocators.domain, Users.getUserOne().getDomain(), true);
        assert_checked(LoginLocators.switch_button, true);
    }

    @Test
    public void test_unchecked() {
        login(Users.getUserOne(), true);
        wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.not_in_vehicle_button);
        doLogout();
        LoginPage.enter_password(Users.getUserOne().getPassword());
        LoginPage.set_remember_me(false);
        LoginPage.click_on_login();
        wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.not_in_vehicle_button);
        doLogout();
        assert_activity_shown(LoginActivity.class.getName(), 2);
        assert_text_not_exist(Users.getUserOne().getUsername());
        assert_text_not_exist(Users.getUserOne().getDomain());
        assert_checked(LoginLocators.switch_button, true);
    }

    @Test
    public void test_secondUser() {
        login(Users.getUserOne(), true);
        wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.not_in_vehicle_button);
        doLogout();
        login(Users.getUserTwo(), true);
        wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.not_in_vehicle_button);
        assert_activity_shown(SelectAssetActivity.class.getName(), 2);
        doLogout();
    }

    @Test
    public void test_secondUserChecked() {
        login(Users.getUserOne(), true);
        wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.not_in_vehicle_button);
        doLogout();
        login(Users.getUserTwo(), true);
        wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.not_in_vehicle_button);
        assert_activity_shown(SelectAssetActivity.class.getName(), 2);
        doLogout();
        assert_activity_shown(LoginActivity.class.getName(), 2);
        assert_matches_text(LoginLocators.username, Users.getUserTwo().getUsername(), true);
        assert_matches_text(LoginLocators.domain, Users.getUserTwo().getDomain(), true);
        assert_checked(LoginLocators.switch_button, true);
    }

    @Test
    public void test_loginWithUnchecked() {
        login(Users.getUserOne(), false);
        wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.not_in_vehicle_button);
        doLogout();
        assert_activity_shown(LoginActivity.class.getName(), 1);
        assert_text_not_exist(Users.getUserOne().getUsername());
        assert_text_not_exist(Users.getUserOne().getDomain());
        assert_checked(LoginLocators.switch_button, true);
    }

    private void doLogout() {
        SelectAssetPage.go_to_home_screen_from_select_asset_screen();
        logout();
        wait_for_view(REQUEST_TIMEOUT, LoginLocators.execute_login);
    }
}
