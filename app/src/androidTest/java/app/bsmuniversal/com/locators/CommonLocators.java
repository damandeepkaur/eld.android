package app.bsmuniversal.com.locators;

import android.graphics.Color;
import android.view.View;

import org.hamcrest.Matcher;

import app.bsmuniversal.com.R;

import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static org.hamcrest.Matchers.allOf;

/**
 * Provide views for common logic
 */
public class CommonLocators {

    public static Matcher<View> navigate_up =
            allOf(withContentDescription("Navigate up"), withParent(withId(R.id.toolbar)));

    public static final String KEY_REMEMBER_USER_ENABLED = "keep_user_enabled";

    public static int primary_color = Color.parseColor("#484848");
    public static int accent_color = Color.parseColor("#7ac144");
}
