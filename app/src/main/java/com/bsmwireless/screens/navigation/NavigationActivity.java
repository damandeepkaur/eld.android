package com.bsmwireless.screens.navigation;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bsmwireless.common.App;
import com.bsmwireless.screens.common.BaseActivity;
import com.bsmwireless.screens.common.BaseFragment;
import com.bsmwireless.screens.home.HomeFragment;
import com.bsmwireless.screens.login.LoginActivity;
import com.bsmwireless.screens.navigation.dagger.DaggerNavigationComponent;
import com.bsmwireless.screens.navigation.dagger.NavigationModule;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NavigationActivity extends BaseActivity implements OnNavigationItemSelectedListener, NavigateView {
    @BindView(R.id.navigation_drawer)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    @BindView(R.id.navigation_toolbar)
    Toolbar mToolbar;

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
        ButterKnife.bind(this);
        open(new HomeFragment(), false);
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
                open(new HomeFragment(), false);
                break;
            case R.id.nav_inspector_view:
                Toast.makeText(this, "Go to inspector screen", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_help:
                Toast.makeText(this, "Go to help screen", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_driver_profile:
                Toast.makeText(this, "Go to driver profile screen", Toast.LENGTH_SHORT).show();
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, 0, 0);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        View header = mNavigationView.getHeaderView(0);
        mHeaderViewHolder = new HeaderViewHolder(header);
    }

    public void open(BaseFragment fragment, boolean useBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (useBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.replace(R.id.navigation_content, fragment).commit();
    }

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
        mHeaderViewHolder.boxId.setText(getResources().getString(R.string.box) + " " + boxId);
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

        HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
