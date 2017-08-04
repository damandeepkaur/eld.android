package com.bsmwireless.screens.autologout;

import android.app.job.JobParameters;
import android.content.Intent;

public interface AutoLogoutView {

    void goToLoginScreen();

    void showAutoLogoutDialog(JobParameters jobParameters, Intent intent);

    void initAutoLogoutIfNoUserInteraction(JobParameters jobParameters, Intent intent);
}
