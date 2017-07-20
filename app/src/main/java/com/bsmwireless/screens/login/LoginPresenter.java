package com.bsmwireless.screens.login;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.domain.interactors.LoginUserInteractor;
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
    private LoginUserInteractor mLoginUserInteractor;
    private CompositeDisposable mDisposables;

    @Inject
    public LoginPresenter(LoginView view, LoginUserInteractor interactor) {
        mView = view;
        mLoginUserInteractor = interactor;
        mDisposables = new CompositeDisposable();

        Timber.d("CREATED");
    }

    public void onLoginButtonClicked(boolean keepToken) {
        Timber.d("onLoginButtonClicked()");

        String username = mView.getUsername();
        String password = mView.getPassword();
        String domain = mView.getDomain();

        if (username == null || username.isEmpty()) {
            mView.showErrorMessage("Username Required");
            return;
        } else if (password == null || password.isEmpty()) {
            mView.showErrorMessage("Password Required");
            return;
        } else if (domain == null || domain.isEmpty()) {
            mView.showErrorMessage("Domain Required");
            return;
        }
        mView.setLoginButtonEnabled(false);

        Disposable disposable = mLoginUserInteractor.loginUser(username, password, domain, keepToken, User.DriverType.DRIVER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        status -> {
                            Timber.i("LoginUser status = %b", status);

                            if (status) {
                                mView.goToMainScreen();
                            } else {
                                mView.showErrorMessage("Login failed");
                                mView.setLoginButtonEnabled(true);
                            }
                        },
                        error -> {
                            Timber.e("LoginUser error: %s", error);
                            mView.showErrorMessage("Exception:" + error.toString());
                            mView.setLoginButtonEnabled(true);
                        }
                );
        mDisposables.add(disposable);
    }

    public void onDestroy() {
        mDisposables.dispose();

        Timber.d("DESTROYED");
    }

    public void onLoadUserData() {
        mView.loadUserData(mLoginUserInteractor.getUserName(), mLoginUserInteractor.getDomainName());
    }

    public void onExitUser() {
        mLoginUserInteractor.removeAccount();
    }
}
