package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.models.InspectionReport;
import com.bsmwireless.models.SyncInspectionCategory;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import app.bsmuniversal.com.RxSchedulerRule;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for InspectionsInteractor.
 */
@RunWith(MockitoJUnitRunner.class)
public class InspectionsInteractorTest {

    final String mCategoryIds = "111,222,333"; // example list of category ids in expected format
    final int mIdNotFoundValue = -1; // hard-coded in order to trigger test fail if PreferencesManager.NOT_FOUND_VALUE changes in the future

    @ClassRule
    public static final RxSchedulerRule RULE = new RxSchedulerRule();

    @Mock
    ServiceApi mServiceApi;

    @Mock
    PreferencesManager mPreferencesManager;

    InspectionsInteractor mInspectionsInteractor;

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);

        mInspectionsInteractor = new InspectionsInteractor(mServiceApi, mPreferencesManager);
    }

    @Test
    public void testGetInspectionItemsByCategoryIds() {
        // given
        List<SyncInspectionCategory> listSyncInspectionCategory = new ArrayList<>();

        when(mPreferencesManager.getBoxId()).thenReturn(123450); // != PreferencesManager.NOT_FOUND_VALUE
        when(mServiceApi.getInspectionItemsByCategoryIds(anyString())).thenReturn(Single.just(listSyncInspectionCategory));

        TestObserver<List<SyncInspectionCategory>> testObserver = TestObserver.create();

        // when
        mInspectionsInteractor.getInspectionItemsByCategoryIds(mCategoryIds).subscribe(testObserver);

        // then
        verify(mServiceApi).getInspectionItemsByCategoryIds(eq(mCategoryIds));
    }

    @Test
    public void testGetInspectionItemsByCategoryIdsNoBoxId() {
        // given
        when(mPreferencesManager.getBoxId()).thenReturn(mIdNotFoundValue);

        TestObserver<List<SyncInspectionCategory>> testObserver = TestObserver.create();

        // when
        mInspectionsInteractor.getInspectionItemsByCategoryIds(mCategoryIds).subscribe(testObserver);

        // then
        testObserver.assertError(throwable -> {

                // TODO: make stricter test criteria if needed
                return throwable.getMessage().length() > 0; // for now, we just care that some message was thrown
        });
    }

    @Test
    public void testGetInspectionItemsByCategoryIdApiError() {
        // given
        Exception fakeApiException = new RuntimeException("the API said no.");

        when(mPreferencesManager.getBoxId()).thenReturn(123450); // != PreferencesManager.NOT_FOUND_VALUE
        when(mServiceApi.getInspectionItemsByCategoryIds(anyString())).thenReturn(Single.error(fakeApiException));

        TestObserver<List<SyncInspectionCategory>> testObserver = TestObserver.create();

        // when
        mInspectionsInteractor.getInspectionItemsByCategoryIds(mCategoryIds).subscribe(testObserver);

        // then
        verify(mServiceApi).getInspectionItemsByCategoryIds(eq(mCategoryIds));
        testObserver.assertError(fakeApiException);
    }

    @Test
    public void testGetInspectionItemsByLastUpdate() {
        // given
        final long lastUpdate = 1438243200L;
        List<SyncInspectionCategory> listSyncInspectionCategory = new ArrayList<>();

        when(mPreferencesManager.getBoxId()).thenReturn(1111);
        when(mServiceApi.getInspectionItemsByLastUpdate(anyLong())).thenReturn(Single.just(listSyncInspectionCategory));

        TestObserver<List<SyncInspectionCategory>> testObserver = TestObserver.create();

        // when
        mInspectionsInteractor.getInspectionItemsByLastUpdate(lastUpdate).subscribe(testObserver);

        // then
        verify(mServiceApi).getInspectionItemsByLastUpdate(eq(lastUpdate));
    }

    @Test
    public void testGetInspectionItemsByLastUpdateNoBoxId() {
        // given
        final long lastUpdate = 1438243200L;

        when(mPreferencesManager.getBoxId()).thenReturn(mIdNotFoundValue);

        TestObserver<List<SyncInspectionCategory>> testObserver = TestObserver.create();

        // when
        mInspectionsInteractor.getInspectionItemsByLastUpdate(lastUpdate).subscribe(testObserver);

        // then
        testObserver.assertError(throwable -> {
                // TODO: make test criteria stricter/more specific if needed
                return throwable.getMessage().length() > 0; // for now, just test for error with non-empty message
        });
    }

    @Test
    public void testGetInspectionItemsByLastUpdateApiError() {
        // given
        final long lastUpdate = 1438243200L;
        Exception fakeApiException = new RuntimeException("404 API not found");

        when(mPreferencesManager.getBoxId()).thenReturn(12312123);
        when(mServiceApi.getInspectionItemsByLastUpdate(any(Long.class))).thenReturn(Single.error(fakeApiException));

        TestObserver<List<SyncInspectionCategory>> testObserver = TestObserver.create();

        // when
        mInspectionsInteractor.getInspectionItemsByLastUpdate(lastUpdate).subscribe(testObserver);

        // then
        verify(mServiceApi).getInspectionItemsByLastUpdate(eq(lastUpdate));
        testObserver.assertError(Throwable.class);
    }

    @Test
    public void testSyncInspectionReport() {
        // given
        final long lastUpdate = 1232470800L;
        final int isTrailer = 1; // 1 trailer, 0 regular vehicle
        final long beginDate = 1234567890L;

        when(mPreferencesManager.getBoxId()).thenReturn(1234);
        when(mServiceApi.syncInspectionReport(anyLong(), anyInt(), anyLong())).thenReturn(Single.just(new InspectionReport()));

        TestObserver<InspectionReport> testObserver = TestObserver.create();

        // when
        mInspectionsInteractor.syncInspectionReport(lastUpdate, isTrailer, beginDate).subscribe(testObserver);

        // then
        verify(mServiceApi).syncInspectionReport(eq(lastUpdate), eq(isTrailer), eq(beginDate));
    }

    @Test
    public void testSyncInspectionReportNoBoxId() {
        // given
        final long lastUpdate = 1232470800L;
        final int isTrailer = 1; // 1 trailer, 0 regular vehicle
        final long beginDate = 1234567890L;

        when(mPreferencesManager.getBoxId()).thenReturn(mIdNotFoundValue);

        TestObserver<InspectionReport> testObserver = TestObserver.create();

        // when
        mInspectionsInteractor.syncInspectionReport(lastUpdate, isTrailer, beginDate).subscribe(testObserver);

        // then
        testObserver.assertError(throwable -> throwable.getMessage().length() > 0);
    }

    @Test
    public void testSyncInspectionReportApiError() {
        // given
        final long lastUpdate = 1232470800L;
        final int isTrailer = 0; // 1 trailer, 0 regular vehicle
        final long beginDate = 1234567890L;

        Exception fakeApiException = new RuntimeException("500 from server");

        when(mPreferencesManager.getBoxId()).thenReturn(2010);
        when(mServiceApi.syncInspectionReport(anyLong(), anyInt(), anyLong())).thenReturn(Single.error(fakeApiException));

        TestObserver<InspectionReport> testObserver = TestObserver.create();

        // when
        mInspectionsInteractor.syncInspectionReport(lastUpdate, isTrailer, beginDate).subscribe(testObserver);

        // then
        verify(mServiceApi).syncInspectionReport(eq(lastUpdate), eq(isTrailer), eq(beginDate));
        testObserver.assertError(fakeApiException);
    }
}
