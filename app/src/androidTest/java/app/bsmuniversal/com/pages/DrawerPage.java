package app.bsmuniversal.com.pages;

import app.bsmuniversal.com.locators.DrawerLocators;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.NavigationViewActions.navigateTo;

/**
 * Provides interaction with navigation drawer
 */
public class DrawerPage {

    public static void open_navigation_drawer() {
        onView(DrawerLocators.navigation_drawer).perform(open());
    }

    public static void click_on_navigation_item(int itemId) {
        onView(DrawerLocators.navigation_view).perform(navigateTo(itemId));
    }

}
