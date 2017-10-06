package com.bsmwireless.screens.transfer;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.common.menu.BaseMenuView;

import java.util.regex.Matcher;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.bsmwireless.common.Constants.COMMENT_VALIDATE_PATTERN;
import static com.bsmwireless.common.utils.DateUtils.MS_IN_DAY;

@ActivityScope
public final class TransferPresenter extends BaseMenuPresenter {

    private TransferView mView;

    @Inject
    public TransferPresenter(TransferView view,
                             UserInteractor userInteractor,
                             DutyTypeManager dutyTypeManager,
                             ELDEventsInteractor eventsInteractor,
                             AccountManager accountManager) {
        super(dutyTypeManager, eventsInteractor, userInteractor, accountManager);
        mView = view;

        Timber.d("CREATED");
    }

    @Override
    protected BaseMenuView getView() {
        return mView;
    }

    void onSendClick(String comment) {
        TransferView.Error error = validateComment(comment);

        if (error == TransferView.Error.INVALID_COMMENT) {
            mView.showError(error);
        } else {
            mView.showReportDialog();
        }
    }

    void onConfirmClick(String comment, int option) {
        getDisposables().add(getUserInteractor().getTimezone()
                .flatMap(timezone -> {
                    long endDay = DateUtils.convertTimeToLogDay(timezone, DateUtils.currentTimeMillis());
                    long startDay = DateUtils.convertTimeToLogDay(timezone, DateUtils.currentTimeMillis() - 8 * MS_IN_DAY);
                    return getEventsInteractor().sendReport(startDay, endDay, option, comment).toFlowable();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> mView.finish(),
                        throwable -> {
                            Timber.e(throwable);
                            mView.showTransferError(TransferView.Error.TRANSFER_FAILED);
                        }
                ));
    }

    private TransferView.Error validateComment(String comment) {
        if (comment.isEmpty()) {
            return TransferView.Error.VALID_COMMENT;
        }
        Matcher matcher = COMMENT_VALIDATE_PATTERN.matcher(comment);
        if (matcher.find()) {
            return TransferView.Error.INVALID_COMMENT;
        }
        return TransferView.Error.VALID_COMMENT;
    }
}
