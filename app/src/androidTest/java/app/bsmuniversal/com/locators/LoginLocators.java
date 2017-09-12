package app.bsmuniversal.com.locators;

import android.view.View;

import org.hamcrest.Matcher;

import app.bsmuniversal.com.R;
import app.bsmuniversal.com.utils.SystemUtil;

import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Provide views for login screen
 */
public class LoginLocators {

    public static Matcher<View> username = withId(R.id.username);
    public static Matcher<View> password = withId(R.id.password);
    public static Matcher<View> password_toggle = withId(R.id.text_input_password_toggle);
    public static Matcher<View> domain = withId(R.id.domain);
    public static Matcher<View> switch_button = withId(R.id.switchButton);
    public static Matcher<View> execute_login = withId(R.id.execute_login);
    public static Matcher<View> login_snackbar = withId(R.id.login_snackbar);

    public static String error_401_message = "(Error 401) Not Authenticated";
    public static String error_empty_username = SystemUtil.resourceToString(R.string.error_username);
    public static String error_empty_password = SystemUtil.resourceToString(R.string.error_password);
    public static String error_empty_domain = SystemUtil.resourceToString(R.string.error_domain);
}
