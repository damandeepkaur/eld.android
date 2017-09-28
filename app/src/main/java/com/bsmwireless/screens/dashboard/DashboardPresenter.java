package com.bsmwireless.screens.dashboard;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.regex.Matcher;

import javax.inject.Inject;

import timber.log.Timber;

import static com.bsmwireless.common.Constants.COMMENT_VALIDATE_PATTERN;

@ActivityScope
public final class DashboardPresenter {
    private DashboardView mView;

    private DutyTypeManager mDutyTypeManager;
    private ELDEventsInteractor mEventsInteractor;

    private DutyTypeManager.DutyTypeListener mListener = this::onSetDutyType;

    @Inject
    public DashboardPresenter(DashboardView view, DutyTypeManager dutyTypeManager, ELDEventsInteractor eventsInteractor) {
        mView = view;

        mDutyTypeManager = dutyTypeManager;
        mEventsInteractor = eventsInteractor;

        mDutyTypeManager.addListener(mListener);

        Timber.d("CREATED");
    }

    public void onDestroy() {
        mDutyTypeManager.removeListener(mListener);
        Timber.d("DESTROYED");
    }

    long getDutyTypeTime(DutyType dutyType) {
        return mDutyTypeManager.getDutyTypeTime(dutyType);
    }

    void onDutyClick() {
        mView.showDutyTypeDialog();
    }

    DashboardView.Error validateComment(String comment) {
        if (comment.length() < 4) {
            return DashboardView.Error.INVALID_COMMENT_LENGTH;
        }
        Matcher matcher = COMMENT_VALIDATE_PATTERN.matcher(comment);
        if (matcher.find()) {
            return DashboardView.Error.INVALID_COMMENT;
        }
        return DashboardView.Error.VALID_COMMENT;
    }

    private void onSetDutyType(DutyType dutyType) {
        mView.setDutyType(dutyType);

        if (mEventsInteractor.isConnected() && dutyType != DutyType.PERSONAL_USE && dutyType != DutyType.YARD_MOVES) {
            mView.showSpecialStatuses();
        } else {
            mView.hideSpecialStatuses();
        }
    }
}
