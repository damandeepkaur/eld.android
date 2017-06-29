package com.bsmwireless.domain.interactors;

import com.bsmwireless.common.App;
import com.bsmwireless.common.Constants;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.models.SyncInspectionCategory;
import com.bsmwireless.models.ELDDriverStatus;
import com.bsmwireless.models.ResponseMessage;

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
        return mServiceApi.getInspectionItemsByCategoryIds(mPreferencesManager.getSelectedBoxId(), categoryIds).subscribeOn(mIoThread);
    }

    public Observable<List<SyncInspectionCategory>> getInspectionItemsByLastUpdate(long lastUpdate) {
        return mServiceApi.getInspectionItemsByLastUpdate(mPreferencesManager.getSelectedBoxId(), lastUpdate).subscribeOn(mIoThread);
    }
}
