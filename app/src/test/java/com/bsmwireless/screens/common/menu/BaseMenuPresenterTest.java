package com.bsmwireless.screens.common.menu;

import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BaseMenuPresenterTest {

    @Mock
    DutyTypeManager dutyTypeManager;

    @Mock
    ELDEventsInteractor eldEventsInteractor;

    @Mock
    UserInteractor userInteractor;

    @Mock
    BaseMenuView view;

    @Mock
    AccountManager accountManager;

    private BaseMenuPresenter baseMenuPresenter;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());

        baseMenuPresenter = new BaseMenuPresenter(dutyTypeManager, eldEventsInteractor, userInteractor, accountManager) {
            @Override
            protected BaseMenuView getView() {
                return view;
            }
        };
    }

    @Test
    public void testMalfunctionEventsExist() throws Exception {
        when(eldEventsInteractor.hasMalfunctionEvents()).thenReturn(Flowable.just(true));
        when(eldEventsInteractor.hasDiagnosticEvents()).thenReturn(Flowable.just(true));
        when(userInteractor.getCoDriversNumber()).thenReturn(Flowable.empty());

        baseMenuPresenter.onStart();
        baseMenuPresenter.onMenuCreated();
        verify(view).changeMalfunctionStatus(true);
        verify(view).changeDiagnosticStatus(true);
    }

    @Test
    public void noMalfunctionEvents() throws Exception {
        when(eldEventsInteractor.hasMalfunctionEvents()).thenReturn(Flowable.just(false));
        when(eldEventsInteractor.hasDiagnosticEvents()).thenReturn(Flowable.just(false));
        when(userInteractor.getCoDriversNumber()).thenReturn(Flowable.empty());

        baseMenuPresenter.onStart();
        baseMenuPresenter.onMenuCreated();
        verify(view).changeDiagnosticStatus(false);
        verify(view).changeMalfunctionStatus(false);
    }

    @Test
    public void malfunctionEventsExistAfterRun() throws Exception {

        BehaviorSubject<Boolean> subject = BehaviorSubject.createDefault(false);

        when(eldEventsInteractor.hasMalfunctionEvents())
                .thenReturn(subject.toFlowable(BackpressureStrategy.LATEST));
        when(eldEventsInteractor.hasDiagnosticEvents())
                .thenReturn(subject.toFlowable(BackpressureStrategy.LATEST));
        when(userInteractor.getCoDriversNumber()).thenReturn(Flowable.empty());

        baseMenuPresenter.onStart();
        baseMenuPresenter.onMenuCreated();
        verify(view).changeDiagnosticStatus(false);
        verify(view).changeMalfunctionStatus(false);

        subject.onNext(true);
        verify(view).changeDiagnosticStatus(true);
        verify(view).changeMalfunctionStatus(true);
    }
}