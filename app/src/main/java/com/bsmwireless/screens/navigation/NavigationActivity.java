package com.bsmwireless.screens.navigation;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bsmwireless.common.App;
import com.bsmwireless.screens.autologout.AutoDutyDialogActivity;
import com.bsmwireless.screens.carrieredit.CarrierEditActivity;
import com.bsmwireless.screens.common.BaseFragment;
import com.bsmwireless.screens.common.menu.BaseMenuActivity;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.driverprofile.DriverProfileActivity;
import com.bsmwireless.screens.home.HomeFragment;
import com.bsmwireless.screens.login.LoginActivity;
import com.bsmwireless.screens.navigation.dagger.DaggerNavigationComponent;
import com.bsmwireless.screens.navigation.dagger.NavigationModule;
import com.bsmwireless.screens.roadsidenavigation.RoadsideNavigationActivity;
import com.bsmwireless.screens.selectasset.SelectAssetActivity;
import com.bsmwireless.screens.settings.SettingsActivity;
import com.bsmwireless.screens.transfer.TransferActivity;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.bsmwireless.screens.autologout.AutoDutyDialogActivity.EXTRA_AUTO_DRIVING;
import static com.bsmwireless.screens.autologout.AutoDutyDialogActivity.EXTRA_AUTO_DRIVING_WITHOUT_CONFIRM;
import static com.bsmwireless.screens.autologout.AutoDutyDialogActivity.EXTRA_AUTO_ON_DUTY;
import static com.bsmwireless.screens.autologout.AutoDutyDialogActivity.EXTRA_AUTO_ON_DUTY_TIME;

public final class NavigationActivity extends BaseMenuActivity implements
        OnNavigationItemSelectedListener, NavigateView {

    private static final int REQUEST_CODE_UPDATE_USER = 101;

    @BindView(R.id.navigation_drawer)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Inject
    NavigationPresenter mPresenter;

    private SmoothActionBarDrawerToggle mDrawerToggle;
    private HeaderViewHolder mHeaderViewHolder;

    private View.OnClickListener mOnAssetMenuClickListener;

    private AlertDialog mAlertDialog;

    public static Intent createIntent(Context context) {
        return new Intent(context, NavigationActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerNavigationComponent.builder().appComponent(App.getComponent())
                .navigationModule(new NavigationModule(this)).build().inject(this);

        setContentView(R.layout.activity_navigation);
        ButterKnife.bind(this);

        initNavigation();
        open(HomeFragment.newInstance(), false);

        mPresenter.onViewCreated();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                break;
            case R.id.nav_inspector_view:
                mDrawerToggle.runWhenIdle(() -> startActivity(new Intent(this, RoadsideNavigationActivity.class)));
                break;
            case R.id.nav_transfer_view:
                mDrawerToggle.runWhenIdle(() -> startActivity(new Intent(this, TransferActivity.class)));
                break;
            case R.id.nav_help:
                Toast.makeText(this, "Go to help screen", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_driver_profile:
                mDrawerToggle.runWhenIdle(() -> startActivityForResult(new Intent(this, DriverProfileActivity.class), REQUEST_CODE_UPDATE_USER));
                break;
            case R.id.nav_settings:
                mDrawerToggle.runWhenIdle(() -> startActivity(new Intent(this, SettingsActivity.class)));
                break;
            case R.id.nav_logout:
                mPresenter.onLogoutItemSelected();
                break;
            case R.id.nav_carrier_edit:
                mDrawerToggle.runWhenIdle(() -> goToCarrierEditScreen());
                break;
            default:
                break;
        }

        mDrawerLayout.closeDrawers();
        return true;
    }

    private void initNavigation() {
        mNavigationView.setNavigationItemSelectedListener(this);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(R.string.menu_home);
        }

        mDrawerToggle = new SmoothActionBarDrawerToggle(this, mDrawerLayout, mToolbar, 0, 0);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        View header = mNavigationView.getHeaderView(0);
        mOnAssetMenuClickListener = v -> goToSelectAssetScreen();
        mHeaderViewHolder = new HeaderViewHolder(header, mOnAssetMenuClickListener);
    }

    public void open(BaseFragment fragment, boolean useBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (useBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.replace(R.id.fragmentContainer, fragment).commit();
    }

    @Override
    public void goToLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void goToSelectAssetScreen() {
        Intent intent = new Intent(this, SelectAssetActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void goToCarrierEditScreen() {
        //TODO: ea_235
        Intent intent = new Intent(this, CarrierEditActivity.class);
        startActivity(intent);
    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setDriverName(String name) {
        mHeaderViewHolder.driverName.setText(name);
    }

    @Override
    public void setCoDriversNumber(int coDriverNum) {
        mHeaderViewHolder.coDriversNumber.setText(getResources().getQuantityString(
                R.plurals.co_drivers, coDriverNum, coDriverNum));
    }

    @Override
    public void setBoxId(int boxId) {
        String boxString = (boxId > 0) ? getResources().getString(R.string.box, boxId)
                : getResources().getString(R.string.select_asset_not_in_vehicle);
        mHeaderViewHolder.boxId.setText(boxString);
    }

    @Override
    public void setAssetsNumber(int assetsNum) {
        mHeaderViewHolder.assetNumber.setText(getResources().getQuantityString(
                R.plurals.assets, assetsNum, assetsNum));
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        mHeaderViewHolder.unbind();
        super.onDestroy();
    }

    @Override
    protected BaseMenuPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void setAutoOnDuty(long stoppedTime) {
        Intent dialogIntent = new Intent(this, AutoDutyDialogActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.putExtra(EXTRA_AUTO_ON_DUTY, true);
        dialogIntent.putExtra(EXTRA_AUTO_ON_DUTY_TIME, stoppedTime);
        startActivity(dialogIntent);
    }

    @Override
    public void setAutoDriving() {
        Intent dialogIntent = new Intent(this, AutoDutyDialogActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.putExtra(EXTRA_AUTO_DRIVING, true);
        startActivity(dialogIntent);
    }

    @Override
    public void setAutoDrivingWithoutConfirm() {
        Intent dialogIntent = new Intent(this, AutoDutyDialogActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.putExtra(EXTRA_AUTO_DRIVING_WITHOUT_CONFIRM, true);
        startActivity(dialogIntent);
    }

    @Override
    public void showUnassignedDialog() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }

        mAlertDialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme_Positive)
                .setTitle(R.string.carrier_edit_dialog_title)
                .setMessage(R.string.carrier_edit_dialog_message)
                .setCancelable(false)
                .setPositiveButton(R.string.carrier_edit_dialog_ok, (dialog, which) -> {
                    goToCarrierEditScreen();
                    dialog.dismiss();
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> mAlertDialog.dismiss())
                .show();
    }

    protected static final class HeaderViewHolder {
        @BindView(R.id.driver_name)
        TextView driverName;

        @BindView(R.id.co_drivers_number)
        TextView coDriversNumber;

        @BindView(R.id.box_id)
        TextView boxId;

        @BindView(R.id.assets_number)
        TextView assetNumber;

        private Unbinder mUnbinder;

        HeaderViewHolder(View view, View.OnClickListener listener) {
            mUnbinder = ButterKnife.bind(this, view);

            boxId.setOnClickListener(listener);
        }

        void unbind() {
            mUnbinder.unbind();
        }
    }

    private static final class SmoothActionBarDrawerToggle extends ActionBarDrawerToggle {

        private Runnable mRunnable;

        public SmoothActionBarDrawerToggle(AppCompatActivity activity, DrawerLayout drawerLayout, Toolbar toolbar, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
            super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            super.onDrawerStateChanged(newState);
            if (mRunnable != null && newState == DrawerLayout.STATE_IDLE) {
                mRunnable.run();
                mRunnable = null;
            }
        }

        public void runWhenIdle(Runnable runnable) {
            this.mRunnable = runnable;
        }
    }
}
