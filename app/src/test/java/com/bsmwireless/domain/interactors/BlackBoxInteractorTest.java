package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.blackbox.BlackBoxConnectionManager;
import com.bsmwireless.models.BlackBoxModel;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import app.bsmuniversal.com.RxSchedulerRule;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for BlackBoxInteractor.
 */
@RunWith(MockitoJUnitRunner.class)
public class BlackBoxInteractorTest {

    @ClassRule
    public static final RxSchedulerRule RULE = new RxSchedulerRule();

    @Mock
    BlackBoxConnectionManager mBlackBoxConnectionManager;


    private BlackBoxInteractor mBlackBoxInteractor;


    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);

        mBlackBoxInteractor = new BlackBoxInteractor(mBlackBoxConnectionManager);
    }

    @Test
    public void testGetDataAlreadyConnected() {
        // given
        final int boxId = 123123;

        when(mBlackBoxConnectionManager.isConnected()).thenReturn(true);
        when(mBlackBoxConnectionManager.getDataObservable()).thenReturn(Observable.just(new BlackBoxModel()));

        TestObserver<BlackBoxModel> testObserver = TestObserver.create();

        // when
        mBlackBoxInteractor.getData(boxId).subscribe(testObserver);

        // then
        verify(mBlackBoxConnectionManager).isConnected();
        verify(mBlackBoxConnectionManager).getDataObservable();
    }

    @Test
    public void testGetDataNotYetConnected() {
        // given
        final int boxId = 123123;

        BlackBoxInteractor interactorSpy = Mockito.spy(mBlackBoxInteractor);

        when(mBlackBoxConnectionManager.isConnected()).thenReturn(false);
        when(mBlackBoxConnectionManager.getDataObservable()).thenReturn(Observable.just(new BlackBoxModel()));

        when(mBlackBoxConnectionManager.connectBlackBox(anyInt()))
                .thenReturn(Observable.just(mBlackBoxConnectionManager));

        TestObserver<BlackBoxModel> testObserver = TestObserver.create();

        // when
        interactorSpy.getData(boxId).subscribe(testObserver);

        // then
        verify(mBlackBoxConnectionManager).isConnected();
        verify(mBlackBoxConnectionManager).getDataObservable();
        verify(mBlackBoxConnectionManager).connectBlackBox(eq(boxId));
    }

    @Test
    public void testGetDataBoxError() {
        // given
        final int boxId = 123123;

        Exception boxException = new RuntimeException("box exception");

        when(mBlackBoxConnectionManager.isConnected()).thenReturn(false);
        when(mBlackBoxConnectionManager.connectBlackBox(anyInt())).thenReturn(Observable.error(boxException));

        TestObserver<BlackBoxModel> testObserver = TestObserver.create();

        // when
        mBlackBoxInteractor.getData(boxId).subscribe(testObserver);

        // then
        testObserver.assertError(boxException);
    }

}
