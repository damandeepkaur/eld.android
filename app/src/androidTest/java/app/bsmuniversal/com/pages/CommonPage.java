package app.bsmuniversal.com.pages;

import android.view.View;

import org.hamcrest.Matcher;

import app.bsmuniversal.com.utils.ViewActionsUtil;
import android.support.test.espresso.ViewInteraction;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;

/**
 * Class that holds all general methods used by other classes
 */
public class CommonPage {

    /**
     * Performs typing passed text into the view with passed matcher.
     *
     * @param view Matcher of view to enter the text
     * @param text Text to be typed
     */
    public static void enter_on_view(Matcher<View> view, String text) {
        enter_on_view(view, text, false, false);
    }

    /**
     * Performs typing passed text into the view with passed matcher. Before that scrolling to the
     * item if it needs.
     * <p>
     * It's for descendants of a ScrollView, HorizontalScrollView, ListView or NestedScrollView.
     *
     * @param view          Matcher of view to enter the text
     * @param text          Text to be typed
     * @param withScroll    Need scroll or not
     * @param closeKeyboard Need to close keyboard
     */
    public static void enter_on_view(Matcher<View> view, String text, boolean withScroll, boolean closeKeyboard) {
        if (withScroll) {
            onView(view).perform(scrollTo());
        }

        if (closeKeyboard) {
            onView(view).perform(clearText(), typeText(text), closeSoftKeyboard());
        } else {
            onView(view).perform(clearText(), typeText(text));
        }
    }

    public static void perform_click(Matcher<View> view) {
        perform_click(view,false,false);
         }

    //updated perform_click after scroll when the element or locator is not in the current View

    public static void perform_click(Matcher<View> view,boolean withScroll, boolean closeKeyboard) {
        if (withScroll) {
            onView(view).perform(scrollTo());
        }
        if (closeKeyboard) {
            onView(view).perform(clearText(),closeSoftKeyboard());
        } else {
            onView(view).perform(click());
        }
    }

    public static void set_checked(Matcher<View> view, boolean checked) {
        onView(view).perform(ViewActionsUtil.setChecked(checked));
    }


}
