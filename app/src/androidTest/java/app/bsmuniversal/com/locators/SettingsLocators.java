package app.bsmuniversal.com.locators;

import android.view.View;

import org.hamcrest.Matcher;

import app.bsmuniversal.com.R;

import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Provide views for settings screen
 */
public class SettingsLocators {

    public static Matcher<View> settings_title = withText(R.string.settings_title);
}
