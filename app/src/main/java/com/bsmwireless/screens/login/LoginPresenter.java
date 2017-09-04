package com.bsmwireless.screens.login;


import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.SchedulerUtils;
import com.bsmwireless.data.network.NtpClientManager;
import com.bsmwireless.data.network.RetrofitException;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.models.User;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


@ActivityScope
public class LoginPresenter {
    private LoginView mView;
    private UserInteractor mUserInteractor;
    private CompositeDisposable mDisposables;

    @Inject
    public LoginPresenter(LoginView view, UserInteractor interactor) {
        mView = view;
        mUserInteractor = interactor;
        mDisposables = new CompositeDisposable();

        Timber.d("CREATED");
    }

    public void onViewCreated() {
        if (mUserInteractor.isLoginActive()) {
            mView.goToNavigationScreen();
        } else if (mUserInteractor.isRememberMeEnabled()) {
            mView.loadUserData(mUserInteractor.getDriverName(), mUserInteractor.getDriverDomainName());
        }
    }

    public void onLoginButtonClicked(boolean keepToken) {
        Timber.d("onLoginButtonClicked()");

        String username = mView.getUsername();
        String password = mView.getPassword();
        String domain = mView.getDomain();

        if (username == null || username.isEmpty()) {
            mView.showErrorMessage(LoginView.Error.ERROR_USER);
            return;
        } else if (password == null || password.isEmpty()) {
            mView.showErrorMessage(LoginView.Error.ERROR_PASSWORD);
            return;
        } else if (domain == null || domain.isEmpty()) {
            mView.showErrorMessage(LoginView.Error.ERROR_DOMAIN);
            return;
        }
        mView.setLoginButtonEnabled(false);
        mView.showProgressBar();

        Disposable disposable = mUserInteractor.loginUser(username, password, domain, keepToken, User.DriverType.DRIVER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        status -> {
                            Timber.i("LoginUser status = %b", status);

                            if (status) {
                                SchedulerUtils.schedule();
                                mView.goToSelectAssetScreen();
                            } else {
                                mView.showErrorMessage(LoginView.Error.ERROR_UNEXPECTED);
                                mView.setLoginButtonEnabled(true);
                            }
                            mView.hideProgressBar();
                        },
                        error -> {
                            Timber.e("LoginUser error: %s", error);
                            if (error instanceof RetrofitException) {
                                mView.showErrorMessage((RetrofitException) error);
                            }
                            mView.setLoginButtonEnabled(true);
                            mView.hideProgressBar();
                        }
                );
        mDisposables.add(disposable);
    }

    public void onDestroy() {
        mDisposables.dispose();

        Timber.d("DESTROYED");
    }
}
