package com.bsmwireless.screens.editevent;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.common.menu.BaseMenuView;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.bsmwireless.common.Constants.COMMENT_VALIDATE_PATTERN;

@ActivityScope
public class EditEventPresenter extends BaseMenuPresenter {

    private EditEventView mView;
    private ELDEvent mELDEvent;
    private String mTimezone;
    private long mEventDay;
    private Calendar mCalendar;


    @Inject
    public EditEventPresenter(EditEventView view,
                              UserInteractor userInteractor,
                              ELDEventsInteractor eventsInteractor,
                              DutyTypeManager dutyTypeManager,
                              AccountManager accountManager) {
        super(dutyTypeManager, eventsInteractor, userInteractor, accountManager);
        mView = view;
        mTimezone = TimeZone.getDefault().getID();
        mCalendar = Calendar.getInstance();

        Timber.d("CREATED");
    }

    public void onViewCreated() {
        add(getUserInteractor().getTimezone()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timezone -> {
                    mTimezone = timezone;
                    mCalendar = Calendar.getInstance(TimeZone.getTimeZone(mTimezone));
                    mView.getExtrasFromIntent();
                }));
    }

    @Override
    protected BaseMenuView getView() {
        return mView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.d("DESTROYED");
    }

    public void onStartTimeClick(String time) {
        mCalendar.setTimeInMillis(DateUtils.convertStringAMPMToTime(time, mEventDay, mTimezone));
        int hours = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minutes = mCalendar.get(Calendar.MINUTE);
        mView.openTimePickerDialog((view, hourOfDay, minute) -> {
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);
            mCalendar.set(Calendar.SECOND, 0);
            mView.setStartTime(DateUtils.convertTimeToAMPMString(mCalendar.getTimeInMillis(), mTimezone));
        }, hours, minutes);
    }

    public void onSaveClick(DutyType type, String startTime, String comment) {
        ArrayList<ELDEvent> events = new ArrayList<>();
        long eventTime = DateUtils.convertStringAMPMToTime(startTime, mEventDay, mTimezone);
        ELDEvent newEvent;

        if (eventTime > System.currentTimeMillis()) {
            mView.showError(EditEventView.Error.ERROR_INVALID_TIME);
            return;
        }

        EditEventView.Error commentValidation = validateComment(comment);
        if (!commentValidation.equals(EditEventView.Error.VALID_COMMENT)) {
            mView.showError(commentValidation);
            return;
        }

        if (mELDEvent != null) {
            newEvent = mELDEvent.clone();
            mELDEvent.setStatus(ELDEvent.StatusCode.INACTIVE_CHANGED.getValue());
            mELDEvent.setId(null);
            events.add(mELDEvent);
        } else {
            newEvent = getEventsInteractor().getEvent(type);
        }

        newEvent.setStatus(ELDEvent.StatusCode.ACTIVE.getValue());
        newEvent.setEventType(type.getType());
        newEvent.setEventCode(type.getCode());
        newEvent.setEventTime(eventTime);
        newEvent.setComment(comment);

        // Need set mobile time for new event or take it from mELDEvent
        if (mELDEvent == null) {
            newEvent.setMobileTime(eventTime);
        }

        events.add(newEvent);

        mView.changeEvent(events);
    }

    public void setEvent(ELDEvent event) {
        mELDEvent = event;
        if (mELDEvent != null) {
            DutyType type = DutyType.getTypeByCode(event.getEventType(), event.getEventCode());
            mView.setStatus(type);

            Long time = mELDEvent.getEventTime();
            mView.setStartTime(DateUtils.convertTimeToAMPMString(time, mTimezone));

            String address = event.getLocation();
            mView.setAddress(address);

            String comment = event.getComment();
            mView.setComment(comment);

            mEventDay = event.getEventTime();
        }
    }

    public void setDayTime(long dayTime) {
        mEventDay = dayTime;
    }

    private EditEventView.Error validateComment(String comment) {
        if (comment.length() < 4) {
            return EditEventView.Error.INVALID_COMMENT_LENGTH;
        }
        Matcher matcher = COMMENT_VALIDATE_PATTERN.matcher(comment);
        if (matcher.find()) {
            return EditEventView.Error.INVALID_COMMENT;
        }
        return EditEventView.Error.VALID_COMMENT;
    }
}
