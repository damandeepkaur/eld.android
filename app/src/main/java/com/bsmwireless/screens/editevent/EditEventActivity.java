package com.bsmwireless.screens.editevent;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;

import com.bsmwireless.common.App;
import com.bsmwireless.common.utils.NetworkUtils;
import com.bsmwireless.data.network.RetrofitException;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.screens.common.menu.BaseMenuActivity;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.editevent.dagger.DaggerEditEventComponent;
import com.bsmwireless.screens.editevent.dagger.EditEventModule;
import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.snackbar.SnackBarLayout;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class EditEventActivity extends BaseMenuActivity implements EditEventView, AdapterView.OnItemSelectedListener {

    public final static String DAY_TIME_EXTRA = "day_time_extra";
    public final static String OLD_ELD_EVENT_EXTRA = "old_eld_event_extra";
    public final static String NEW_ELD_EVENT_EXTRA = "new_eld_event_extra";

    @Inject
    EditEventPresenter mPresenter;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.snackbar)
    SnackBarLayout mSnackBarLayout;
    @BindView(R.id.event_status)
    AppCompatSpinner mEventStatus;
    @BindView(R.id.start_time)
    TextInputEditText mStartTime;
    @BindView(R.id.comment)
    TextInputEditText mComment;
    @BindView(R.id.address)
    TextInputEditText mAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        DaggerEditEventComponent.builder().appComponent(App.getComponent()).editEventModule(new EditEventModule(this)).build().inject(this);
        mUnbinder = ButterKnife.bind(this);

        initToolbar();
        initStatusSpinner();
    }

    @OnClick(R.id.save_event)
    void onSaveEventButtonClicked() {
        DutyType type = (DutyType) mEventStatus.getSelectedItem();
        String startTime = mStartTime.getText().toString();
        String comment = mComment.getText().toString();

        mPresenter.onSaveClick(type, startTime, comment);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mPresenter.onViewCreated();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        mSnackBarLayout.reset();
        super.onDestroy();
    }

    @Override
    protected BaseMenuPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void setStartTime(String time) {
        mStartTime.setText(time);
    }

    @Override
    public void setStatus(DutyType type) {
        mEventStatus.setSelection(type.ordinal());
    }

    @Override
    public void setComment(String comment) {
        mComment.setText(comment);
    }

    @Override
    public void setAddress(String address) {
        mAddress.setText(address);
    }

    @Override
    public void openTimePickerDialog(TimePickerDialog.OnTimeSetListener listener, int hours, int minutes) {
        TimePickerDialog dialog = new TimePickerDialog(this, R.style.TimePicker, listener, hours, minutes, false);
        dialog.show();
    }

    @Override
    public void changeEvent(ArrayList<ELDEvent> newELDEvent) {
        Intent result = new Intent();
        result.putParcelableArrayListExtra(NEW_ELD_EVENT_EXTRA, newELDEvent);
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void showError(Error error) {
        mSnackBarLayout.setOnReadyListener(snackBar -> snackBar.reset()
                .setMessage(getString(error.getStringId()))
                .setHideableOnTimeout(SnackBarLayout.DURATION_LONG))
                .showSnackbar();
    }

    @Override
    public void showError(RetrofitException error) {
        mSnackBarLayout.setOnReadyListener(snackBar -> snackBar.reset()
                .setMessage(NetworkUtils.getErrorMessage(error, this))
                .setHideableOnTimeout(SnackBarLayout.DURATION_LONG))
                .showSnackbar();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        DutyType type = (DutyType) parent.getItemAtPosition(position);
        mEventStatus.getBackground().setColorFilter(ContextCompat.getColor(this, type.getColor()), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void getExtrasFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(OLD_ELD_EVENT_EXTRA)) {
                ELDEvent event = intent.getParcelableExtra(OLD_ELD_EVENT_EXTRA);
                mPresenter.setEvent(event);
            }
            if (intent.hasExtra(DAY_TIME_EXTRA)) {
                mPresenter.setDayTime(intent.getLongExtra(DAY_TIME_EXTRA, 0));
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @OnClick(R.id.start_time)
    void onStartTimeClick() {
        mPresenter.onStartTimeClick(mStartTime.getText().toString());
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initStatusSpinner() {
        //TODO: set correct types
        List<DutyType> types = DutyTypeManager.DRIVER_DUTY_EXTENDED_WITH_CLEAR;
        mEventStatus.setAdapter(new DutyTypeSpinnerAdapter(this, types));
        mEventStatus.setOnItemSelectedListener(this);
    }
}
