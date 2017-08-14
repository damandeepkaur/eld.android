package com.bsmwireless.screens.editevent;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.domain.interactors.LoginUserInteractor;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.Calendar;
import java.util.TimeZone;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@ActivityScope
public class EditEventPresenter {

    private EditEventView mView;
    private CompositeDisposable mDisposables;
    private ELDEvent mELDEvent;
    private String mTimezone;
    private long mEventDay;
    private Calendar mCalendar;

    private LoginUserInteractor mLoginUserInteractor;
    private VehiclesInteractor mVehiclesInteractor;

    @Inject
    public EditEventPresenter(EditEventView view, LoginUserInteractor loginUserInteractor, VehiclesInteractor vehiclesInteractor) {
        mView = view;
        mDisposables = new CompositeDisposable();
        mLoginUserInteractor = loginUserInteractor;
        mVehiclesInteractor = vehiclesInteractor;
        mTimezone = TimeZone.getDefault().getID();
        mCalendar = Calendar.getInstance();

        Timber.d("CREATED");
    }

    public void onViewCreated() {
        mDisposables.add(mLoginUserInteractor.getTimezone()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timezone -> {
                    mTimezone = timezone;
                    mCalendar = Calendar.getInstance(TimeZone.getTimeZone(mTimezone));
                    mView.getExtrasFromIntent();
                }));
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
            newEvent = prepareNewELDEvent();
        }

        long eventTime = DateUtils.convertStringAMPMToTime(startTime, mEventDay, mTimezone);

        newEvent.setStatus(ELDEvent.StatusCode.ACTIVE.getValue());
        newEvent.setEventCode(type.getId());
        newEvent.setEventTime(eventTime);
        newEvent.setComment(comment);
        if (mELDEvent == null) {
            newEvent.setMobileTime(eventTime);
        }

        if (mELDEvent == null) {
            mView.addEvent(newEvent);
        } else {
            mView.changeEvent(mELDEvent, newEvent);
        }
    }

    public void setEvent(ELDEvent event) {
        mELDEvent = event;
        if (mELDEvent != null) {
            DutyType type = DutyType.getTypeById(event.getEventCode());
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

    private ELDEvent prepareNewELDEvent() {
        ELDEvent event = new ELDEvent();
        event.setStatus(ELDEvent.StatusCode.ACTIVE.getValue());
        event.setOrigin(2);
        event.setEventType(ELDEvent.EventType.DUTY_STATUS_CHANGING.getValue());
        event.setEventCode(DutyType.OFF_DUTY.getId());
        event.setEventTime(mEventDay);
        event.setEngineHours(0);
        event.setLat(0d);
        event.setLng(0d);
        event.setLocation("");
        event.setDistance(0);
        event.setMalfunction(false);
        event.setDiagnostic(false);
        event.setTimezone(mTimezone);
        event.setDriverId(mLoginUserInteractor.getDriverId());
        event.setBoxId(mVehiclesInteractor.getBoxId());
        event.setVehicleId(mVehiclesInteractor.getVehicleId());
        event.setMobileTime(mEventDay);
        return event;
    }
}
