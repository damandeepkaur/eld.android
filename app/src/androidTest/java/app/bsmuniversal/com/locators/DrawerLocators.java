package app.bsmuniversal.com.locators;

import android.view.View;

import org.hamcrest.Matcher;

import app.bsmuniversal.com.R;
import app.bsmuniversal.com.utils.SystemUtil;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Provide views for navigation drawer
 */
public class DrawerLocators {

    public static Matcher<View> navigation_drawer = withId(R.id.navigation_drawer);
    public static Matcher<View> navigation_view = withId(R.id.navigation_view);

    public static Matcher<View> box_id = withId(R.id.box_id);

    public static Matcher<View> nav_home = withText(R.string.nav_home);
    public static Matcher<View> nav_inspector_view = withText(R.string.nav_inspector_view);
    public static Matcher<View> nav_help = withText(R.string.nav_help);
    public static Matcher<View> nav_driver_profile = withText(R.string.nav_driver_profile);
    public static Matcher<View> nav_settings = withText(R.string.nav_settings);
    public static Matcher<View> nav_logout = withText(R.string.nav_logout);

    public static int nav_logout_item = R.id.nav_logout;
    public static int nav_home_item = R.id.nav_home;
    public static int nav_driver_profile_item = R.id.nav_driver_profile;
    public static int nav_settings_item = R.id.nav_settings;

    public static String not_in_vehicle_text = SystemUtil.resourceToString(R.string.select_asset_not_in_vehicle);

}
