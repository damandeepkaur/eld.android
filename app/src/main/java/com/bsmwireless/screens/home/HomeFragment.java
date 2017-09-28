package com.bsmwireless.screens.home;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bsmwireless.screens.common.BaseFragment;
import com.bsmwireless.widgets.alerts.DutyType;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class HomeFragment extends BaseFragment implements HomeView{

    @BindView(R.id.home_screen_current_status)
    TextView mCurrentStatus;

    @Inject
    HomePresenter mHomePresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void dutyStatusChanged(DutyType dutyType) {
        mCurrentStatus.setText(dutyType.getName());
        mCurrentStatus.setTextColor(ContextCompat.getColor(getContext(), dutyType.getColor()));
    }

    @Override
    public void startHoursOfService() {

    }

    @OnClick(R.id.home_screen_pre_trip)
    void onPreTripClick(){
        mHomePresenter.onPreTrip();
    }

    @OnClick(R.id.home_screen_post_trip)
    void onPostTripClick(){
        mHomePresenter.onPostTrip();
    }

    @OnClick(R.id.home_screen_inspections)
    void onInspectionsClick(){
        mHomePresenter.onInspections();
    }

    @OnClick(R.id.homeScreen_hours_of_service)
    void onHoursOfServiceClick(){
        mHomePresenter.onHoursOfService();
    }
}
