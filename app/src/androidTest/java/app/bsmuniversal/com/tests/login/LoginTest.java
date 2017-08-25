package app.bsmuniversal.com.tests.login;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;

import com.bsmwireless.screens.login.LoginActivity;
import com.bsmwireless.screens.selectasset.SelectAssetActivity;

import org.junit.Rule;
import org.junit.Test;

import app.bsmuniversal.com.R;
import app.bsmuniversal.com.base.BaseTestClass;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.times;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


/**
 * Automation test class to verify Login implementation
 */
public class LoginTest extends BaseTestClass {

    @Rule
    public ActivityTestRule<LoginActivity> mLoginActivityTestRule = new IntentsTestRule<>(LoginActivity.class);

    @Test
    public void test_firstLoginSuccessful() throws InterruptedException {
        fillLoginDataAndDoLogin("mera2", "pass789", "mera", false);
        intended(hasComponent(SelectAssetActivity.class.getName()));
        onView(withId(R.id.select_asset_not_in_vehicle_button)).perform(click());
        doLogout();
    }

    @Test
    public void test_secondLoginSuccessful() throws InterruptedException {
        fillLoginDataAndDoLogin("mera2", "pass789", "mera", false);
        onView(withId(R.id.select_asset_not_in_vehicle_button)).perform(click());
        doLogout();
        fillLoginDataAndDoLogin("mera2", "pass789", "mera", true);
        intended(hasComponent(SelectAssetActivity.class.getName()), times(2));
        onView(withId(R.id.select_asset_not_in_vehicle_button)).perform(click());
        doLogout();
    }

    @Test
    public void test_wrongUsername() throws InterruptedException {
        fillLoginDataAndDoLogin("asasdd", "pass789", "mera", false);
        checkIfSnackBarWithMessageIsDisplayed(R.id.login_snackbar, "(Error 401) Not Authenticated");
    }

    @Test
    public void test_wrongPassword() throws InterruptedException {
        fillLoginDataAndDoLogin("mera2", "pasasdasds789", "mera", false);
        checkIfSnackBarWithMessageIsDisplayed(R.id.login_snackbar, "(Error 401) Not Authenticated");
    }

    @Test
    public void test_wrongCompany() throws InterruptedException {
        fillLoginDataAndDoLogin("mera2", "pass789", "mera12asd", false);
        checkIfSnackBarWithMessageIsDisplayed(R.id.login_snackbar, "(Error 401) Not Authenticated");
    }

    @Test
    public void test_emptyUsername() throws InterruptedException {
        fillLoginDataAndDoLogin("", "pass789", "mera", false);
        checkIfSnackBarWithMessageIsDisplayed(R.id.login_snackbar,
                mLoginActivityTestRule.getActivity().getString(R.string.error_username));
    }

    @Test
    public void test_emptyPassword() throws InterruptedException {
        fillLoginDataAndDoLogin("mera2", "", "mera", false);
        checkIfSnackBarWithMessageIsDisplayed(R.id.login_snackbar,
                mLoginActivityTestRule.getActivity().getString(R.string.error_password));
    }

    @Test
    public void test_emptyCompany() throws InterruptedException {
        fillLoginDataAndDoLogin("mera", "pass789", "", false);
        checkIfSnackBarWithMessageIsDisplayed(R.id.login_snackbar,
                mLoginActivityTestRule.getActivity().getString(R.string.error_domain));
    }

    @Test
    public void test_emptyFields() throws InterruptedException {
        fillLoginDataAndDoLogin("", "", "", false);
        checkIfSnackBarWithMessageIsDisplayed(R.id.login_snackbar,
                mLoginActivityTestRule.getActivity().getString(R.string.error_username));
    }

}