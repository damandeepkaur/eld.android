package app.bsmuniversal.com.locators;

import android.view.View;

import org.hamcrest.Matcher;

import app.bsmuniversal.com.R;

import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Provide views for home screen
 */
public class HomeLocators {

    public static Matcher<View> home_title = withText(R.string.menu_home);

}
