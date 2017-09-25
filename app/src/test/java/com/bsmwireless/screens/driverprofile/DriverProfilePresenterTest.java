package com.bsmwireless.screens.driverprofile;

import android.content.res.Resources;

import com.bsmwireless.data.network.RetrofitException;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.carriers.CarrierEntity;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalEntity;
import com.bsmwireless.data.storage.users.FullUserEntity;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

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

@RunWith(MockitoJUnitRunner.Silent.class)
public class DriverProfilePresenterTest {

    @ClassRule
    public static final RxSchedulerRule RULE = new RxSchedulerRule();

    @Mock
    Resources mResources;

    @Mock
    DriverProfileView mView;

    @Mock
    UserInteractor mUserInteractor;

    @Mock
    DutyTypeManager mDutyTypeManager;

    @Mock
    ELDEventsInteractor mEventsInteractor;

    @Mock
    AccountManager mAccountManager;

    private static final int MAX_SIGNATURE_LENGTH = 50000; // defined here explicitly because number is defined in API

    private final String mTestSignature = "51,377;89,310;89,310;-544,-1";
    private final String mTooLongTestSignature;

    private FullUserEntity mFakeFullUserEntity;
    private DriverProfilePresenter mDriverProfilePresenter;


    public DriverProfilePresenterTest() {
        mTooLongTestSignature = buildTooLongSignature();
    }

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);

        mFakeFullUserEntity = new FullUserEntity();
        mDriverProfilePresenter = new DriverProfilePresenter(mView, mUserInteractor, mDutyTypeManager, mEventsInteractor, mAccountManager);
    }

    @Test
    public void testOnNeedUpdateUserInfo() {
        // given
        when(mUserInteractor.getFullUserSync()).thenReturn(mFakeFullUserEntity);

        // when
        mDriverProfilePresenter.onNeedUpdateUserInfo();

        // then
        verify(mView).setUserInfo(eq(mFakeFullUserEntity.getUserEntity()));
    }

    @Test
    public void testOnNeedUpdateUserInfoError() {
        // given
        final Throwable error = new RuntimeException("error!");

        when(mUserInteractor.getFullDriver()).thenReturn(Flowable.error(error));

        // when
        mDriverProfilePresenter.onNeedUpdateUserInfo();
    }

    @Test
    public void testOnNeedUpdateUserInfoValidHomeTerminal() {
        // given
        List<HomeTerminalEntity> homeTerminals = new ArrayList<>();

        HomeTerminalEntity ht1 = new HomeTerminalEntity();
        ht1.setId(101);
        ht1.setName("ht1");
        homeTerminals.add(ht1);

        HomeTerminalEntity ht2 = new HomeTerminalEntity();
        ht2.setId(102);
        ht2.setName("ht2");
        homeTerminals.add(ht2);

        HomeTerminalEntity ht3 = new HomeTerminalEntity();
        ht3.setId(103);
        ht3.setName("ht3");
        homeTerminals.add(ht3);

        final int selectedHomeTerminalId = 102;
        final int listIdxOfSelected = 1;  // id 102 is at list index 1

        // note that mFakeFullUserEntity is reset before every test is run
        mFakeFullUserEntity.getUserEntity().setHomeTermId(selectedHomeTerminalId);
        mFakeFullUserEntity.setHomeTerminalEntities(homeTerminals);

        when(mUserInteractor.getFullUserSync()).thenReturn(mFakeFullUserEntity);

        // when
        mDriverProfilePresenter.onNeedUpdateUserInfo();

        // then
        verify(mView).setHomeTerminalsSpinner(any(List.class), eq(listIdxOfSelected));
    }

    @Test
    public void testOnNeedUpdateUserInfoWithCarriers() {
        // given
        List<CarrierEntity> carriers = new ArrayList<>();

        CarrierEntity ce1 = new CarrierEntity();
        ce1.setId(101);
        ce1.setName("ce1");
        carriers.add(ce1);

        CarrierEntity ce2 = new CarrierEntity();
        ce2.setId(102);
        ce2.setName("ce2");
        carriers.add(ce2);

        mFakeFullUserEntity.setCarriers(carriers);

        when(mUserInteractor.getFullUserSync()).thenReturn(mFakeFullUserEntity);

        // when
        mDriverProfilePresenter.onNeedUpdateUserInfo();

        // then
        verify(mView).setCarrierInfo(eq(ce1));
    }

    @Test
    public void testOnSaveSignatureClickedNullUser() {
        // given
        setUserToNull();

        // when
        mDriverProfilePresenter.onSaveSignatureClicked(mTestSignature);

        // then
        verify(mView).showError(eq(DriverProfileView.Error.ERROR_INVALID_USER));
    }

    @Test
    public void testOnSaveSignatureClickedValidUserShortSigSuccess() {
        // given
        setUserToNotNull();
        when(mUserInteractor.updateDriverSignature(anyString())).thenReturn(Observable.just(true));

        // when
        mDriverProfilePresenter.onSaveSignatureClicked(mTestSignature);

        // then
        assertTrue(mFakeFullUserEntity.getUserEntity().getSignature().equals(mTestSignature));
    }

    @Test
    @Ignore("Handler not mocked")
    public void testOnSaveSignatureClickedApiFailed() {
        // given
        setUserToNotNull();
        when(mUserInteractor.updateDriverSignature(anyString())).thenReturn(Observable.just(false));

        // when
        mDriverProfilePresenter.onSaveSignatureClicked(mTestSignature);

        // then
        verify(mView).showError(eq(DriverProfileView.Error.ERROR_SAVE_SIGNATURE));
    }

    @Test
    public void testOnSaveSignatureClickedApiError() {
        // given
        setUserToNotNull();

        when(mUserInteractor.updateDriverSignature(anyString())).thenReturn(Observable.error(RetrofitException.networkError(new ConnectException())));

        // when
        mDriverProfilePresenter.onSaveSignatureClicked(mTestSignature);

        // then
        verify(mView).showError(any(RetrofitException.class));
    }

    @Test
    public void testOnSaveSignatureClickedValidUserLongSig() {
        // given
        setUserToNotNull(); // sets to mFakeUserEntity
        when(mUserInteractor.updateDriverSignature(anyString())).thenReturn(Observable.just(true)); // API success

        // when
        mDriverProfilePresenter.onSaveSignatureClicked(mTooLongTestSignature);

        // then
        verify(mView).showError(eq(DriverProfileView.Error.ERROR_SIGNATURE_LENGTH));
        assertTrue(mFakeFullUserEntity.getUserEntity().getSignature().length() <= MAX_SIGNATURE_LENGTH);  // check cropped
    }

    @Test
    public void testOnChangePasswordClickSuccess() {
        // given
        String oldPwd = "oldPwd";
        String newPwd = "newPwd";
        String confirmPwd = newPwd;

        when(mUserInteractor.updateDriverPassword(anyString(), anyString())).thenReturn(Observable.just(true));

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

        when(mUserInteractor.updateDriverPassword(anyString(), anyString())).thenReturn(Observable.just(true));


        // when
        mDriverProfilePresenter.onChangePasswordClick(oldPwd, newPwd, confirmPwd);

        // then
        verify(mView).showError(eq(DriverProfileView.Error.PASSWORD_FIELD_EMPTY));
    }

    @Test
    public void testOnChangePasswordClickEmptyNew() {
        // given
        String oldPwd = "oldPwd";
        String newPwd = "";
        String confirmPwd = "confirmPwd";

        when(mUserInteractor.updateDriverPassword(anyString(), anyString())).thenReturn(Observable.just(true));


        // when
        mDriverProfilePresenter.onChangePasswordClick(oldPwd, newPwd, confirmPwd);

        // then
        verify(mView).showError(eq(DriverProfileView.Error.PASSWORD_FIELD_EMPTY));
    }

    @Test
    public void testOnChangePasswordClickConfirmMismatch() {
        // given
        String oldPwd = "oldPwd";
        String newPwd = "newPwd";
        String confirmPwd = "confirmPwd";

        when(mUserInteractor.updateDriverPassword(anyString(), anyString())).thenReturn(Observable.just(true));


        // when
        mDriverProfilePresenter.onChangePasswordClick(oldPwd, newPwd, confirmPwd);

        // then
        verify(mView).showError(eq(DriverProfileView.Error.PASSWORD_NOT_MATCH));
    }

    @Test
    public void testOnChangePasswordNotUpdated() {
        // given
        String oldPwd = "oldPwd";
        String newPwd = "newPwd";
        String confirmPwd = newPwd;

        when(mUserInteractor.updateDriverPassword(anyString(), anyString())).thenReturn(Observable.just(false));


        // when
        mDriverProfilePresenter.onChangePasswordClick(oldPwd, newPwd, confirmPwd);

        // then
        verify(mView).showError(eq(DriverProfileView.Error.ERROR_CHANGE_PASSWORD));
    }

    @Test
    public void testOnChangePasswordError() {
        // given
        String oldPwd = "oldPwd";
        String newPwd = "newPwd";
        String confirmPwd = newPwd;

        when(mUserInteractor.updateDriverPassword(anyString(), anyString())).thenReturn(Observable.error(RetrofitException.networkError(new ConnectException())));

        // when
        mDriverProfilePresenter.onChangePasswordClick(oldPwd, newPwd, confirmPwd);

        // then
        verify(mView).showError(any(RetrofitException.class));
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
            Field field = mDriverProfilePresenter.getClass().getDeclaredField("mFullUserEntity");
            field.setAccessible(true);
            field.set(mDriverProfilePresenter, null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            fail("could not explicitly set mDriverProfilePresenter.mFullUserEntity to null");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            fail("could not access mDriverProfilePresenter.mFullUserEntity");
        }
    }

    /**
     * Set mDriverProfilePresenter.mUserEntity to mFakeUserEntity UserEntity.
     *
     * Assumption: mFakeUserEntity is reset before every test
     */
    private void setUserToNotNull() {
        try {
            Field field = mDriverProfilePresenter.getClass().getDeclaredField("mFullUserEntity");
            field.setAccessible(true);
            field.set(mDriverProfilePresenter, mFakeFullUserEntity);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            fail("could not explicitly set mDriverProfilePresenter.mFullUserEntity");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            fail("could not access mDriverProfilePresenter.mFullUserEntity");
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
