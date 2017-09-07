package app.bsmuniversal.com.tests.selectasset;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.Suppress;
import android.support.test.rule.ActivityTestRule;

import com.bsmwireless.screens.login.LoginActivity;
import com.bsmwireless.screens.navigation.NavigationActivity;
import com.bsmwireless.screens.selectasset.SelectAssetActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import app.bsmuniversal.com.base.BaseTestClass;
import app.bsmuniversal.com.locators.DrawerLocators;
import app.bsmuniversal.com.locators.LoginLocators;
import app.bsmuniversal.com.locators.SelectAssetLocators;
import app.bsmuniversal.com.pages.DrawerPage;
import app.bsmuniversal.com.pages.SelectAssetPage;
import app.bsmuniversal.com.utils.Users;

/**
 * TA class to verify select asset screen implementation
 */
@Suppress
public class SelectAssetTest extends BaseTestClass {

    @Rule
    public ActivityTestRule<LoginActivity> loginActivityTestRule =
            new IntentsTestRule<>(LoginActivity.class);

    @Before
    public void setUp() throws Exception {
        login(Users.getUserOne(), false);
        wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.not_in_vehicle_button);
    }

    @Test
    public void test_firstLogin() {
        assert_activity_shown(SelectAssetActivity.class.getName(), 1);
        SelectAssetPage.go_to_home_screen();
    }

    @Test
    public void test_searchBox() {
        assert_something_displayed(SelectAssetLocators.search_hint, true);
        assert_keyboard(true);
        SelectAssetPage.go_to_home_screen();
    }

    @Test
    public void test_clearText() {
        SelectAssetPage.click_on_search();
        SelectAssetPage.enter_search_text("w");
        assert_something_displayed(SelectAssetLocators.search_clear_text, true);
        SelectAssetPage.go_to_home_screen();
    }

    @Test
    public void test_lessThanThree() {
        SelectAssetPage.click_on_search();
        SelectAssetPage.enter_search_text("bc");
        wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.search_list);
        assert_text_displayed(SelectAssetLocators.less_characters_text, true);
        SelectAssetPage.go_to_home_screen();
    }

    @Test
    public void test_moreThanThree() {
        SelectAssetPage.click_on_search();
        SelectAssetPage.enter_search_text("1234");
        wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.search_list);
        assert_list_contains_text(SelectAssetLocators.search_list, "1234");
        assert_text_not_exist(SelectAssetLocators.less_characters_text);
        SelectAssetPage.go_to_home_screen();
    }

    @Test
    public void test_unExistingAsset() {
        SelectAssetPage.click_on_search();
        SelectAssetPage.enter_search_text("unexisting");
        wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.search_list);
        assert_text_displayed(SelectAssetLocators.find_nothing_text, true);
        assert_text_not_exist(SelectAssetLocators.less_characters_text);
        SelectAssetPage.go_to_home_screen();
    }

    @Test
    public void test_notInVehicleHome() {
        SelectAssetPage.go_to_home_screen();
        assert_activity_shown(NavigationActivity.class.getName(), 1);
    }

    @Test
    public void test_notInVehicleLabel() {
        SelectAssetPage.go_to_home_screen();
        DrawerPage.open_navigation_drawer();
        assert_text_displayed(DrawerLocators.not_in_vehicle_text, true);
    }

    @Test
    public void test_hintAfterClear() {
        SelectAssetPage.click_on_search();
        SelectAssetPage.enter_search_text("a");
        SelectAssetPage.click_on_clear_text();
        assert_something_displayed(SelectAssetLocators.search_hint, true);
        SelectAssetPage.go_to_home_screen();
    }

    @Test
    public void test_keyboardAfterClear() {
        SelectAssetPage.click_on_search();
        SelectAssetPage.enter_search_text("a");
        SelectAssetPage.click_on_clear_text();
        assert_keyboard(true);
        SelectAssetPage.go_to_home_screen();
    }

    @Test
    public void test_fullBox() {
        SelectAssetPage.click_on_search();
        SelectAssetPage.enter_search_text("4343455");
        wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.search_list);
        assert_list_contains_text(SelectAssetLocators.search_list, "4343455");
        assert_text_not_exist(SelectAssetLocators.less_characters_text);
        SelectAssetPage.go_to_home_screen();
    }

    @Test
    public void test_licensePlate() {
        SelectAssetPage.click_on_search();
        SelectAssetPage.enter_search_text("TD123");
        wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.search_list);
        assert_list_contains_text(SelectAssetLocators.search_list, "1234");
        assert_text_not_exist(SelectAssetLocators.less_characters_text);
        SelectAssetPage.go_to_home_screen();
    }

    @Test
    public void test_keyword() {
        SelectAssetPage.click_on_search();
        SelectAssetPage.enter_search_text("box test123");
        wait_for_view(REQUEST_TIMEOUT, SelectAssetLocators.search_list);
        assert_list_contains_text(SelectAssetLocators.search_list, "box test123");
        assert_text_not_exist(SelectAssetLocators.less_characters_text);
        SelectAssetPage.go_to_home_screen();
    }

    @After
    public void tearDown() throws Exception {
        logout();
        wait_for_view(REQUEST_TIMEOUT, LoginLocators.execute_login);
    }
}