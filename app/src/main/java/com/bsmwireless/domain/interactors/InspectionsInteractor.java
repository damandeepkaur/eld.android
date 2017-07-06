package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.models.InspectionReport;
import com.bsmwireless.models.SyncInspectionCategory;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class InspectionsInteractor {

    private ServiceApi mServiceApi;
    private PreferencesManager mPreferencesManager;

    public InspectionsInteractor(ServiceApi serviceApi, PreferencesManager preferencesManager) {
        mServiceApi = serviceApi;
        mPreferencesManager = preferencesManager;
    }

    public Observable<List<SyncInspectionCategory>> getInspectionItemsByCategoryIds(String categoryIds) {
        int boxId = mPreferencesManager.getSelectedBoxId();
        if (boxId == PreferencesManager.NOT_FOUND_VALUE) {
            return Observable.error(new Throwable("Not found selected boxId"));
        } else {
            return mServiceApi.getInspectionItemsByCategoryIds(boxId, categoryIds).subscribeOn(Schedulers.io());
        }
    }

    public Observable<List<SyncInspectionCategory>> getInspectionItemsByLastUpdate(long lastUpdate) {
        int boxId = mPreferencesManager.getSelectedBoxId();
        if (boxId == PreferencesManager.NOT_FOUND_VALUE) {
            return Observable.error(new Throwable("Not found selected boxId"));
        } else {
            return mServiceApi.getInspectionItemsByLastUpdate(boxId, lastUpdate).subscribeOn(Schedulers.io());
        }
    }

    public Observable<InspectionReport> syncInspectionReport(long lastUpdate, int isTrailer, long beginDate) {
        int boxId = mPreferencesManager.getSelectedBoxId();
        if (boxId == PreferencesManager.NOT_FOUND_VALUE) {
            return Observable.error(new Throwable("Not found selected boxId"));
        } else {
            return mServiceApi.syncInspectionReport(lastUpdate, isTrailer, beginDate, boxId).subscribeOn(Schedulers.io());
        }
    }
}
