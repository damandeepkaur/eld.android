package app.bsmuniversal.com.tests.login;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;

import com.bsmwireless.screens.login.LoginActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import app.bsmuniversal.com.base.BaseTestClass;
import app.bsmuniversal.com.locators.CommonLocators;
import app.bsmuniversal.com.locators.LoginLocators;
import app.bsmuniversal.com.pages.LoginPage;
import app.bsmuniversal.com.utils.SystemUtil;

/**
 * Automation test class to verify Login screen elements
 */
public class LoginElementsTest extends BaseTestClass{

    @Rule
    public ActivityTestRule<LoginActivity> loginActivityTestRule = new IntentsTestRule<>(LoginActivity.class);

    @Before
    public void setUp() throws Exception {
        SystemUtil.clearSharedPrefForKey(CommonLocators.KEY_REMEMBER_USER_ENABLED);
    }

    @Test
    public void test_switch() {
        assert_checked(LoginLocators.switch_button, true);
    }

    @Test
    public void test_elements() {
        int primaryColor = LoginPage.get_primary_color(loginActivityTestRule.getActivity());
        int accentColor = LoginPage.get_accent_color(loginActivityTestRule.getActivity());
        Assert.assertEquals(CommonLocators.primary_color, primaryColor);
        Assert.assertEquals(CommonLocators.accent_color, accentColor);

        assert_something_displayed(LoginLocators.username, true);
        assert_something_displayed(LoginLocators.password, true);
        assert_something_displayed(LoginLocators.password_toggle, true);
        assert_something_displayed(LoginLocators.domain, true);
        assert_something_displayed(LoginLocators.switch_button, true);
        assert_something_displayed(LoginLocators.execute_login, true);
    }
}
