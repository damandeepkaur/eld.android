package app.bsmuniversal.com.tests.selectasset;

import com.bsmwireless.screens.selectasset.SelectAssetActivity;

import org.junit.Test;

import app.bsmuniversal.com.base.BaseTestCaseLoginOnce;
import app.bsmuniversal.com.locators.DrawerLocators;
import app.bsmuniversal.com.locators.SelectAssetLocators;
import app.bsmuniversal.com.pages.DrawerPage;
import app.bsmuniversal.com.pages.SelectAssetPage;
import app.bsmuniversal.com.utils.Users;

/**
 * TA class to verify select asset screen implementation when selected from menu
 */
public class SelectAssetFromMenuTest extends BaseTestCaseLoginOnce {

    public SelectAssetFromMenuTest() {
        super(Users.getUserOne());
    }

    @Test
    public void test_selectAssetScreen() {
        DrawerPage.open_navigation_drawer();
        DrawerPage.click_on_box_label();
        assert_activity_shown(SelectAssetActivity.class.getName(), 2);
    }

    @Test
    public void test_selectNotInVehicleAssetScreen() {
        DrawerPage.open_navigation_drawer();
        DrawerPage.click_on_box_label();
        close_keyboard_and_wait_for_view(SelectAssetLocators.not_in_vehicle_button);
        SelectAssetPage.click_on_not_in_vehicle();
        DrawerPage.open_navigation_drawer();
        assert_text_displayed(DrawerLocators.not_in_vehicle_text, true);
    }

}
