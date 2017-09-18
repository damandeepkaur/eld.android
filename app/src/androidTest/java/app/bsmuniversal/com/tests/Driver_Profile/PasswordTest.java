package app.bsmuniversal.com.tests.Driver_Profile;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;

import com.bsmwireless.screens.login.LoginActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import app.bsmuniversal.com.base.BaseTestClass;
import app.bsmuniversal.com.locators.DrawerLocators;
import app.bsmuniversal.com.locators.LoginLocators;
import app.bsmuniversal.com.locators.SelectAssetLocators;
import app.bsmuniversal.com.pages.DrawerPage;
import app.bsmuniversal.com.pages.DriverProfilePage;
import app.bsmuniversal.com.pages.SelectAssetPage;
import app.bsmuniversal.com.utils.Users;

/**
 * Automation test class to verify Password
 */
public class PasswordTest extends BaseTestClass {
    @Rule
    public ActivityTestRule<LoginActivity> mLoginActivityTestRule =
            new IntentsTestRule<>(LoginActivity.class);

    @Before
    public void setUp() throws Exception {
        login(Users.getUserOne(), false);
        wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.not_in_vehicle_button);
    }

    @Test
    public void test_emptyPassword() {
        SelectAssetPage.go_to_home_screen();
        DrawerPage.open_navigation_drawer();
        assert_navigation_drawer_opened(DrawerLocators.navigation_drawer, true);
        DrawerPage.click_on_navigation_item(DrawerLocators.nav_driver_profile);
        DriverProfilePage.change_password("", "");
        DriverProfilePage.click_on_change_password_button();
    }

    @Test
    public void test_incorrectCurrentPassword() {
        SelectAssetPage.go_to_home_screen();
        DrawerPage.open_navigation_drawer();
        assert_navigation_drawer_opened(DrawerLocators.navigation_drawer, true);
        DrawerPage.click_on_navigation_item(DrawerLocators.nav_driver_profile);
        DriverProfilePage.change_password("", Users.getUserOne().getPassword());
        DriverProfilePage.click_on_change_password_button();
    }

    @Test
    public void test_changePassword() {
        SelectAssetPage.go_to_home_screen();
        DrawerPage.open_navigation_drawer();
        assert_navigation_drawer_opened(DrawerLocators.navigation_drawer, true);
        DrawerPage.click_on_navigation_item(DrawerLocators.nav_driver_profile);
        DriverProfilePage.change_password(Users.getUserOne().getPassword(), Users.getUserOne().getPassword());
        DriverProfilePage.click_on_change_password_button();
    }

    @After
    public void tearDown() throws Exception {
        DriverProfilePage.click_back_button();
        logout();
        wait_for_view(REQUEST_TIMEOUT, LoginLocators.execute_login);
    }
}
