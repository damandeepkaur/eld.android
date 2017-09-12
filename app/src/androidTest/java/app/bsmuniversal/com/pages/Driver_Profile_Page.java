package app.bsmuniversal.com.pages;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;

import app.bsmuniversal.com.locators.DrawerLocators;
import app.bsmuniversal.com.locators.DriverProfileLocator;
import app.bsmuniversal.com.locators.LoginLocators;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Provides interaction with Driver Profile Screen
 */

public class Driver_Profile_Page {
    public static void change_password(String currentpassword,String newpassword) {
        CommonPage.enter_on_view(DriverProfileLocator.current_password, currentpassword);
        CommonPage.enter_on_view(DriverProfileLocator.new_password, newpassword);
        CommonPage.enter_on_view(DriverProfileLocator.confirm_password, newpassword,true,true);

    }
    public static void click_on_change_password_button() {
        Espresso.closeSoftKeyboard();
        CommonPage.perform_click(DriverProfileLocator.confirm_button,true,false);
    }


    public static void clear_signature()
    {
        Espresso.closeSoftKeyboard();
        CommonPage.perform_click(DriverProfileLocator.change_button,true,false);
        CommonPage.perform_click(DriverProfileLocator.negative_action);

        //  CommonPage.perform_click(DriverProfileLocator.positive_action);

    }
    public static void click_back_button()
    {
        Espresso.closeSoftKeyboard();
        DriverProfileLocator.back.perform(click());
    }
    public static void Change_terminal_home()
    {
        Espresso.closeSoftKeyboard();
        CommonPage.perform_click(DriverProfileLocator.terminal_list,true,false);
        ViewInteraction Terminal_Home_list = onView(
                allOf(withId(android.R.id.text1), withText("mera home terminal"), isDisplayed()));
        Terminal_Home_list.perform(click());


    }
    public static void re_login() {
        Espresso.closeSoftKeyboard();
        CommonPage.perform_click(LoginLocators.execute_login);
    }


}
