package com.bsmwireless.screens.login;

import android.content.Context;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.NetworkUtils;
import com.bsmwireless.data.network.RetrofitException;
import com.bsmwireless.domain.interactors.LoginUserInteractor;
import com.bsmwireless.models.User;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@ActivityScope
public class LoginPresenter {
    private Context mContext;
    private LoginView mView;
    private LoginUserInteractor mLoginUserInteractor;
    private CompositeDisposable mDisposables;

    @Inject
    public LoginPresenter(Context context, LoginView view, LoginUserInteractor interactor) {
        mContext = context;
        mView = view;
        mLoginUserInteractor = interactor;
        mDisposables = new CompositeDisposable();

        Timber.d("CREATED");
    }

    public void onViewCreated() {
        if (mLoginUserInteractor.isLoginActive()) {
            mView.goToNavigationScreen();
        } else if (mLoginUserInteractor.isRememberMeEnabled()) {
            mView.loadUserData(mLoginUserInteractor.getUserName(), mLoginUserInteractor.getDomainName());
        }
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
                                mView.goToSelectAssetScreen();
                            } else {
                                mView.showErrorMessage(mContext.getString(R.string.error_unexpected));
                                mView.setLoginButtonEnabled(true);
                            }
                        },
                        error -> {
                            Timber.e("LoginUser error: %s", error);
                            mView.showErrorMessage(NetworkUtils.getErrorMessage((RetrofitException) error, mContext));
                            mView.setLoginButtonEnabled(true);
                        }
                );
        mDisposables.add(disposable);
    }

    public void onDestroy() {
        mDisposables.dispose();

        Timber.d("DESTROYED");
    }
}
