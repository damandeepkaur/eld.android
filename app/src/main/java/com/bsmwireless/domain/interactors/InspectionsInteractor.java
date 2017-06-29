package com.bsmwireless.domain.interactors;

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

    private final ServiceApi mServiceApi;

    private final Scheduler mIoThread;

    private final PreferencesManager mPreferencesManager;

    @Inject
    public InspectionsInteractor(ServiceApi serviceApi, @Named(Constants.IO_THREAD) Scheduler ioThread, PreferencesManager preferencesManager) {
        this.mServiceApi = serviceApi;
        this.mIoThread = ioThread;
        this.mPreferencesManager = preferencesManager;
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
