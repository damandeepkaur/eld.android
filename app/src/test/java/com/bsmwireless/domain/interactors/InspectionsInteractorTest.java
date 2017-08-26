package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.models.SyncInspectionCategory;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import app.bsmuniversal.com.RxSchedulerRule;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

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
        when(mServiceApi.getInspectionItemsByCategoryIds(anyString())).thenReturn(Observable.just(listSyncInspectionCategory));

        TestObserver<List<SyncInspectionCategory>> testObserver = TestObserver.create();

        // when
        mInspectionsInteractor.getInspectionItemsByCategoryIds(mCategoryIds).subscribe(testObserver);

        // then
        verify(mServiceApi).getInspectionItemsByCategoryIds(eq(mCategoryIds));
    }

    // TODO: getInspectionItemsByCategoryIds no box id
    // TODO: getInspectionItemsByCategoryIds API error
}
