package com.bsmwireless.screens.common.menu;

import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.screens.common.BaseActivity;
import com.bsmwireless.screens.diagnostic.DiagnosticDialog;
import com.bsmwireless.screens.diagnostic.MalfunctionDialog;
import com.bsmwireless.screens.switchdriver.DriverDialog;
import com.bsmwireless.screens.switchdriver.SwitchDriverDialog;
import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.alerts.ELDType;
import com.bsmwireless.widgets.alerts.OccupancyType;

import java.util.ArrayList;

import app.bsmuniversal.com.R;

public abstract class BaseMenuActivity extends BaseActivity implements BaseMenuView {
    private MenuItem mELDItem;
    private MenuItem mDutyItem;
    private MenuItem mOccupancyItem;
    private MenuItem mMalfunctionItem;

    protected AlertDialog mDutyDialog;

    protected DriverDialog mSwitchDriverDialog;

    protected abstract BaseMenuPresenter getPresenter();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alert, menu);

        mELDItem = menu.findItem(R.id.action_diagnostic);
        mDutyItem = menu.findItem(R.id.action_duty);
        mOccupancyItem = menu.findItem(R.id.action_occupancy);
        mMalfunctionItem = menu.findItem(R.id.action_malfunction);

        mSwitchDriverDialog = new SwitchDriverDialog(this);

        getPresenter().onMenuCreated();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_diagnostic:
                getPresenter().showDiagnosticEvents();
                break;
            case R.id.action_duty:
                getPresenter().onChangeDutyClick();
                break;
            case R.id.action_occupancy:
                showSwitchDriverDialog();
                break;
            case android.R.id.home: {
                onHomePress();
                break;
            }
            case R.id.action_malfunction:
                getPresenter().showMalfunctionEvent();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDutyDialog != null) {
            mDutyDialog.dismiss();
        }
    }

    @Override
    public void setELDType(ELDType type) {
        if (mELDItem != null) {
            mELDItem.getIcon().setLevel(type.ordinal());
            mELDItem.setTitle(getString(type.getName()));
        }
    }

    @Override
    public void setDutyType(DutyType type) {
        if (mDutyItem != null) {
            mDutyItem.setIcon(type.getIcon());
            mDutyItem.setTitle(getString(type.getName()));
        }
    }

    @Override
    public void setOccupancyType(OccupancyType type) {
        if (mOccupancyItem != null) {
            mOccupancyItem.getIcon().setLevel(type.ordinal());
            mOccupancyItem.setTitle(getString(type.getName()));
        }
    }

    @Override
    public void showSwitchDriverDialog() {
        if (mSwitchDriverDialog != null) {
            mSwitchDriverDialog.show();
        }
    }

    @Override
    public void showMalfunctionDialog() {
        MalfunctionDialog.newInstance().show(getSupportFragmentManager(), "");
    }

    @Override
    public void showDiagnosticEvents() {
        DiagnosticDialog.newInstance().show(getSupportFragmentManager(), "");
    }

    @Override
    public void changeDutyType(DutyType dutyType) {
        getPresenter().onDutyChanged(dutyType);
    }

    protected void onHomePress() {
        onBackPressed();
    }

    @Override
    public void showDutyTypeDialog(DutyType current) {
        if (mDutyDialog != null) {
            mDutyDialog.dismiss();
        }

        //TODO: set correct types
        DutyType[] dutyTypes = DutyTypeManager.DRIVER_DUTY_EXTENDED;

        ArrayList<BaseMenuAdapter.DutyItem> dutyItems = new ArrayList<>();
        for (DutyType dutyType : dutyTypes) {
            boolean isEnabled = current != dutyType;
            switch (dutyType) {
                case ON_DUTY:
                    isEnabled &= current != DutyType.PERSONAL_USE;
                    break;

                case OFF_DUTY:
                    isEnabled &= current != DutyType.YARD_MOVES;
                    break;

                case DRIVING:
                    isEnabled &= current != DutyType.YARD_MOVES & current != DutyType.PERSONAL_USE & getPresenter().isUserDriver();
                    break;

                case SLEEPER_BERTH:
                    isEnabled &= current != DutyType.YARD_MOVES & current != DutyType.PERSONAL_USE;
                    break;

                case PERSONAL_USE:
                    isEnabled &= current == DutyType.OFF_DUTY;
                    break;

                case YARD_MOVES:
                    isEnabled &= current == DutyType.ON_DUTY;
                    break;
            }

            dutyItems.add(new BaseMenuAdapter.DutyItem(dutyType, isEnabled));
        }

        ArrayAdapter<BaseMenuAdapter.DutyItem> arrayAdapter = new BaseMenuAdapter(this, dutyItems);

        mDutyDialog = new AlertDialog.Builder(this)
                .setAdapter(arrayAdapter, (dialog, which) -> changeDutyType(dutyItems.get(which).getDutyType()))
                .setCancelable(true)
                .show();
    }

    @Override
    public void showNotInVehicleDialog() {
        if (mDutyDialog != null) {
            mDutyDialog.dismiss();
        }

        mDutyDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.not_in_vehicle_dialog_title)
                .setMessage(R.string.not_in_vehicle_dialog_message)
                .setPositiveButton(R.string.not_in_vehicle_accept, null)
                .setCancelable(true)
                .show();
    }
}
