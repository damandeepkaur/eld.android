package com.bsmwireless.screens.common.menu;

import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.bsmwireless.data.storage.DutyManager;
import com.bsmwireless.screens.common.BaseActivity;
import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.alerts.ELDType;
import com.bsmwireless.widgets.alerts.OccupancyType;

import app.bsmuniversal.com.R;

public abstract class BaseMenuActivity extends BaseActivity implements BaseMenuView {
    private MenuItem mELDItem;
    private MenuItem mDutyItem;
    private MenuItem mOccupancyItem;

    protected AlertDialog mDutyDialog;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alert, menu);

        mELDItem = menu.findItem(R.id.action_eld);
        mDutyItem = menu.findItem(R.id.action_duty);
        mOccupancyItem = menu.findItem(R.id.action_occupancy);

        initDialog();

        getPresenter().onMenuCreated();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_eld:
                break;
            case R.id.action_duty:
                mDutyDialog.show();
                break;
            case R.id.action_occupancy:
                break;
            case android.R.id.home: {
                onHomePress();
                break;
            }
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

    protected abstract BaseMenuPresenter getPresenter();

    protected void onHomePress() {
        onBackPressed();
    }

    public void setELDType(ELDType type) {
        if (mELDItem != null) {
            mELDItem.getIcon().setLevel(type.ordinal());
            mELDItem.setTitle(getString(type.getName()));
        }
    }

    public void setDutyType(DutyType type) {
        if (mDutyItem != null) {
            mDutyItem.setIcon(type.getIcon());
            mDutyItem.setTitle(getString(type.getName()));
        }
    }

    public void setOccupancyType(OccupancyType type) {
        if (mOccupancyItem != null) {
            mOccupancyItem.getIcon().setLevel(type.ordinal());
            mOccupancyItem.setTitle(getString(type.getName()));
        }
    }

    @Override
    public void showDutyDialog() {
        if (mDutyDialog != null) {
            mDutyDialog.show();
        }
    }

    protected void initDialog() {
        //TODO: set correct types
        DutyType[] types = DutyManager.DRIVER_DUTY_EXTENDED;
        ArrayAdapter<DutyType> arrayAdapter = new BaseMenuAdapter(this, types);

        mDutyDialog = new AlertDialog.Builder(this)
                .setAdapter(arrayAdapter, (dialog, which) -> getPresenter().onDutyChanged(types[which]))
                .setCancelable(true)
                .create();
    }
}
