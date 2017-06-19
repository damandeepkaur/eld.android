package com.bsmwireless.screens.login;

import com.bsmwireless.common.App;
import com.bsmwireless.common.Constants;
import com.bsmwireless.domain.interactors.LoginUserInteractor;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class LoginPresenter {

    @Inject
    @Named(Constants.UI_THREAD)
    Scheduler mUiThread;

    LoginView mView;

    LoginUserInteractor mLoginUserInteractor;

    CompositeDisposable mDisposables;

    public LoginPresenter(LoginView view, LoginUserInteractor interactor) {
        App.getComponent().inject(this);

        mView = view;
        mLoginUserInteractor = interactor;
        mDisposables = new CompositeDisposable();

        Timber.d("LoginPresenter() CREATED");
    }

    public void onForgotPasswordButtonClicked() {
        Timber.d("onForgotPasswordButtonClicked()");
        mView.goToForgotPasswordScreen();
    }

    public void onLoginButtonClicked(boolean keepToken) {
        Timber.d("onLoginButtonClicked()");

        String username = mView.getUsername();
        String password = mView.getPassword();
        String domain = mView.getDomain();

        if (username != null && username.isEmpty()) {
            mView.showErrorMessage("Username Required");
            return;
        } else if (password.isEmpty()) {
            mView.showErrorMessage("Password Required");
            return;
        } else if (domain.isEmpty()) {
            mView.showErrorMessage("Domain Required");
            return;
        }

        Disposable disposable = mLoginUserInteractor.loginUser(username, password, domain, keepToken)
                .observeOn(mUiThread)
                .subscribe(
                        status -> {
                            Timber.i("LoginUser status = %b", status);

                            if (status) {
                                mView.goToMainScreen();
                            } else {
                                mView.showErrorMessage("Login failed");
                            }
                        },
                        error -> {
                            Timber.e("LoginUser error: %s", error);
                            mView.showErrorMessage("Exception:" + error.toString());
                        }
                );
        mDisposables.add(disposable);
    }

    public void onDestroy() {
        mDisposables.dispose();
        Timber.d("LoginPresenter() DESTROYED");
    }

    public void onLoadUserData() {
        mView.loadUserData(mLoginUserInteractor.getUserName(), mLoginUserInteractor.getDomainName());
    }
}
