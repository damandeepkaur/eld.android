package app.bsmuniversal.com.pages;

import app.bsmuniversal.com.base.BaseTestClass;
import app.bsmuniversal.com.locators.SelectAssetLocators;

/**
 * Provides interaction with select asset screen
 */
public class SelectAssetPage {

    public static void go_to_home_screen() {
        BaseTestClass.close_keyboard_and_wait_for_view(SelectAssetLocators.not_in_vehicle_button);
        click_on_not_in_vehicle();
    }

    public static void enter_search_text(String searchText) {
        CommonPage.enter_on_view(SelectAssetLocators.search_input, searchText);
    }

    public static void click_on_clear_text() {
        CommonPage.perform_click(SelectAssetLocators.search_clear_text);
    }

    public static void click_on_not_in_vehicle() {
        CommonPage.perform_click(SelectAssetLocators.not_in_vehicle_button);

    }
}
