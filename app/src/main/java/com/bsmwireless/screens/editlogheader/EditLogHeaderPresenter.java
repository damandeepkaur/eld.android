package com.bsmwireless.screens.editlogheader;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.common.menu.BaseMenuView;
import com.bsmwireless.screens.logs.LogHeaderModel;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@ActivityScope
public class EditLogHeaderPresenter extends BaseMenuPresenter implements AccountManager.AccountListener {

    private EditLogHeaderView mView;
    private LogHeaderModel mLogHeaderModel;

    @Inject
    public EditLogHeaderPresenter(EditLogHeaderView view, UserInteractor userInteractor,
                                  DutyTypeManager dutyTypeManager, AccountManager accountManager) {
        mView = view;
        mDisposables = new CompositeDisposable();
        mUserInteractor = userInteractor;
        mDutyTypeManager = dutyTypeManager;
        mAccountManager = accountManager;

        Timber.d("CREATED");
    }

    public void onViewCreated(LogHeaderModel logHeaderModel) {
        mLogHeaderModel = logHeaderModel;
        mView.setLogHeaderModel(logHeaderModel);
        mAccountManager.addListener(this);
        if (!mAccountManager.isCurrentUserDriver()) {
            Disposable disposable = Single.fromCallable(() -> mUserInteractor.getFullUserNameSync())
                                          .subscribeOn(Schedulers.io())
                                          .observeOn(AndroidSchedulers.mainThread())
                                          .subscribe(name -> mView.showCoDriverView(name));
            mDisposables.add(disposable);
        } else {
            mView.hideCoDriverView();
        }
    }

    public void onSaveLogHeaderButtonClicked() {
        LogHeaderModel logHeaderModel = mView.getLogHeader(mLogHeaderModel);
        mView.saveLogHeader(logHeaderModel);
    }

    @Override
    protected BaseMenuView getView() {
        return mView;
    }

    public void onDestroy() {
        super.onDestroy();
        Timber.d("DESTROYED");
        mAccountManager.removeListener(this);
    }

    @Override
    public void onUserChanged() {
        if (!mAccountManager.isCurrentUserDriver()) {
            Disposable disposable = Single.fromCallable(() -> mUserInteractor.getFullUserNameSync())
                                          .subscribeOn(Schedulers.io())
                                          .observeOn(AndroidSchedulers.mainThread())
                                          .subscribe(name -> mView.showCoDriverView(name));
            mDisposables.add(disposable);
        } else {
            mView.hideCoDriverView();
        }
    }

    @Override
    public void onDriverChanged() {}
}
