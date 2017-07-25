package com.bsmwireless.screens.navigation;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bsmwireless.common.App;
import com.bsmwireless.screens.common.BaseMenuActivity;
import com.bsmwireless.screens.driverprofile.DriverProfileActivity;
import com.bsmwireless.screens.login.LoginActivity;
import com.bsmwireless.screens.navigation.dagger.DaggerNavigationComponent;
import com.bsmwireless.screens.navigation.dagger.NavigationModule;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NavigationActivity extends BaseMenuActivity implements OnNavigationItemSelectedListener, NavigateView {

    private static final int REQUEST_CODE_UPDATE_USER = 101;

    @BindView(R.id.navigation_drawer)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    @BindView(R.id.navigation_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.navigation_tab_layout)
    TabLayout mTabLayout;

    @BindView(R.id.navigation_view_pager)
    ViewPager mViewPager;

    private NavigationAdapter mPagerAdapter;

    @Inject
    NavigationPresenter mPresenter;

    private ActionBarDrawerToggle mDrawerToggle;
    private HeaderViewHolder mHeaderViewHolder;

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
                startActivityForResult(new Intent(this, DriverProfileActivity.class), REQUEST_CODE_UPDATE_USER);
                break;
            case R.id.nav_settings:
                Toast.makeText(this, "Go to settings screen", Toast.LENGTH_SHORT).show();
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

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, 0, 0);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        View header = mNavigationView.getHeaderView(0);
        mHeaderViewHolder = new HeaderViewHolder(header);

        mPagerAdapter = new NavigationAdapter(getApplicationContext(), getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabTextColors(ContextCompat.getColor(this, R.color.accent_transparent), ContextCompat.getColor(this, R.color.accent));
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

    protected static class HeaderViewHolder {
        @BindView(R.id.driver_name)
        TextView driverName;

        @BindView(R.id.co_drivers_number)
        TextView coDriversNumber;

        @BindView(R.id.box_id)
        TextView boxId;

        @BindView(R.id.assets_number)
        TextView assetNumber;

        private Unbinder mUnbinder;

        HeaderViewHolder(View view) {
            mUnbinder = ButterKnife.bind(this, view);
        }

        void unbind() {
            mUnbinder.unbind();
        }
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        mHeaderViewHolder.unbind();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_UPDATE_USER: {
                mPresenter.onUserUpdated();
                break;
            }
            default: {
                break;
            }
        }
    }
}
