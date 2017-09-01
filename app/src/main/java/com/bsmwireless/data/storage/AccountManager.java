package com.bsmwireless.data.storage;

import android.os.Handler;
import android.support.annotation.NonNull;

import java.util.ArrayList;

public class AccountManager {

    private PreferencesManager mPreferencesManager;

    private final ArrayList<AccountListener> mListeners = new ArrayList<>();

    private Handler mHandler = new Handler();
    private Runnable mNotifyUserChangedTask = () -> {
        synchronized (mListeners) {
            for (AccountListener listener : mListeners) {
                listener.onUserChanged();
            }
        }
    };
    private Runnable mNotifyDriverChangedTask = () -> {
        synchronized (mListeners) {
            for (AccountListener listener : mListeners) {
                listener.onDriverChanged();
            }
        }
    };

    public interface AccountListener {
        void onUserChanged();
        void onDriverChanged();
    }

    public AccountManager(PreferencesManager preferencesManager) {
        mPreferencesManager = preferencesManager;
    }

    public void setCurrentUser(int userId, String accountName) {
        mPreferencesManager.setUserId(userId);
        mPreferencesManager.setUserAccountName(accountName);
        notifyListeners(mNotifyUserChangedTask);
    }

    public int getCurrentUserId() {
        return mPreferencesManager.getUserId();
    }

    public void setCurrentDriver(int driverId, String accountName) {
        mPreferencesManager.setDriverId(driverId);
        mPreferencesManager.setDriverAccountName(accountName);
        notifyListeners(mNotifyDriverChangedTask);
    }

    public int getCurrentDriverId() {
        return mPreferencesManager.getDriverId();
    }

    public String getCurrentDriverAccountName() {
        return mPreferencesManager.getDriverAccountName();
    }

    public String getCurrentUserAccountName() {
        return mPreferencesManager.getUserAccountName();
    }

    public void resetUserToDriver() {
        setCurrentUser(getCurrentDriverId(), getCurrentDriverAccountName());
    }

    public boolean isCurrentUserDriver() {
        return getCurrentUserId() == getCurrentDriverId();
    }

    public void addListener(@NonNull AccountListener listener) {
        mHandler.post(() -> {
            synchronized (mListeners) {
                mListeners.add(listener);
                listener.onUserChanged();
            }
        });
    }

    public void removeListener(@NonNull AccountListener listener) {
        synchronized (mListeners) {
            mListeners.remove(listener);
        }
    }

    private void notifyListeners(Runnable task) {
        mHandler.post(task);
    }
}
