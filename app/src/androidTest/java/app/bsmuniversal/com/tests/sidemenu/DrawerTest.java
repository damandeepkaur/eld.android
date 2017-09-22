package app.bsmuniversal.com.tests.sidemenu;

import com.bsmwireless.screens.login.LoginActivity;
import com.bsmwireless.screens.navigation.NavigationActivity;

import org.junit.Test;

import app.bsmuniversal.com.base.BaseTestCaseLoginOnce;
import app.bsmuniversal.com.locators.CommonLocators;
import app.bsmuniversal.com.locators.DrawerLocators;
import app.bsmuniversal.com.locators.HomeLocators;
import app.bsmuniversal.com.locators.LoginLocators;
import app.bsmuniversal.com.locators.SelectAssetLocators;
import app.bsmuniversal.com.locators.SettingsLocators;
import app.bsmuniversal.com.pages.CommonPage;
import app.bsmuniversal.com.pages.DrawerPage;
import app.bsmuniversal.com.pages.SelectAssetPage;
import app.bsmuniversal.com.utils.Users;

/**
 * TA class to verify Side Menu implementation
 */
public class DrawerTest extends BaseTestCaseLoginOnce {

    public DrawerTest() {
        super(Users.getUserOne());
    }

    @Test
    public void test_validateElements() {
        DrawerPage.open_navigation_drawer();
        assert_something_displayed(DrawerLocators.box_id, true);
        assert_something_displayed(DrawerLocators.nav_home, true);
        assert_something_displayed(DrawerLocators.nav_inspector_view, true);
        assert_something_displayed(DrawerLocators.nav_help, true);
        assert_something_displayed(DrawerLocators.nav_driver_profile, true);
        assert_something_displayed(DrawerLocators.nav_settings, true);
        assert_something_displayed(DrawerLocators.nav_logout, true);
    }

    @Test
    public void test_logout() {
        DrawerPage.open_navigation_drawer();
        DrawerPage.click_on_navigation_item(DrawerLocators.nav_logout_item);
        wait_for_view(REQUEST_TIMEOUT, LoginLocators.execute_login);
        assert_activity_shown(LoginActivity.class.getName(), 1);
        login(Users.getUserOne(), true);
        close_keyboard_and_wait_for_view(SelectAssetLocators.not_in_vehicle_button);
        SelectAssetPage.go_to_home_screen();
    }

    @Test
    public void test_home() {
        DrawerPage.open_navigation_drawer();
        DrawerPage.click_on_navigation_item(DrawerLocators.nav_home_item);
        wait_for_view(REQUEST_TIMEOUT, HomeLocators.home_title);
        assert_something_displayed(HomeLocators.home_title, true);
    }

    @Test
    public void test_notInVehicle() {
        DrawerPage.open_navigation_drawer();
        DrawerPage.click_on_box_label();
        close_keyboard_and_wait_for_view(SelectAssetLocators.not_in_vehicle_button);
        SelectAssetPage.click_on_not_in_vehicle();
        DrawerPage.open_navigation_drawer();
        assert_text_displayed(DrawerLocators.not_in_vehicle_text, true);
    }

    @Test
    public void test_close() {
        DrawerPage.open_navigation_drawer();
        DrawerPage.close_navigation_drawer();
        assert_navigation_drawer_opened(DrawerLocators.navigation_drawer, false);
    }

    @Test
    public void test_appears() {
        DrawerPage.open_navigation_drawer();
        assert_navigation_drawer_opened(DrawerLocators.navigation_drawer, true);
    }

    @Test
    public void test_hide() {
        DrawerPage.open_navigation_drawer();
        DrawerPage.click_on_navigation_item(DrawerLocators.nav_settings_item);
        wait_for_view(REQUEST_TIMEOUT, SettingsLocators.settings_title);
        assert_not_exist(DrawerLocators.navigation_drawer);
        CommonPage.perform_click(CommonLocators.navigate_up);
        wait_for_view(REQUEST_TIMEOUT, HomeLocators.home_title);
    }

}