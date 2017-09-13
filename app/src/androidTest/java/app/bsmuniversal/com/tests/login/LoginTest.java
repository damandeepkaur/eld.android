package app.bsmuniversal.com.tests.login;

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
 * Automation test class to verify Login implementation
 */
public class LoginTest extends BaseTestClass {

    @Rule
    public ActivityTestRule<LoginActivity> loginActivityTestRule = new IntentsTestRule<>(LoginActivity.class);

    @Test
    public void test_firstLoginSuccessful() {
        login(Users.getUserOne(), false);
        wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.not_in_vehicle_button);
        assert_activity_shown(SelectAssetActivity.class.getName(), 1);
        doLogout();
    }

    @Test
    public void test_secondLoginSuccessful() {
        login(Users.getUserOne(), false);
        wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.not_in_vehicle_button);
        doLogout();
        login(Users.getUserOne(), true);
        wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.not_in_vehicle_button);
        assert_activity_shown(SelectAssetActivity.class.getName(), 2);
        doLogout();
    }

    private void doLogout() {
        SelectAssetPage.go_to_home_screen();
        logout();
        wait_for_view(REQUEST_TIMEOUT, LoginLocators.execute_login);
    }

    @Test
    public void test_wrongUsername() {
        login(Users.getUserWrongUsername(), false);
        assert_snack_bar_with_message_displayed(LoginLocators.login_snackbar, LoginLocators.error_401_message);
    }

    @Test
    public void test_wrongPassword() {
        login(Users.getUserWrongPassword(), false);
        assert_snack_bar_with_message_displayed(LoginLocators.login_snackbar, LoginLocators.error_401_message);
    }

    @Test
    public void test_wrongCompany() {
        login(Users.getUserWrongCompany(), false);
        assert_snack_bar_with_message_displayed(LoginLocators.login_snackbar, LoginLocators.error_401_message);
    }

    @Test
    public void test_emptyUsername() {
        login(Users.getUserEmptyUsername(), false);
        assert_snack_bar_with_message_displayed(LoginLocators.login_snackbar, LoginLocators.error_empty_username);
    }

    @Test
    public void test_emptyPassword() {
        login(Users.getUserEmptyPassword(), false);
        assert_snack_bar_with_message_displayed(LoginLocators.login_snackbar, LoginLocators.error_empty_password);
    }

    @Test
    public void test_emptyCompany() {
        login(Users.getUserEmptyCompany(), false);
        assert_snack_bar_with_message_displayed(LoginLocators.login_snackbar, LoginLocators.error_empty_domain);
    }

    @Test
    public void test_emptyFields() {
        login(Users.getEmptyUser(), false);
        assert_snack_bar_with_message_displayed(LoginLocators.login_snackbar, LoginLocators.error_empty_username);
    }

}