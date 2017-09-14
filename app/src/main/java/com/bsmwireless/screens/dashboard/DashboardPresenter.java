package com.bsmwireless.screens.dashboard;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import timber.log.Timber;

@ActivityScope
public class DashboardPresenter {
    private DashboardView mView;

    private DutyTypeManager mDutyTypeManager;
    private ELDEventsInteractor mEventsInteractor;

    private DutyTypeManager.DutyTypeListener mListener = dutyType -> mView.setDutyType(dutyType);

    @Inject
    public DashboardPresenter(DashboardView view, DutyTypeManager dutyTypeManager, ELDEventsInteractor eventsInteractor) {
        mView = view;

        mDutyTypeManager = dutyTypeManager;
        mEventsInteractor = eventsInteractor;

        Timber.d("CREATED");
    }

    void onResume() {
        mView.setDutyType(mDutyTypeManager.getDutyType());
        mDutyTypeManager.addListener(mListener);
    }

    void onPause() {
        mDutyTypeManager.removeListener(mListener);
    }

    public void onDestroy() {
        Timber.d("DESTROYED");
    }

    long getDutyTypeTime(DutyType dutyType) {
        return mDutyTypeManager.getDutyTypeTime(dutyType);
    }

    void onDutyClick() {
        if (mEventsInteractor.isConnected()) {
            mView.showDutyTypeDialog();
        } else {
            mView.showNotInVehicleDialog();
        }
    }

    DashboardView.Error validateComment(String comment) {
        if (comment.length() < 4) {
            return DashboardView.Error.INVALID_COMMENT_LENGTH;
        }
        Pattern pattern = Pattern.compile("[^A-Za-z0-9`!@#$%^&* ()_\\-+=\\[\\]\\\\/?><.,;:'|\"{}~]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(comment);
        if (matcher.find()) {
            return DashboardView.Error.INVALID_COMMENT;
        }
        return DashboardView.Error.VALID_COMMENT;
    }
}
