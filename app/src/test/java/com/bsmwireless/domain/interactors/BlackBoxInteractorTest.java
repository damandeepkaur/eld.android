package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.blackbox.BlackBox;
import com.bsmwireless.data.network.blackbox.BlackBoxConnectionManager;
import com.bsmwireless.models.BlackBoxModel;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import app.bsmuniversal.com.RxSchedulerRule;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
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

    @Mock
    BlackBox mBlackBox;


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
                .thenReturn(Single.just(mBlackBoxConnectionManager));

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
        when(mBlackBoxConnectionManager.connectBlackBox(anyInt())).thenReturn(Single.error(boxException));

        TestObserver<BlackBoxModel> testObserver = TestObserver.create();

        // when
        mBlackBoxInteractor.getData(boxId).subscribe(testObserver);

        // then
        testObserver.assertError(boxException);
    }

    @Test
    public void testGetLastData() {
        // given
        BlackBoxModel blackBoxModel = new BlackBoxModel();

        when(mBlackBoxConnectionManager.getBlackBox()).thenReturn(mBlackBox);
        when(mBlackBox.getBlackBoxState()).thenReturn(blackBoxModel);

        // when
        mBlackBoxInteractor.getLastData();

        // then
        verify(mBlackBoxConnectionManager).getBlackBox();
        verify(mBlackBox).getBlackBoxState();
    }

    @Test
    public void testGetLastDataNullBox() {
        // given
        BlackBoxModel blackBoxModel = new BlackBoxModel();

        when(mBlackBoxConnectionManager.getBlackBox()).thenReturn(null);
        when(mBlackBox.getBlackBoxState()).thenReturn(blackBoxModel);

        // when
        BlackBoxModel result = mBlackBoxInteractor.getLastData();

        // then
        verify(mBlackBoxConnectionManager).getBlackBox();
        verify(mBlackBox, never()).getBlackBoxState();
        assertNotNull(result);
    }

    @Test
    public void testShutdown() {
        // given
        String emittedString = "box off";
        Boolean emittedBoolean = true;
        Integer emittedInteger = 1231241;

        TestObserver<String> testObserverString = TestObserver.create();
        TestObserver<Boolean> testObserverBoolean = TestObserver.create();
        TestObserver<Integer> testObserverInteger = TestObserver.create();

        when(mBlackBoxConnectionManager.disconnectBlackBox())
                .thenReturn(Single.just(mBlackBoxConnectionManager));

        // when
        Observable<String> observableString = mBlackBoxInteractor.shutdown(emittedString);
        Observable<Boolean> observableBoolean = mBlackBoxInteractor.shutdown(emittedBoolean);
        Observable<Integer> observableInteger = mBlackBoxInteractor.shutdown(emittedInteger);

        observableString.subscribe(testObserverString);
        observableBoolean.subscribe(testObserverBoolean);
        observableInteger.subscribe(testObserverInteger);

        // then
        testObserverString.assertResult(emittedString);
        testObserverBoolean.assertResult(emittedBoolean);
        testObserverInteger.assertResult(emittedInteger);
    }

    @Test
    public void testShutdownBoxError() {
        // given
        Exception error = new Exception("sorry Dave, I'm afraid I can't do that");
        when(mBlackBoxConnectionManager.disconnectBlackBox()).thenReturn(Single.error(error));
        TestObserver<String> testObserverString = TestObserver.create();

        // when
        mBlackBoxInteractor.shutdown("any string").subscribe(testObserverString);

        // then
        testObserverString.assertError(error);
    }

    @Test
    public void testGetVinNumber() {
        // given
        String expectedVin = "SCAZY19C3WCX80710";
        when(mBlackBoxConnectionManager.getBlackBox()).thenReturn(mBlackBox);
        when(mBlackBox.getVinNumber()).thenReturn(expectedVin);

        // when
        String result = mBlackBoxInteractor.getVinNumber();

        // then
        assertEquals(expectedVin, result);
        verify(mBlackBox).getVinNumber();
    }

    @Test
    public void testGetVinNumberNullBox() {
        // given
        String expectedVin = "";
        when(mBlackBoxConnectionManager.getBlackBox()).thenReturn(null);

        // when
        String result = mBlackBoxInteractor.getVinNumber();

        // then
        assertEquals(expectedVin, result);
        verify(mBlackBox, never()).getVinNumber();
    }

}
