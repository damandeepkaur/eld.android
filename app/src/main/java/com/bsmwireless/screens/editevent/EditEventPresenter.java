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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        } else {
            newEvent = mEventsInteractor.getEvent(type);
        }

        if (type.equals(DutyType.PERSONAL_USE) || type.equals(DutyType.YARD_MOVES)) {
            ELDEvent indicationDutyEvent = mEventsInteractor.getEvent(type);
            indicationDutyEvent.setEventType(type.getType());
            indicationDutyEvent.setEventCode(type.getCode());
            indicationDutyEvent.setEventTime(eventTime);

            newEvent.setStatus(ELDEvent.StatusCode.ACTIVE.getValue());
            newEvent.setEventTime(eventTime);
            newEvent.setComment(comment);
            if (type.equals(DutyType.PERSONAL_USE)) {
                newEvent.setEventCode(DutyType.OFF_DUTY.getCode());
            } else if (type.equals(DutyType.YARD_MOVES)) {
                newEvent.setEventCode(DutyType.ON_DUTY.getCode());
            }

            if (mELDEvent == null) {
                newEvent.setMobileTime(eventTime);
                indicationDutyEvent.setMobileTime(eventTime);
            } else {
                indicationDutyEvent.setMobileTime(mELDEvent.getMobileTime());
            }

            // TODO: remove if we need not auto generate clear event
            // Auto generating clear event
            /*mDisposables.add(mEventsInteractor.getLatestActiveDutyEventFromDB(eventTime)
                                              .subscribeOn(Schedulers.io())
                                              .observeOn(AndroidSchedulers.mainThread())
                                              .subscribe(latestEvents -> {
                                                  for (ELDEvent event: latestEvents) {
                                                      DutyType prevDutyType = DutyType.getTypeByCode(event.getEventType(), event.getEventCode());
                                                      if (prevDutyType.equals(DutyType.YARD_MOVES) || prevDutyType.equals(DutyType.PERSONAL_USE)) {
                                                          ELDEvent clearEvent = prepareNewELDEvent();
                                                          clearEvent.setEventType(DutyType.CLEAR.getType());
                                                          clearEvent.setEventCode(DutyType.CLEAR.getCode());
                                                          clearEvent.setEventTime(eventTime);
                                                          if (mELDEvent == null) {
                                                              clearEvent.setMobileTime(eventTime);
                                                          } else {
                                                              clearEvent.setMobileTime(mELDEvent.getMobileTime());
                                                          }
                                                          events.add(clearEvent);
                                                      }
                                                  }

                                                  events.add(newEvent);
                                                  events.add(indicationDutyEvent);

                                                  mView.changeEvent(events);
                                              }, throwable -> {
                                                  if (throwable instanceof RetrofitException) {
                                                      mView.showError((RetrofitException) throwable);
                                                  } else {
                                                      mView.showError(EditEventView.Error.SERVER_ERROR);
                                                  }
                                              }));*/

            events.add(newEvent);
            events.add(indicationDutyEvent);
        } else {
            newEvent.setStatus(ELDEvent.StatusCode.ACTIVE.getValue());
            newEvent.setEventType(type.getType());
            newEvent.setEventCode(type.getCode());
            newEvent.setEventTime(eventTime);
            newEvent.setComment(comment);

            if (mELDEvent == null) {
                newEvent.setMobileTime(eventTime);
            }

            // TODO: remove if we need not auto generate clear event
            // Auto generating clear event
            /*mDisposables.add(mEventsInteractor.getLatestActiveDutyEventFromDB(eventTime)
                                              .subscribeOn(Schedulers.io())
                                              .observeOn(AndroidSchedulers.mainThread())
                                              .subscribe(latestEvents -> {
                                                  for (ELDEvent event: latestEvents) {
                                                      DutyType prevDutyType = DutyType.getTypeByCode(event.getEventType(), event.getEventCode());
                                                      if (prevDutyType.equals(DutyType.YARD_MOVES) || prevDutyType.equals(DutyType.PERSONAL_USE)) {
                                                          ELDEvent clearEvent = prepareNewELDEvent();
                                                          clearEvent.setEventType(DutyType.CLEAR.getType());
                                                          clearEvent.setEventCode(DutyType.CLEAR.getCode());
                                                          clearEvent.setEventTime(eventTime);
                                                          if (mELDEvent == null) {
                                                              clearEvent.setMobileTime(eventTime);
                                                          } else {
                                                              clearEvent.setMobileTime(mELDEvent.getMobileTime());
                                                          }
                                                          events.add(clearEvent);
                                                      }
                                                  }

                                                  events.add(newEvent);

                                                  mView.changeEvent(events);
                                              }, throwable -> {
                                                  if (throwable instanceof RetrofitException) {
                                                      mView.showError((RetrofitException) throwable);
                                                  } else {
                                                      mView.showError(EditEventView.Error.SERVER_ERROR);
                                                  }
                                              }));*/
            events.add(newEvent);
        }

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
        Pattern pattern = Pattern.compile("[^A-Za-z0-9]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(comment);
        if (matcher.find()) {
            return EditEventView.Error.INVALID_COMMENT;
        }
        return EditEventView.Error.VALID_COMMENT;
    }
}
