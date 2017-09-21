package com.bsmwireless.common.utils.observers;

import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public final class DutyManagerObservable extends Observable<DutyType> {

    public static Observable<DutyType> create(DutyTypeManager dutyTypeManager) {
        return new DutyManagerObservable(dutyTypeManager);
    }

    private final DutyTypeManager manager;

    private DutyManagerObservable(DutyTypeManager dutyTypeManager) {
        this.manager = dutyTypeManager;
    }

    @Override
    protected void subscribeActual(Observer<? super DutyType> observer) {
        DutyManagerListener listener = new DutyManagerListener(manager, observer);
        observer.onSubscribe(listener);
        manager.addListener(listener);
    }

    private final static class DutyManagerListener implements DutyTypeManager.DutyTypeListener, Disposable {

        private final DutyTypeManager dutyTypeManager;
        private final Observer<? super DutyType> observer;
        private final AtomicBoolean isDisposed;

        DutyManagerListener(DutyTypeManager dutyTypeManager, Observer<? super DutyType> observer) {
            this.dutyTypeManager = dutyTypeManager;
            this.observer = observer;
            isDisposed = new AtomicBoolean(false);
        }

        @Override
        public void onDutyTypeChanged(DutyType dutyType) {
            if (!isDisposed()) {
                observer.onNext(dutyType);
            }
        }

        @Override
        public void dispose() {
            dutyTypeManager.removeListener(this);
            isDisposed.set(true);
        }

        @Override
        public boolean isDisposed() {
            return isDisposed.get();
        }
    }
}
