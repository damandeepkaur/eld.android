package com.bsmwireless.screens.hoursofservice;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.BlackBoxInteractor;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.common.menu.BaseMenuView;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@ActivityScope
public final class HoursOfServicePresenter extends BaseMenuPresenter {

    private final HoursOfServiceView mView;
    private final BlackBoxInteractor mBlackBoxInteractor;

    @Inject
    public HoursOfServicePresenter(DutyTypeManager dutyTypeManager,
                                   ELDEventsInteractor eventsInteractor,
                                   UserInteractor userInteractor,
                                   AccountManager mAccountManager,
                                   HoursOfServiceView view, BlackBoxInteractor blackBoxInteractor) {
        super(dutyTypeManager, eventsInteractor, userInteractor, mAccountManager);
        mView = view;
        mBlackBoxInteractor = blackBoxInteractor;
    }

    public void loadTitle() {
        Disposable disposable = Single.zip(loadDriverName(), loadBlackboxName(), Result::new)
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> {
                    Timber.e(throwable, "Error load driver info");
                    return new Result("", -1L);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> mView.setTitle(result.boxId, result.driverName));
        add(disposable);
    }

    @Override
    protected BaseMenuView getView() {
        return mView;
    }

    @Override
    public void onDriverChanged() {
        super.onDriverChanged();
        loadTitle();
    }

    private Single<String> loadDriverName(){
        return getUserInteractor()
                .getFullDriverName()
                .first("");
    }

    private Single<Long> loadBlackboxName(){
        return Single.fromCallable(mBlackBoxInteractor::getLastData)
                .map(BlackBoxModel::getBoxId)
                .onErrorReturnItem(-1L);
    }

    private static final class Result {
        final String driverName;
        final long boxId;

        private Result(String driverName, long boxId) {
            this.driverName = driverName;
            this.boxId = boxId;
        }
    }
}
