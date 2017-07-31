package com.bsmwireless.screens.common;

import android.view.Menu;
import android.view.MenuItem;

import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.alerts.ELDType;
import com.bsmwireless.widgets.alerts.OccupancyType;

import app.bsmuniversal.com.R;

public abstract class BaseMenuActivity extends BaseActivity {
    private MenuItem mELDItem;
    private MenuItem mDutyItem;
    private MenuItem mOccupancyItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alert, menu);

        mELDItem = menu.findItem(R.id.action_eld);
        mDutyItem = menu.findItem(R.id.action_duty);
        mOccupancyItem = menu.findItem(R.id.action_occupancy);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_eld:
                break;
            case R.id.action_duty:
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

    public void onHomePress() {
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
            mDutyItem.getIcon().setLevel(type.ordinal());
            mDutyItem.setTitle(getString(type.getName()));
        }
    }

    public void setOccupancyType(OccupancyType type) {
        if (mOccupancyItem != null) {
            mOccupancyItem.getIcon().setLevel(type.ordinal());
            mOccupancyItem.setTitle(getString(type.getName()));
        }
    }
}
