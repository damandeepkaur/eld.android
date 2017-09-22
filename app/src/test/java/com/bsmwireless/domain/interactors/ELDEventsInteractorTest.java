package com.bsmwireless.domain.interactors;

import com.bsmwireless.BaseTest;
import com.bsmwireless.common.Constants;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.eldevents.ELDEventDao;
import com.bsmwireless.data.storage.eldevents.ELDEventEntity;
import com.bsmwireless.data.storage.users.UserDao;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.BlackBoxSensorState;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.Malfunction;
import com.bsmwireless.widgets.alerts.DutyType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ELDEventsInteractorTest extends BaseTest {

    @Mock
    ServiceApi mServiceApi;
    @Mock
    BlackBoxInteractor mBlackBoxInteractor;
    @Mock
    UserInteractor mUserInteractor;
    @Mock
    DutyTypeManager mDutyTypeManager;
    @Mock
    ELDEventDao mELDEventDao;
    @Mock
    AppDatabase mAppDatabase;
    @Mock
    UserDao mUserDao;
    @Mock
    PreferencesManager mPreferencesManager;
    @Mock
    AccountManager mAccountManager;
    @Mock
    TokenManager mTokenManager;

    ELDEventsInteractor mELDEventsInteractor;


    @Before
    public void setUp() throws Exception {

        when(mAppDatabase.ELDEventDao()).thenReturn(mELDEventDao);
        when(mAppDatabase.userDao()).thenReturn(mUserDao);

        when(mUserInteractor.getTimezone()).thenReturn(Flowable.empty());

        mELDEventsInteractor = new ELDEventsInteractor(mServiceApi,
                mPreferencesManager,
                mAppDatabase,
                mUserInteractor,
                mBlackBoxInteractor,
                mDutyTypeManager,
                mAccountManager,
                mTokenManager);
    }

    @Test
    public void getEventPositionCompliance() throws Exception {
        BlackBoxModel blackBoxModel = spy(BlackBoxModel.class);
        when(blackBoxModel.getSensorState(BlackBoxSensorState.GPS)).thenReturn(true);
        when(mBlackBoxInteractor.getLastData()).thenReturn(blackBoxModel);

        ELDEventEntity eldEventEntity = mock(ELDEventEntity.class);
        when(eldEventEntity.getEventType())
                .thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED.getCode());

        when(mELDEventDao.getLatestEventSync(ELDEvent.EventType.DATA_DIAGNOSTIC.getValue(),
                Malfunction.POSITIONING_COMPLIANCE.getCode()))
                .thenReturn(eldEventEntity);

        ELDEvent event = mELDEventsInteractor.getEvent(DutyType.ON_DUTY);
        assertEquals(ELDEvent.LatLngFlag.FLAG_E, event.getLatLngFlag());

    }

    @Test
    public void getEventPositionComplianceCleared() throws Exception {

        BlackBoxModel blackBoxModel = spy(BlackBoxModel.class);
        when(blackBoxModel.getSensorState(BlackBoxSensorState.GPS)).thenReturn(true);
        when(mBlackBoxInteractor.getLastData()).thenReturn(blackBoxModel);

        ELDEventEntity eldEventEntity = mock(ELDEventEntity.class);
        when(eldEventEntity.getEventType())
                .thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode());

        when(mELDEventDao.getLatestEventSync(ELDEvent.EventType.DATA_DIAGNOSTIC.getValue(),
                Malfunction.POSITIONING_COMPLIANCE.getCode()))
                .thenReturn(eldEventEntity);

        ELDEvent event = mELDEventsInteractor.getEvent(DutyType.ON_DUTY);
        assertEquals(ELDEvent.LatLngFlag.FLAG_NONE, event.getLatLngFlag());
    }

    @Test
    public void getEventNoCompliance() throws Exception {

        BlackBoxModel blackBoxModel = spy(BlackBoxModel.class);
        when(blackBoxModel.getSensorState(BlackBoxSensorState.GPS)).thenReturn(true);

        when(mBlackBoxInteractor.getLastData()).thenReturn(blackBoxModel);

        when(mELDEventDao.getLatestEventSync(ELDEvent.EventType.DATA_DIAGNOSTIC.getValue(),
                Malfunction.POSITIONING_COMPLIANCE.getCode()))
                .thenReturn(null);

        ELDEvent event = mELDEventsInteractor.getEvent(DutyType.ON_DUTY);

        assertEquals(ELDEvent.LatLngFlag.FLAG_NONE, event.getLatLngFlag());

    }

    @Test
    public void getEventGpsNoFixNoCompliance() throws Exception {

        BlackBoxModel blackBoxModel = mock(BlackBoxModel.class);
        when(blackBoxModel.getSensorState(BlackBoxSensorState.GPS)).thenReturn(false);

        when(mBlackBoxInteractor.getLastData()).thenReturn(blackBoxModel);

        when(mELDEventDao.getLatestEventSync(ELDEvent.EventType.DATA_DIAGNOSTIC.getValue(),
                Malfunction.POSITIONING_COMPLIANCE.getCode()))
                .thenReturn(null);

        ELDEvent event = mELDEventsInteractor.getEvent(DutyType.ON_DUTY);

        assertEquals(ELDEvent.LatLngFlag.FLAG_X, event.getLatLngFlag());

    }

    /**
     * Test for getting a correct list of diagnostic events
     *
     * @throws Exception
     */
    @Test
    public void getDiagnosticEvents() throws Exception {

        final int currentUserId = 1;

        when(mAccountManager.getCurrentUserId()).thenReturn(currentUserId);

        // logged events

        ELDEventEntity powerDiagnosticLogged = mock(ELDEventEntity.class);
        when(powerDiagnosticLogged.getEventCode())
                .thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED.getCode());
        when(powerDiagnosticLogged.getMalCode())
                .thenReturn(Malfunction.POWER_DATA_DIAGNOSTIC.getCode());

        ELDEventEntity engineSynchLogged = mock(ELDEventEntity.class);
        when(engineSynchLogged.getEventCode())
                .thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED.getCode());
        when(engineSynchLogged.getMalCode())
                .thenReturn(Malfunction.ENGINE_SYNCHRONIZATION.getCode());

        ELDEventEntity dataTransferLogged = mock(ELDEventEntity.class);
        when(dataTransferLogged.getEventCode())
                .thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED.getCode());
        when(dataTransferLogged.getMalCode())
                .thenReturn(Malfunction.DATA_TRANSFER.getCode());

        ELDEventEntity secondPowerDiagnosticLogged = mock(ELDEventEntity.class);
        when(secondPowerDiagnosticLogged.getEventCode())
                .thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED.getCode());
        when(secondPowerDiagnosticLogged.getMalCode())
                .thenReturn(Malfunction.POWER_DATA_DIAGNOSTIC.getCode());

        ELDEventEntity unidentifiedLogged = mock(ELDEventEntity.class);
        when(unidentifiedLogged.getEventCode())
                .thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED.getCode());
        when(unidentifiedLogged.getMalCode())
                .thenReturn(Malfunction.UNIDENTIFIED_DRIVING.getCode());

        // cleared events
        ELDEventEntity powerDiagnosticCleared = mock(ELDEventEntity.class);
        when(powerDiagnosticCleared.getEventCode())
                .thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode());
        when(powerDiagnosticCleared.getMalCode())
                .thenReturn(Malfunction.POWER_DATA_DIAGNOSTIC.getCode());

        ELDEventEntity dataTransferCleared = mock(ELDEventEntity.class);
        when(dataTransferCleared.getEventCode())
                .thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode());
        when(dataTransferCleared.getMalCode())
                .thenReturn(Malfunction.DATA_TRANSFER.getCode());

        ELDEventEntity unidentifiedCleared = mock(ELDEventEntity.class);
        when(unidentifiedCleared.getEventCode())
                .thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode());
        when(unidentifiedCleared.getMalCode())
                .thenReturn(Malfunction.UNIDENTIFIED_DRIVING.getCode());

        ELDEventEntity engineSynchCleared = mock(ELDEventEntity.class);
        when(engineSynchCleared.getEventCode())
                .thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode());
        when(engineSynchCleared.getMalCode())
                .thenReturn(Malfunction.ENGINE_SYNCHRONIZATION.getCode());

        List<ELDEventEntity> entities = Arrays.asList(powerDiagnosticLogged, engineSynchLogged,
                powerDiagnosticCleared, engineSynchCleared, secondPowerDiagnosticLogged,
                dataTransferLogged, unidentifiedLogged, unidentifiedCleared, dataTransferCleared);

        when(mELDEventDao.loadMalfunctions(anyInt(), anyInt(), any(String[].class)))
                .thenReturn(Single.just(entities));

        mELDEventsInteractor.getDiagnosticEvents()
                .test()
                .assertValue(events -> {

                    if (events.size() != 1) return false;
                    ELDEvent event = events.get(0);

                    return event.getMalCode() == Malfunction.POWER_DATA_DIAGNOSTIC
                            && event.getEventCode() == ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED.getCode();
                });
        verify(mELDEventDao).loadMalfunctions(currentUserId,
                ELDEvent.EventType.DATA_DIAGNOSTIC.getValue(),
                Constants.DIAGNOSTIC_CODES);
    }
}