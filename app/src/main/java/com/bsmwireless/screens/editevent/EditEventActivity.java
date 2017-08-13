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
import android.widget.ArrayAdapter;

import com.bsmwireless.common.App;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.screens.common.BaseMenuActivity;
import com.bsmwireless.screens.editevent.dagger.DaggerEditEventComponent;
import com.bsmwireless.screens.editevent.dagger.EditEventModule;
import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.snackbar.SnackBarLayout;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditEventActivity extends BaseMenuActivity implements EditEventView, AdapterView.OnItemSelectedListener {

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
        initSnackbar();
        initStatusSpinner();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mPresenter.onViewCreated();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void setStartTime(String time) {
        mStartTime.setText(time);
    }

    @Override
    public void setStatus(DutyType type) {
        mEventStatus.setSelection(type.getId() - 1);
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
    public void addEvent(ELDEvent newELDEvent) {
        Intent result = new Intent();
        result.putExtra(NEW_ELD_EVENT_EXTRA, newELDEvent);
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void changeEvent(ELDEvent oldEvent, ELDEvent newELDEvent) {
        Intent result = new Intent();
        result.putExtra(NEW_ELD_EVENT_EXTRA, newELDEvent);
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String name = (String) parent.getItemAtPosition(position);
        DutyType type = DutyType.getTypeByName(this, name);
        mEventStatus.getBackground().setColorFilter(ContextCompat.getColor(this, DutyType.getColorById(type.getId())), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void getExtrasFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(OLD_ELD_EVENT_EXTRA)) {
                ELDEvent event = intent.getParcelableExtra(OLD_ELD_EVENT_EXTRA);
                mPresenter.onEvent(event);
            }
            if (intent.hasExtra(DAY_TIME_EXTRA)) {
                mPresenter.onDayTime(intent.getLongExtra(DAY_TIME_EXTRA, 0));
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

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

    private void initSnackbar() {
        mSnackBarLayout.setPositiveLabel(getString(R.string.edit_event_save), v -> {
            DutyType type = DutyType.getTypeByName(this, (String) mEventStatus.getSelectedItem());
            String startTime = mStartTime.getText().toString();
            String comment = mComment.getText().toString();

            mPresenter.onSaveClick(type, startTime, comment);
        }).setHideableOnTouch(false)
        .showSnackbar();
    }

    private void initStatusSpinner() {
        mEventStatus.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                android.R.id.text1,
                DutyType.getNames(this)));
        mEventStatus.setOnItemSelectedListener(this);
    }
}
