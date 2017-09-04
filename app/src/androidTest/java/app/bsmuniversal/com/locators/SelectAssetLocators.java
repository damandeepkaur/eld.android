package app.bsmuniversal.com.locators;

import android.view.View;
import android.widget.EditText;

import org.hamcrest.Matcher;

import app.bsmuniversal.com.R;
import app.bsmuniversal.com.utils.SystemUtil;

import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static org.hamcrest.Matchers.allOf;

/**
 * Provide views for select asset screen
 */
public class SelectAssetLocators {

    public static Matcher<View> not_in_vehicle_button = withId(R.id.select_asset_not_in_vehicle_button);
    public static Matcher<View> search_hint = withHint(R.string.select_asset_search_hint);
    public static Matcher<View> search_view = withId(R.id.select_asset_search_view);
    public static Matcher<View> search_input = isAssignableFrom(EditText.class);
    public static Matcher<View> search_list = withId(R.id.select_asset_search_list);
    public static Matcher<View> search_list_item = withId(R.id.select_asset_item);

    public static Matcher<View> search_clear_text =
            allOf(withId(R.id.search_close_btn), withContentDescription("Clear query"),
                    withParent(allOf(withId(R.id.search_plate), withParent(withId(R.id.search_edit_frame)))));

    public static String less_characters_text = SystemUtil.resourceToString(R.string.select_asset_characters);
    public static String find_nothing_text = SystemUtil.resourceToString(R.string.select_asset_find_nothing);
}
