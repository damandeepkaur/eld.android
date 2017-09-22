package com.bsmwireless.data.storage;

import android.os.Handler;
import android.support.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;

public class AccountManager {

    private PreferencesManager mPreferencesManager;

    private final Set<AccountListener> mListeners = new HashSet<>();

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

    /**
     * Check current driver and current user.
     * @return true if current user is a driver
     */
    public boolean isCurrentUserDriver() {
        return getCurrentUserId() == getCurrentDriverId();
    }

    public void addListener(@NonNull AccountListener listener) {
        mHandler.post(() -> {
            synchronized (mListeners) {
                mListeners.add(listener);
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
