package com.bsmwireless.screens.driverprofile;

import android.content.Context;
import android.content.res.Resources;

import com.bsmwireless.data.storage.users.UserConverter;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.domain.interactors.LoginUserInteractor;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import app.bsmuniversal.com.R;
import app.bsmuniversal.com.RxSchedulerRule;
import io.reactivex.Flowable;
import io.reactivex.Observable;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for DriverProfilePresenter
 */

@RunWith(MockitoJUnitRunner.class)
public class DriverProfilePresenterTest {

    @ClassRule
    public static final RxSchedulerRule RULE = new RxSchedulerRule();

    @Mock
    Context mContext;

    @Mock
    Resources mResources;

    @Mock
    DriverProfileView mView;

    @Mock
    LoginUserInteractor mLoginUserInteractor;

    private static final int MAX_SIGNATURE_LENGTH = 50000; // defined here explicitly because number is defined in API

    private final String mTestSignature = "51,377;89,310;89,310;-544,-1";
    private final String mTooLongTestSignature;

    private UserEntity mFakeUserEntity;
    private DriverProfilePresenter mDriverProfilePresenter;


    public DriverProfilePresenterTest() {
        mTooLongTestSignature = buildTooLongSignature();
    }

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);

        mFakeUserEntity = new UserEntity();
        mDriverProfilePresenter = new DriverProfilePresenter(mContext, mView, mLoginUserInteractor);
    }

    @Test
    public void testOnNeedUpdateUserInfo() {
        // given
        final UserEntity user = new UserEntity();
        when(mLoginUserInteractor.getUser()).thenReturn(Flowable.just(user));

        // when
        mDriverProfilePresenter.onNeedUpdateUserInfo();

        // then
        verify(mView).setUserInfo(eq(user));
    }

    @Test
    public void testOnNeedUpdateUserInfoError() {
        // given
        final Throwable error = new RuntimeException("error!");
        when(mLoginUserInteractor.getUser()).thenReturn(Flowable.error(error));

        // when
        mDriverProfilePresenter.onNeedUpdateUserInfo();

        // then
        verify(mView).showError(eq(error));
    }

    @Test
    public void testOnSaveSignatureClickedNullUser() {
        // given
        setUserToNull();

        when(mContext.getResources()).thenReturn(mResources);
        when(mResources.getString(any(Integer.class))).thenReturn("mock error");

        // when
        mDriverProfilePresenter.onSaveSignatureClicked(mTestSignature);

        // then
        verify(mView).showError(any(Throwable.class));  // Note: verification of string from resource is done in instrumented tests
        verify(mView).hideControlButtons();
    }

    @Test
    public void testOnSaveSignatureClickedValidUserShortSig() {
        // given
        setUserToNotNull();

        // when
        mDriverProfilePresenter.onSaveSignatureClicked(mTestSignature);

        // then
        verify(mView).hideControlButtons();
        assertTrue(mFakeUserEntity.getSignature().equals(mTestSignature));
    }

    @Test
    public void testOnSaveSignatureClickedValidUserLongSig() {
        // given
        setUserToNotNull(); // sets to mFakeUserEntity
        when(mContext.getResources()).thenReturn(mResources);
        when(mResources.getString(any(Integer.class))).thenReturn("mock resource string");

        // when
        mDriverProfilePresenter.onSaveSignatureClicked(mTooLongTestSignature);

        // then
        verify(mView).showError(any(Throwable.class)); // signature-too-large error
        assertTrue(mFakeUserEntity.getSignature().length() < MAX_SIGNATURE_LENGTH);  // check cropped
    }

    @Test
    public void testOnSaveUserInfoNullUser() {
        // given
        setUserToNull();
        when(mContext.getResources()).thenReturn(mResources);
        when(mResources.getString(any(Integer.class))).thenReturn("mock resource string");

        // when
        mDriverProfilePresenter.onSaveUserInfo();

        // then
        verify(mView).showError(any(Exception.class));
    }

    @Test
    public void testOnSaveUserValidUser() {
        // given
        setUserToNotNull();

        // when
        mDriverProfilePresenter.onSaveUserInfo();

        // then
        verify(mView).setResults(eq(UserConverter.toUser(mFakeUserEntity)));
    }

    @Test
    public void testOnChangePasswordClickSuccess() {
        // given
        String oldPwd = "oldPwd";
        String newPwd = "newPwd";
        String confirmPwd = newPwd;

        String mockStrValidPass = "mock valid password";
        String mockStrDriverProfilePwdEmpty = "mock empty password";
        String mockStrDriverProfilePwdNoMatch = "mock no-match password";
        String mockStrDriverProfilePwdNoChange = "mock password not changed";

        when(mContext.getString(eq(R.string.driver_profile_valid_password))).thenReturn(mockStrValidPass);
        when(mContext.getString(eq(R.string.driver_profile_password_field_empty))).thenReturn(mockStrDriverProfilePwdEmpty);
        when(mContext.getString(eq(R.string.driver_profile_password_not_match))).thenReturn(mockStrDriverProfilePwdNoMatch);
        when(mContext.getString(eq(R.string.driver_profile_password_not_changed))).thenReturn(mockStrDriverProfilePwdNoChange);

        when(mLoginUserInteractor.updateDriverPassword(anyString(), anyString())).thenReturn(Observable.just(true));


        // when
        mDriverProfilePresenter.onChangePasswordClick(oldPwd, newPwd, confirmPwd);

        // then
        verify(mView).showPasswordChanged();
    }

    @Test
    public void testOnChangePasswordClickEmptyOld() {
        // given
        String oldPwd = "";
        String newPwd = "newPwd";
        String confirmPwd = newPwd;

        String mockStrValidPass = "mock valid password";
        String mockStrDriverProfilePwdEmpty = "mock empty password";
        String mockStrDriverProfilePwdNoMatch = "mock no-match password";
        String mockStrDriverProfilePwdNoChange = "mock password not changed";

        when(mContext.getString(eq(R.string.driver_profile_valid_password))).thenReturn(mockStrValidPass);
        when(mContext.getString(eq(R.string.driver_profile_password_field_empty))).thenReturn(mockStrDriverProfilePwdEmpty);
        when(mContext.getString(eq(R.string.driver_profile_password_not_match))).thenReturn(mockStrDriverProfilePwdNoMatch);
        when(mContext.getString(eq(R.string.driver_profile_password_not_changed))).thenReturn(mockStrDriverProfilePwdNoChange);

        when(mLoginUserInteractor.updateDriverPassword(anyString(), anyString())).thenReturn(Observable.just(true));


        // when
        mDriverProfilePresenter.onChangePasswordClick(oldPwd, newPwd, confirmPwd);

        // then
        verify(mView).showChangePasswordError(eq(mockStrDriverProfilePwdEmpty));
    }

    @Test
    public void testOnChangePasswordClickEmptyNew() {
        // given
        String oldPwd = "oldPwd";
        String newPwd = "";
        String confirmPwd = "confirmPwd";

        String mockStrValidPass = "mock valid password";
        String mockStrDriverProfilePwdEmpty = "mock empty password";
        String mockStrDriverProfilePwdNoMatch = "mock no-match password";
        String mockStrDriverProfilePwdNoChange = "mock password not changed";

        when(mContext.getString(eq(R.string.driver_profile_valid_password))).thenReturn(mockStrValidPass);
        when(mContext.getString(eq(R.string.driver_profile_password_field_empty))).thenReturn(mockStrDriverProfilePwdEmpty);
        when(mContext.getString(eq(R.string.driver_profile_password_not_match))).thenReturn(mockStrDriverProfilePwdNoMatch);
        when(mContext.getString(eq(R.string.driver_profile_password_not_changed))).thenReturn(mockStrDriverProfilePwdNoChange);

        when(mLoginUserInteractor.updateDriverPassword(anyString(), anyString())).thenReturn(Observable.just(true));


        // when
        mDriverProfilePresenter.onChangePasswordClick(oldPwd, newPwd, confirmPwd);

        // then
        verify(mView).showChangePasswordError(eq(mockStrDriverProfilePwdEmpty));
    }

    @Test
    public void testOnChangePasswordClickConfirmMismatch() {
        // given
        String oldPwd = "oldPwd";
        String newPwd = "newPwd";
        String confirmPwd = "confirmPwd";

        String mockStrValidPass = "mock valid password";
        String mockStrDriverProfilePwdEmpty = "mock empty password";
        String mockStrDriverProfilePwdNoMatch = "mock no-match password";
        String mockStrDriverProfilePwdNoChange = "mock password not changed";

        when(mContext.getString(eq(R.string.driver_profile_valid_password))).thenReturn(mockStrValidPass);
        when(mContext.getString(eq(R.string.driver_profile_password_field_empty))).thenReturn(mockStrDriverProfilePwdEmpty);
        when(mContext.getString(eq(R.string.driver_profile_password_not_match))).thenReturn(mockStrDriverProfilePwdNoMatch);
        when(mContext.getString(eq(R.string.driver_profile_password_not_changed))).thenReturn(mockStrDriverProfilePwdNoChange);

        when(mLoginUserInteractor.updateDriverPassword(anyString(), anyString())).thenReturn(Observable.just(true));


        // when
        mDriverProfilePresenter.onChangePasswordClick(oldPwd, newPwd, confirmPwd);

        // then
        verify(mView).showChangePasswordError(eq(mockStrDriverProfilePwdNoMatch));
    }

    @Test
    public void testOnChangePasswordNotUpdated() {
        // given
        String oldPwd = "oldPwd";
        String newPwd = "newPwd";
        String confirmPwd = newPwd;

        String mockStrValidPass = "mock valid password";
        String mockStrDriverProfilePwdEmpty = "mock empty password";
        String mockStrDriverProfilePwdNoMatch = "mock no-match password";
        String mockStrDriverProfilePwdNoChange = "mock password not changed";

        when(mContext.getString(eq(R.string.driver_profile_valid_password))).thenReturn(mockStrValidPass);
        when(mContext.getString(eq(R.string.driver_profile_password_field_empty))).thenReturn(mockStrDriverProfilePwdEmpty);
        when(mContext.getString(eq(R.string.driver_profile_password_not_match))).thenReturn(mockStrDriverProfilePwdNoMatch);
        when(mContext.getString(eq(R.string.driver_profile_password_not_changed))).thenReturn(mockStrDriverProfilePwdNoChange);

        when(mLoginUserInteractor.updateDriverPassword(anyString(), anyString())).thenReturn(Observable.just(false));


        // when
        mDriverProfilePresenter.onChangePasswordClick(oldPwd, newPwd, confirmPwd);

        // then
        verify(mView).showError(any(Exception.class));
    }

    @Test
    public void testOnChangePasswordError() {
        // given
        String oldPwd = "oldPwd";
        String newPwd = "newPwd";
        String confirmPwd = newPwd;

        String mockStrValidPass = "mock valid password";
        String mockStrDriverProfilePwdEmpty = "mock empty password";
        String mockStrDriverProfilePwdNoMatch = "mock no-match password";
        String mockStrDriverProfilePwdNoChange = "mock password not changed";

        when(mContext.getString(eq(R.string.driver_profile_valid_password))).thenReturn(mockStrValidPass);
        when(mContext.getString(eq(R.string.driver_profile_password_field_empty))).thenReturn(mockStrDriverProfilePwdEmpty);
        when(mContext.getString(eq(R.string.driver_profile_password_not_match))).thenReturn(mockStrDriverProfilePwdNoMatch);
        when(mContext.getString(eq(R.string.driver_profile_password_not_changed))).thenReturn(mockStrDriverProfilePwdNoChange);

        when(mLoginUserInteractor.updateDriverPassword(anyString(), anyString())).thenReturn(Observable.error(new Exception("didn't work")));


        // when
        mDriverProfilePresenter.onChangePasswordClick(oldPwd, newPwd, confirmPwd);

        // then
        verify(mView).showError(any(Exception.class));
    }

    // TODO: move cropSignature tests if cropSignature is moved to a class for signatures or utility class

    // TODO: add tests for cropSignature if error handling added for invalid signature strings
    // (e.g. currently it throws an exception for long strings without semicolon)
    // (e.g. cropSignature cannot handle: signature == null)

    @Test
    public void testCropSignatureShortValidSig() {
        // given
        String validShortSignature = "1,2;2,3;3,4;5,-1;";

        // when
        String result = cropSignature(validShortSignature);

        // then
        assertEquals(validShortSignature, result);
    }

    @Test
    public void testCropSignatureLongValidSig() {
        // given
        String validLongSignature = buildTooLongSignature();

        // when
        String result = cropSignature(validLongSignature);

        // then

        // result is substring of original without leading characters
        assertTrue(validLongSignature.indexOf(result) == 0);

        // result has length less than max
        assertTrue(result.length() < MAX_SIGNATURE_LENGTH);
    }


    /**
     * Builds a signature that is too long.
     *
     * This signature string is parsable as a valid signature.
     *
     * @return a signature string that is too long for the ELD API
     */
    private String buildTooLongSignature() {

        String sigVertexWord = "1,2;";
        int wordLength = sigVertexWord.length();
        int numWordsInMaxLength = MAX_SIGNATURE_LENGTH / wordLength; // integer division
        int tooManyWordsCount = numWordsInMaxLength + 1;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tooManyWordsCount; i++) {
            sb.append(sigVertexWord);
        }
        return sb.toString();
    }

    /**
     * Explicitly sets the mUserEntity property of mDriverProfilePresenter to null.
     *
     * Currently mUserEntity is initialized to null in the constructor of DriverProfilePresenter,
     * but we can't assume this for tests, in case it changes. (i.e. Null is a precondition for the
     * test cases, but the constructor initializing to null isn't necessarily).
     */
    private void setUserToNull() {
        try {
            Field field = mDriverProfilePresenter.getClass().getDeclaredField("mUserEntity");
            field.setAccessible(true);
            field.set(mDriverProfilePresenter, null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            fail("could not explicitly set mDriverProfilePresenter.mUserEntity to null");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            fail("could not access mDriverProfilePresenter.mUserEntity");
        }
    }

    /**
     * Set mDriverProfilePresenter.mUserEntity to mFakeUserEntity UserEntity.
     *
     * Assumption: mFakeUserEntity is reset before every test
     */
    private void setUserToNotNull() {
        try {
            Field field = mDriverProfilePresenter.getClass().getDeclaredField("mUserEntity");
            field.setAccessible(true);
            field.set(mDriverProfilePresenter, mFakeUserEntity);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            fail("could not explicitly set mDriverProfilePresenter.mUserEntity");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            fail("could not access mDriverProfilePresenter.mUserEntity");
        }
    }

    /**
     * Wrapper for DriverProfilePresenterTest#cropSignature(String)
     *
     * @param signature a string that represents a signature
     * @return the original signature, or a truncated signature if the original length > MAX_SIGNATURE_LENGTH
     */
    private String cropSignature(String signature) {

        // access private method
        try {
            Method method = mDriverProfilePresenter.getClass().getDeclaredMethod("cropSignature", String.class);
            method.setAccessible(true);

            return (String) method.invoke(mDriverProfilePresenter, signature);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            fail("DriverProfilePresenter#cropSignature(String) cannot be accessed");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            fail("IllegalAccessException for cropSignature(String)");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            fail("InvocationTargetException for cropSignature(String)");
        }

        return "";
    }

}
