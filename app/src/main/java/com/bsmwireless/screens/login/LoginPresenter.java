package com.bsmwireless.screens.login;

import com.bsmwireless.common.Constants;
import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.domain.interactors.LoginUserInteractor;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

@ActivityScope
public class LoginPresenter {

    private final Scheduler mUiThread;

    private final LoginView mView;

    private final LoginUserInteractor mLoginUserInteractor;

    private final CompositeDisposable mDisposables;

    @Inject
    public LoginPresenter(LoginView view, LoginUserInteractor interactor, @Named(Constants.UI_THREAD) Scheduler uiThread) {
        mView = view;
        mLoginUserInteractor = interactor;
        mDisposables = new CompositeDisposable();
        mUiThread = uiThread;

        Timber.d("CREATED");
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

        Disposable disposable = mLoginUserInteractor.loginUser(username, password, domain, keepToken)
                          .observeOn(mUiThread)
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
}
