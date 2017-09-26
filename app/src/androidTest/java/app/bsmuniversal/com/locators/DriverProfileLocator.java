package app.bsmuniversal.com.locators;

import android.support.test.espresso.ViewInteraction;
import android.view.View;

import org.hamcrest.Matcher;

import app.bsmuniversal.com.R;
import app.bsmuniversal.com.utils.SystemUtil;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static org.hamcrest.Matchers.allOf;


/**
 * Provide views for Driver Profile screen
 */

public class DriverProfileLocator {

        public static Matcher<View> drivename = withId(R.id.name);
        public static Matcher<View> empid = withId(R.id.emp_id);
        public static Matcher<View> license = withId(R.id.license);
        public static Matcher<View> role = withId(R.id.role);
        public static Matcher<View> current_password = withId(R.id.current_password);
        public static Matcher<View> new_password = withId(R.id.new_password);
        public static Matcher<View> confirm_password = withId(R.id.confirm_password);
        public static Matcher<View> confirm_button = withId(R.id.change_password_button);
        public static Matcher<View> change_button = withId(R.id.change_button);
        public static Matcher<View> negative_action = withId(R.id.negative_action);
        public static Matcher<View> positive_action = withId(R.id.positive_action);

        public static ViewInteraction back = onView(allOf(withContentDescription("Navigate up"),
                                                withParent(withId(R.id.toolbar)),isDisplayed()));

       public static Matcher<View> driver_sign = withId(R.id.driver_sign);
       public static Matcher<View> terminal_list = withId(R.id.terminal_name_spinner);

        public static String error_change_password = SystemUtil.resourceToString(R.string.error_password);
        public static String error_save_signature = SystemUtil.resourceToString(R.string.sign_error_message);
        public static String error_password_not_match = SystemUtil.resourceToString(R.string.driver_profile_password_not_match);
        public static String error_empty_domain = SystemUtil.resourceToString(R.string.driver_profile_password_field_empty);


}
