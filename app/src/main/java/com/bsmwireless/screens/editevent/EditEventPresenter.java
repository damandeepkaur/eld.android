package com.bsmwireless.screens.editevent;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.common.menu.BaseMenuView;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.Calendar;
import java.util.TimeZone;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@ActivityScope
public class EditEventPresenter extends BaseMenuPresenter {

    private EditEventView mView;
    private CompositeDisposable mDisposables;
    private ELDEvent mELDEvent;
    private String mTimezone;
    private long mEventDay;
    private Calendar mCalendar;

    private UserInteractor mUserInteractor;

    @Inject
    public EditEventPresenter(EditEventView view, UserInteractor userInteractor, ELDEventsInteractor eventsInteractor, DutyTypeManager dutyTypeManager) {
        mView = view;
        mDisposables = new CompositeDisposable();
        mUserInteractor = userInteractor;
        mEventsInteractor = eventsInteractor;
        mDutyTypeManager = dutyTypeManager;
        mTimezone = TimeZone.getDefault().getID();
        mCalendar = Calendar.getInstance();

        Timber.d("CREATED");
    }

    public void onViewCreated() {
        mDisposables.add(mUserInteractor.getTimezone()
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

    public void onDestroy() {
        mDisposables.dispose();

        Timber.d("DESTROYED");
    }

    public void onStartTimeClick(String time) {
        mCalendar.setTimeInMillis(DateUtils.convertStringAMPMToTime(time, mEventDay, mTimezone));
        int hours = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minutes = mCalendar.get(Calendar.MINUTE);
        mView.openTimePickerDialog((view, hourOfDay, minute) -> {
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);
            mView.setStartTime(DateUtils.convertTimeToAMPMString(mCalendar.getTimeInMillis(), mTimezone));
        }, hours, minutes);
    }

    public void onSaveClick(DutyType type, String startTime, String comment) {
        ELDEvent newEvent;

        if (mELDEvent != null) {
            newEvent = mELDEvent.clone();
        } else {
            newEvent = mEventsInteractor.getEvent(type);
        }

        long eventTime = DateUtils.convertStringAMPMToTime(startTime, mEventDay, mTimezone);

        newEvent.setStatus(ELDEvent.StatusCode.ACTIVE.getValue());
        newEvent.setEventCode(type.getCode());
        newEvent.setEventTime(eventTime);
        newEvent.setComment(comment);
        if (mELDEvent == null) {
            newEvent.setMobileTime(eventTime);
            mView.addEvent(newEvent);
        } else {
            mView.changeEvent(mELDEvent, newEvent);
        }
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
}
