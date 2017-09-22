package com.bsmwireless.screens.navigation;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bsmwireless.common.App;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.screens.autologout.AutoDutyDialogActivity;
import com.bsmwireless.screens.common.menu.BaseMenuActivity;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.driverprofile.DriverProfileActivity;
import com.bsmwireless.screens.login.LoginActivity;
import com.bsmwireless.screens.navigation.dagger.DaggerNavigationComponent;
import com.bsmwireless.screens.navigation.dagger.NavigationModule;
import com.bsmwireless.screens.selectasset.SelectAssetActivity;
import com.bsmwireless.screens.settings.SettingsActivity;
import com.bsmwireless.widgets.snackbar.SnackBarLayout;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.bsmwireless.screens.autologout.AutoDutyDialogActivity.EXTRA_AUTO_DRIVING;
import static com.bsmwireless.screens.autologout.AutoDutyDialogActivity.EXTRA_AUTO_DRIVING_WITHOUT_CONFIRM;
import static com.bsmwireless.screens.autologout.AutoDutyDialogActivity.EXTRA_AUTO_ON_DUTY;

public final class NavigationActivity extends BaseMenuActivity implements OnNavigationItemSelectedListener, NavigateView, ViewPager.OnPageChangeListener {

    private static final int REQUEST_CODE_UPDATE_USER = 101;

    @BindView(R.id.navigation_drawer)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.navigation_tab_layout)
    TabLayout mTabLayout;

    @BindView(R.id.navigation_view_pager)
    ViewPager mViewPager;

    @BindView(R.id.navigation_snackbar)
    SnackBarLayout mSnackBarLayout;

    @Inject
    NavigationPresenter mPresenter;

    private NavigationAdapter mPagerAdapter;
    private SmoothActionBarDrawerToggle mDrawerToggle;
    private HeaderViewHolder mHeaderViewHolder;

    private Handler mHandler = new Handler();
    private Runnable mResetTimeTask = () -> mPresenter.onResetTime();

    private View.OnClickListener mOnAssetMenuClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerNavigationComponent.builder().appComponent(App.getComponent())
                .navigationModule(new NavigationModule(this)).build().inject(this);

        setContentView(R.layout.activity_navigation);
        mUnbinder = ButterKnife.bind(this);

        //TODO: waiting for UI
        //open(new HomeFragment(), false);

        initNavigation();

        mPresenter.onViewCreated();
        mPresenter.onResetTime();
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
                mViewPager.setCurrentItem(0, true);

                //TODO: waiting for UI
                /*Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.navigation_content);
                if (fragment == null) {
                    fragment = new HomeFragment();
                } else if (fragment instanceof HomeFragment) {
                    break;
                }
                open((BaseFragment) fragment, false);*/
                break;
            case R.id.nav_inspector_view:
                Toast.makeText(this, "Go to inspector screen", Toast.LENGTH_SHORT).show();
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

        mPagerAdapter = new NavigationAdapter(getApplicationContext(), getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(this);

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabTextColors(ContextCompat.getColor(this, R.color.accent_transparent), ContextCompat.getColor(this, R.color.accent));

        mSnackBarLayout.setHideableOnTouch(false);
    }

    //TODO: waiting for UI
    /*public void open(BaseFragment fragment, boolean useBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (useBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.replace(R.id.navigation_content, fragment).commit();
    }*/

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
        String boxString = (boxId > 0) ? getResources().getString(R.string.box) + " " + boxId
                : getResources().getString(R.string.select_asset_not_in_vehicle);
        mHeaderViewHolder.boxId.setText(boxString);
    }

    @Override
    public void setAssetsNumber(int assetsNum) {
        mHeaderViewHolder.assetNumber.setText(getResources().getQuantityString(
                R.plurals.assets, assetsNum, assetsNum));
    }

    @Override
    public SnackBarLayout getSnackBar() {
        return mSnackBarLayout;
    }

    @Override
    public void showReassignDialog(ELDEvent event) {
        showReassignEventDialog(event);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(mResetTimeTask);
        mViewPager.removeOnPageChangeListener(this);
        mPresenter.onDestroy();
        mHeaderViewHolder.unbind();
        super.onDestroy();
    }

    @Override
    protected BaseMenuPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mSnackBarLayout.reset().hideSnackbar();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void setResetTime(long time) {
        mHandler.removeCallbacks(mResetTimeTask);
        if (time == 0) {
            mHandler.post(mResetTimeTask);
        } else {
            mHandler.postAtTime(mResetTimeTask, time);
        }
    }

    @Override
    public void setAutoOnDuty() {
        Intent dialogIntent = new Intent(this, AutoDutyDialogActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.putExtra(EXTRA_AUTO_ON_DUTY, true);
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
