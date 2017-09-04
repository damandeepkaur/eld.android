package app.bsmuniversal.com.locators;

import android.view.View;

import org.hamcrest.Matcher;

import app.bsmuniversal.com.R;
import app.bsmuniversal.com.utils.SystemUtil;

import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Provide views for navigation drawer
 */
public class DrawerLocators {

    public static Matcher<View> navigation_drawer = withId(R.id.navigation_drawer);
    public static Matcher<View> navigation_view = withId(R.id.navigation_view);
    public static Matcher<View> box_id = withId(R.id.box_id);

    public static int nav_logout_item = R.id.nav_logout;

    public static String not_in_vehicle_text = SystemUtil.resourceToString(R.string.select_asset_not_in_vehicle);

}
