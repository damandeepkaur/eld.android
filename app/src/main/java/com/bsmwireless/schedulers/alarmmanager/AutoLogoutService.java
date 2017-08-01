package com.bsmwireless.schedulers.alarmmanager;

import android.app.IntentService;
import android.content.Intent;

import com.bsmwireless.common.App;
import com.bsmwireless.domain.interactors.LoginUserInteractor;
import com.bsmwireless.schedulers.alarmmanager.AlarmReceiver;
import com.bsmwireless.schedulers.jobscheduler.AutoLogoutJobService;
import com.bsmwireless.screens.login.LoginActivity;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class AutoLogoutService extends IntentService {

    public static final String TAG = AutoLogoutJobService.class.getSimpleName();

    @Inject
    LoginUserInteractor mLoginUserInteractor;

    private CompositeDisposable mCompositeDisposable;

    public AutoLogoutService() {
        super(TAG);
        App.getComponent().inject(this);
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Disposable disposable = mLoginUserInteractor.logoutUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        status -> {
                            Timber.i("LoginUser status = %b", status);
                            if (status) {
                                Intent loginActivityIntent = new Intent(App.getComponent().context(), LoginActivity.class);
                                loginActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                App.getComponent().context().startActivity(loginActivityIntent);

                                // manually release the WakeLock when the scheduler works ends
                                AlarmReceiver.completeWakefulIntent(intent);
                            }
                        },
                        error -> Timber.e("LoginUser error: %s", error)
                );
        mCompositeDisposable.add(disposable);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mCompositeDisposable.dispose();
        return super.onUnbind(intent);
    }
}
