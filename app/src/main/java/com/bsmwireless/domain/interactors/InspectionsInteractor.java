package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.models.InspectionReport;
import com.bsmwireless.models.SyncInspectionCategory;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class InspectionsInteractor {

    private ServiceApi mServiceApi;
    private PreferencesManager mPreferencesManager;

    @Inject
    public InspectionsInteractor(ServiceApi serviceApi, PreferencesManager preferencesManager) {
        mServiceApi = serviceApi;
        mPreferencesManager = preferencesManager;
    }

    public Observable<List<SyncInspectionCategory>> getInspectionItemsByCategoryIds(String categoryIds) {
        int boxId = mPreferencesManager.getBoxId();
        if (boxId == PreferencesManager.NOT_FOUND_VALUE) {
            return Observable.error(new Throwable("Not in vehicle"));
        } else {
            return mServiceApi.getInspectionItemsByCategoryIds(categoryIds);
        }
    }

    public Observable<List<SyncInspectionCategory>> getInspectionItemsByLastUpdate(long lastUpdate) {
        int boxId = mPreferencesManager.getBoxId();
        if (boxId == PreferencesManager.NOT_FOUND_VALUE) {
            return Observable.error(new Throwable("Not in vehicle"));
        } else {
            return mServiceApi.getInspectionItemsByLastUpdate(lastUpdate);
        }
    }

    public Observable<InspectionReport> syncInspectionReport(long lastUpdate, int isTrailer, long beginDate) {
        int boxId = mPreferencesManager.getBoxId();
        if (boxId == PreferencesManager.NOT_FOUND_VALUE) {
            return Observable.error(new Throwable("Not in vehicle"));
        } else {
            return mServiceApi.syncInspectionReport(lastUpdate, isTrailer, beginDate);
        }
    }
}
