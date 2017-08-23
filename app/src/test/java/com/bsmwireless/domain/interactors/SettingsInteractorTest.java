package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import app.bsmuniversal.com.RxSchedulerRule;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Tests for SettingsInteractor.
 */
@RunWith(MockitoJUnitRunner.class)
public class SettingsInteractorTest {

    @ClassRule
    public static final RxSchedulerRule RULE = new RxSchedulerRule();

    @Mock
    AppDatabase mAppDatabase;

    @Mock
    PreferencesManager mPreferencesManager;

    private SettingsInteractor mSettingsInteractor;

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);

        mSettingsInteractor = new SettingsInteractor(mAppDatabase, mPreferencesManager);
    }


    @Test
    public void testSaveBoxGPSEnabledTrue() {
        // given
        // n/a

        // when
        mSettingsInteractor.saveBoxGPSEnabled(true);

        // then
        verify(mPreferencesManager).setBoxGPSEnabled(eq(true));
    }

    @Test
    public void testSaveBoxGPSEnabledFalse() {
        // given
        // n/a

        // when
        mSettingsInteractor.saveBoxGPSEnabled(false);

        // then
        verify(mPreferencesManager).setBoxGPSEnabled(eq(false));
    }

    @Test
    public void testSaveFixedAmountEnabledTrue() {
        // given
        // n/a

        // when
        mSettingsInteractor.saveFixedAmountEnabled(true);

        // then
        verify(mPreferencesManager).setFixedAmountEnabled(eq(true));
    }

    @Test
    public void testSaveFixedAmountEnabledFalse() {
        // given
        // n/a

        // when
        mSettingsInteractor.saveFixedAmountEnabled(false);

        // then
        verify(mPreferencesManager).setFixedAmountEnabled(eq(false));
    }

    @Test
    public void testIsBoxGpsEnabled() {
        // given
        // n/a

        // when
        mSettingsInteractor.isBoxGPSEnabled();

        // then
        verify(mPreferencesManager).isBoxGPSEnabled();
    }

    @Test
    public void testIsFixedAmountEnabled() {
        // given
        // n/a

        // when
        mSettingsInteractor.isFixedAmountEnabled();

        // then
        verify(mPreferencesManager).isFixedAmountEnabled();
    }

    @Test
    public void testSaveKmOdometerUnitsSelectedTrue() {
        // given
        // n/a

        // when
        mSettingsInteractor.saveKMOdometerUnitsSelected(true);

        // then
        verify(mPreferencesManager).setKMOdometerUnits(eq(true));
    }

    @Test
    public void testSaveKmOdometerUnitsSelectedFalse() {
        // given
        // n/a

        // when
        mSettingsInteractor.saveKMOdometerUnitsSelected(false);

        // then
        verify(mPreferencesManager).setKMOdometerUnits(eq(false));
    }

    @Test
    public void testIsKmOdometerUnitsSelected() {
        // given
        // n/a

        // when
        mSettingsInteractor.isKMOdometerUnitsSelected();

        // then
        verify(mPreferencesManager).isKMOdometerUnitsSelected();
    }

}
