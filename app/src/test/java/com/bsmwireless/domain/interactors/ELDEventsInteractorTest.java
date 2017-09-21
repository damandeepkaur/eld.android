package com.bsmwireless.domain.interactors;

import com.bsmwireless.BaseTest;
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

import io.reactivex.Flowable;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
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
}