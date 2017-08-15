package com.bsmwireless.screens.multyday;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.bsmwireless.common.App;
import com.bsmwireless.screens.common.BaseFragment;
import com.bsmwireless.screens.multyday.dagger.DaggerMultydayComponent;
import com.bsmwireless.screens.multyday.dagger.MultydayModule;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MultydayFragment extends BaseFragment implements MultydayView, AdapterView.OnItemSelectedListener {

    public Unbinder mUnbinder;

    @BindView(R.id.multyday_selector)
    AppCompatSpinner mMultyDaySelector;
    @BindView(R.id.total_off_duty_time)
    TextView mTotalTimeOffDuty;
    @BindView(R.id.total_sleeping_time)
    TextView mTotalTimeSleeping;
    @BindView(R.id.total_driving_time)
    TextView mTotalTimeDriving;
    @BindView(R.id.total_on_duty_time)
    TextView mTotalTimeOnDuty;
    @BindView(R.id.multyday_recycler_view)
    RecyclerView mRecyclerView;

    @Inject
    MultydayPresenter mPresenter;

    private MultydayAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_multyday, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        DaggerMultydayComponent.builder().appComponent(App.getComponent()).multydayModule(new MultydayModule(this)).build().inject(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecyclerView();

        mMultyDaySelector.setOnItemSelectedListener(this);

        mPresenter.onViewCreated();
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        mPresenter.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void setItems(List<MultydayItemModel> items) {
        mAdapter.updateItems(items);
    }

    @Override
    public void setTotalOffDuty(String time) {
        mTotalTimeOffDuty.setText(time);
    }

    @Override
    public void setTotalSleeping(String time) {
        mTotalTimeSleeping.setText(time);
    }

    @Override
    public void setTotalDriving(String time) {
        mTotalTimeDriving.setText(time);
    }

    @Override
    public void setTotalOnDuty(String time) {
        mTotalTimeOnDuty.setText(time);
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MultydayAdapter(new ArrayList<>());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        Pattern pattern = Pattern.compile("[^0-9]");
        Matcher matcher = pattern.matcher(item);
        String number = matcher.replaceAll("");
        int daysCount = Integer.parseInt(number);
        if (daysCount > 0) {
            mPresenter.getItems(daysCount);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
