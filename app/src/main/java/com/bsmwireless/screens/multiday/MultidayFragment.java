package com.bsmwireless.screens.multiday;

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
import com.bsmwireless.screens.multiday.dagger.DaggerMultidayComponent;
import com.bsmwireless.screens.multiday.dagger.MultidayModule;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public final class MultidayFragment extends BaseFragment implements MultidayView, AdapterView.OnItemSelectedListener {

    public Unbinder mUnbinder;

    @BindView(R.id.multiday_selector)
    AppCompatSpinner mMultiDaySelector;
    @BindView(R.id.total_off_duty_time)
    TextView mTotalTimeOffDuty;
    @BindView(R.id.total_sleeping_time)
    TextView mTotalTimeSleeping;
    @BindView(R.id.total_driving_time)
    TextView mTotalTimeDriving;
    @BindView(R.id.total_on_duty_time)
    TextView mTotalTimeOnDuty;
    @BindView(R.id.multiday_recycler_view)
    RecyclerView mRecyclerView;

    @Inject
    MultidayPresenter mPresenter;

    private MultidayAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_multiday, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        DaggerMultidayComponent.builder().appComponent(App.getComponent()).multidayModule(new MultidayModule(this)).build().inject(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecyclerView();

        mMultiDaySelector.setOnItemSelectedListener(this);

        mPresenter.onViewCreated();
    }

    @Override
    public void onDestroyView() {
        mPresenter.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void setItems(List<MultidayItemModel> items) {
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

    @Override
    public int getDayCount() {
        String item = (String) mMultiDaySelector.getSelectedItem();
        return parseDayCount(item);
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MultidayAdapter(new ArrayList<>());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        int daysCount = parseDayCount(item);
        if (daysCount > 0) {
            mPresenter.getItems(daysCount);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private int parseDayCount(String item) {
        Pattern pattern = Pattern.compile("[^0-9]");
        Matcher matcher = pattern.matcher(item);
        String number = matcher.replaceAll("");
        return Integer.parseInt(number);
    }
}
