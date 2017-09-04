package app.bsmuniversal.com.pages;

import android.support.test.espresso.Espresso;

import app.bsmuniversal.com.locators.LoginLocators;

/**
 * Provides interaction with login screen
 */
public class LoginPage {

    public static void enter_password(String password) {
        CommonPage.enter_on_view(LoginLocators.password, password);
    }

    public static void click_on_login() {
        Espresso.closeSoftKeyboard();
        CommonPage.perform_click(LoginLocators.execute_login);
    }

    public static void set_remember_me(boolean checked) {
        CommonPage.set_checked(LoginLocators.switch_button, checked);
    }
}
