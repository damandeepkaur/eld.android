package com.bsmwireless.domain.interactors;

import com.bsmwireless.common.App;
import com.bsmwireless.common.Constants;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.models.InspectionReport;
import com.bsmwireless.models.SyncInspectionCategory;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;
import io.reactivex.Scheduler;

public class InspectionsInteractor {

    @Inject
    ServiceApi mServiceApi;

    @Inject
    @Named(Constants.IO_THREAD)
    Scheduler mIoThread;

    @Inject
    PreferencesManager mPreferencesManager;

    public InspectionsInteractor() {
        App.getComponent().inject(this);
    }

    public Observable<List<SyncInspectionCategory>> getInspectionItemsByCategoryIds(String categoryIds) {
        int boxId = mPreferencesManager.getSelectedBoxId();
        if (boxId == PreferencesManager.NOT_FOUND_VALUE) {
            return Observable.error(new Throwable("Not found selected boxId"));
        } else {
            return mServiceApi.getInspectionItemsByCategoryIds(boxId, categoryIds).subscribeOn(mIoThread);
        }
    }

    public Observable<List<SyncInspectionCategory>> getInspectionItemsByLastUpdate(long lastUpdate) {
        int boxId = mPreferencesManager.getSelectedBoxId();
        if (boxId == PreferencesManager.NOT_FOUND_VALUE) {
            return Observable.error(new Throwable("Not found selected boxId"));
        } else {
            return mServiceApi.getInspectionItemsByLastUpdate(boxId, lastUpdate).subscribeOn(mIoThread);
        }
    }

    public Observable<InspectionReport> syncInspectionReport(long lastUpdate, int isTrailer, long beginDate) {
        int boxId = mPreferencesManager.getSelectedBoxId();
        if (boxId == PreferencesManager.NOT_FOUND_VALUE) {
            return Observable.error(new Throwable("Not found selected boxId"));
        } else {
            return mServiceApi.syncInspectionReport(lastUpdate, isTrailer, beginDate, boxId).subscribeOn(mIoThread);
        }
    }
}
